package com.hirno.gif.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hirno.gif.data.badRequest
import com.hirno.gif.data.message
import com.hirno.gif.data.source.RandomDataSource
import com.hirno.gif.data.source.remote.FakeRandomDataSource
import com.hirno.gif.data.testResponse
import com.hirno.gif.di.testAppModules
import com.hirno.gif.model.state.RandomScreenEvent
import com.hirno.gif.model.state.RandomScreenState
import com.hirno.gif.util.MainDispatcherRule
import com.hirno.gif.util.getOrAwaitValue
import com.hirno.gif.util.skip
import kotlinx.coroutines.CoroutineDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class RandomViewModelTest : KoinTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        testAppModules(
            module {
                single<CoroutineDispatcher>(
                    qualifier = named("IODispatcher")
                ) {
                    mainDispatcherRule.testDispatcher
                }
                single { SavedStateHandle() }
                single<RandomDataSource> { FakeRandomDataSource }
            }
        )
    }

    private val viewModel: RandomViewModel by inject()
    private val savedState: SavedStateHandle by inject()

    @Before
    fun setup() {
        FakeRandomDataSource.reset()
    }

    @Test
    fun `test loading view state`() {
        val viewState = viewModel.obtainState.getOrAwaitValue {
            viewModel.event(RandomScreenEvent.ScreenLoad)
        }

        assertEquals(
            expected = RandomScreenState.Loading,
            actual = viewState,
        )
    }

    @Test
    fun `test success view state`() {
        FakeRandomDataSource.model = testResponse

        val response = viewModel.obtainState
            .skip { it is RandomScreenState.Loading }
            .getOrAwaitValue {
                viewModel.event(RandomScreenEvent.ScreenLoad)
            }

        assertEquals(
            expected = RandomScreenState.Success(testResponse.data),
            actual = response,
        )
    }

    @Test
    fun `test error view state`() {
        val error = badRequest.message
        FakeRandomDataSource.errorMessage = error

        val response = viewModel.obtainState
            .skip { it is RandomScreenState.Loading }
            .getOrAwaitValue {
                viewModel.event(RandomScreenEvent.ScreenLoad)
            }

        assertEquals(
            expected = RandomScreenState.Error(text = error),
            actual = response,
        )
    }

    @Test
    fun `test SwipeToRefresh event`() {
        viewModel.event(RandomScreenEvent.ScreenLoad)

        var response = viewModel.obtainState
            .skip { it is RandomScreenState.Success }
            .getOrAwaitValue {
                viewModel.event(RandomScreenEvent.SwipeToRefresh)
            }

        assertEquals(
            expected = RandomScreenState.Loading,
            actual = response,
        )

        response = viewModel.obtainState.getOrAwaitValue()

        assertEquals(
            expected = RandomScreenState.Success(testResponse.data),
            actual = response,
        )
    }

    @Test
    fun `test SwipeToRefresh event after error`() {
        FakeRandomDataSource.errorMessage = badRequest.message

        viewModel.event(RandomScreenEvent.ScreenLoad)

        FakeRandomDataSource.reset()

        var response = viewModel.obtainState
            .skip { it is RandomScreenState.Error }
            .getOrAwaitValue {
                viewModel.event(RandomScreenEvent.SwipeToRefresh)
            }

        assertEquals(
            expected = RandomScreenState.Loading,
            actual = response,
        )

        response = viewModel.obtainState.getOrAwaitValue()

        assertEquals(
            expected = RandomScreenState.Success(testResponse.data),
            actual = response,
        )
    }

    @Test
    fun `test saved error state`() {
        val error = badRequest.message
        FakeRandomDataSource.errorMessage = error

        viewModel.event(RandomScreenEvent.ScreenLoad)

        assertEquals(
            expected = RandomScreenState.Error(text = error),
            actual = savedState["viewState"],
        )
    }

    @Test
    fun `test saved success state`() {
        FakeRandomDataSource.model = testResponse

        viewModel.event(RandomScreenEvent.ScreenLoad)

        assertEquals(
            expected = RandomScreenState.Success(testResponse.data),
            actual = savedState["viewState"],
        )
    }

    @Test
    fun `test changing failed response after calling ScreenLoad again`() {
        FakeRandomDataSource.errorMessage = badRequest.message

        viewModel.event(RandomScreenEvent.ScreenLoad)

        FakeRandomDataSource.apply {
            reset()
            model = testResponse
        }

        viewModel.event(RandomScreenEvent.ScreenLoad)

        val response = viewModel.obtainState
            .skip { it is RandomScreenState.Loading }
            .getOrAwaitValue()

        assertEquals(
            expected = RandomScreenState.Success(testResponse.data),
            actual = response,
        )
    }

    @Test
    fun `test maintaining old success state after calling ScreenLoad again`() {
        FakeRandomDataSource.model = testResponse

        viewModel.event(RandomScreenEvent.ScreenLoad)

        FakeRandomDataSource.model = testResponse.copy(
            data = testResponse.data.copy(
                _title = "New GIF"
            ),
        )

        viewModel.event(RandomScreenEvent.ScreenLoad)

        val response = viewModel.obtainState
            .skip { it is RandomScreenState.Loading }
            .getOrAwaitValue()

        assertEquals(
            expected = RandomScreenState.Success(testResponse.data),
            actual = response,
        )
    }

    @Test
    fun `test Refresh event`() {
        FakeRandomDataSource.model = testResponse

        viewModel.event(RandomScreenEvent.ScreenLoad)

        val newResponse = testResponse.copy(
            data = testResponse.data.copy(
                _title = "New GIF"
            ),
        )
        FakeRandomDataSource.model = newResponse

        advanceTimeBy(11.seconds)

        val response = viewModel.obtainState
            .skip { it is RandomScreenState.Loading }
            .getOrAwaitValue()

        assertEquals(
            expected = RandomScreenState.Success(newResponse.data),
            actual = response,
        )
    }

    @Test
    fun `test running refresh timer on ScreenLoad event`() {
        FakeRandomDataSource.model = testResponse

        viewModel.event(RandomScreenEvent.ScreenLoad)

        assertTrue(
            actual = viewModel.refreshTimerJob?.isActive == true
        )
    }

    @Test
    fun `test pausing refresh timer on StartSearching event`() {
        FakeRandomDataSource.model = testResponse

        viewModel.event(RandomScreenEvent.ScreenLoad)
        viewModel.event(RandomScreenEvent.StartSearching)

        assertNull(
            actual = viewModel.refreshTimerJob
        )
    }

    @Test
    fun `test resuming refresh timer on ScreenLoad event after navigating back from search`() {
        FakeRandomDataSource.model = testResponse

        viewModel.event(RandomScreenEvent.ScreenLoad)
        viewModel.event(RandomScreenEvent.StartSearching)
        viewModel.event(RandomScreenEvent.ScreenLoad)

        assertTrue(
            actual = viewModel.refreshTimerJob?.isActive == true
        )
    }

    private fun advanceTimeBy(duration: Duration) {
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(duration)
    }
}