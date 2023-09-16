package com.hirno.gif.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hirno.gif.data.badRequest
import com.hirno.gif.data.message
import com.hirno.gif.data.source.SearchDataSource
import com.hirno.gif.data.source.remote.FakeSearchDataSource
import com.hirno.gif.data.testArrayResponse
import com.hirno.gif.di.testAppModules
import com.hirno.gif.model.state.SearchScreenEffect.ToggleClearButtonVisibility
import com.hirno.gif.model.state.SearchScreenEffect.ToggleSearchBoxFocus
import com.hirno.gif.model.state.SearchScreenEvent
import com.hirno.gif.model.state.SearchScreenState
import com.hirno.gif.util.MainDispatcherRule
import com.hirno.gif.util.getOrAwaitValue
import com.hirno.gif.util.skip
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.runTest
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

@RunWith(AndroidJUnit4::class)
class SearchViewModelTest : KoinTest {
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
                single<SearchDataSource> { FakeSearchDataSource }
            }
        )
    }

    private val viewModel: SearchViewModel by inject()
    private val savedState: SavedStateHandle by inject()

    @Before
    fun setup() {
        FakeSearchDataSource.reset()
    }

    @Test
    fun `test empty success view state on ScreenLoad`() {
        val viewState = viewModel.obtainState.getOrAwaitValue {
            viewModel.event(SearchScreenEvent.ScreenLoad())
        }

        assertEquals(
            expected = SearchScreenState.Success(),
            actual = viewState,
        )
    }

    @Test
    fun `test loading view state`() {
        val viewState = viewModel.obtainState.getOrAwaitValue {
            viewModel.event(SearchScreenEvent.Search(term = "test"))
        }

        assertEquals(
            expected = SearchScreenState.Loading,
            actual = viewState,
        )
    }

    @Test
    fun `test success view state`() {
        FakeSearchDataSource.model = testArrayResponse

        val response = viewModel.obtainState
            .skip { it is SearchScreenState.Loading }
            .getOrAwaitValue {
                viewModel.event(SearchScreenEvent.Search(term = "test"))
            }

        assertEquals(
            expected = SearchScreenState.Success(
                term = "test",
                gifs = testArrayResponse.data,
            ),
            actual = response,
        )
    }

    @Test
    fun `test error view state`() {
        val error = badRequest.message
        FakeSearchDataSource.errorMessage = error

        val response = viewModel.obtainState
            .skip { it is SearchScreenState.Loading }
            .getOrAwaitValue {
                viewModel.event(SearchScreenEvent.Search(term = "test"))
            }

        assertEquals(
            expected = SearchScreenState.Error(text = error),
            actual = response,
        )
    }

    @Test
    fun `test SwipeToRefresh event`() {
        viewModel.event(SearchScreenEvent.ScreenLoad())

        var response = viewModel.obtainState
            .skip { it is SearchScreenState.Success }
            .getOrAwaitValue {
                viewModel.event(SearchScreenEvent.SwipeToRefresh(term = "test"))
            }

        assertEquals(
            expected = SearchScreenState.Loading,
            actual = response,
        )

        response = viewModel.obtainState.getOrAwaitValue()

        assertEquals(
            expected = SearchScreenState.Success(
                term = "test",
                gifs = testArrayResponse.data,
            ),
            actual = response,
        )
    }

    @Test
    fun `test SwipeToRefresh event after error`() {
        FakeSearchDataSource.errorMessage = badRequest.message

        viewModel.event(SearchScreenEvent.Search(term = "test"))

        FakeSearchDataSource.reset()

        var response = viewModel.obtainState
            .skip { it is SearchScreenState.Error }
            .getOrAwaitValue {
                viewModel.event(SearchScreenEvent.SwipeToRefresh(term = "test"))
            }

        assertEquals(
            expected = SearchScreenState.Loading,
            actual = response,
        )

        response = viewModel.obtainState.getOrAwaitValue()

        assertEquals(
            expected = SearchScreenState.Success(
                term = "test",
                gifs = testArrayResponse.data,
            ),
            actual = response,
        )
    }

    @Test
    fun `test saved error state`() {
        val error = badRequest.message
        FakeSearchDataSource.errorMessage = error

        viewModel.event(SearchScreenEvent.Search(term = "test"))

        assertEquals(
            expected = SearchScreenState.Error(text = error),
            actual = savedState["viewState"],
        )
    }

    @Test
    fun `test saved success state`() {
        FakeSearchDataSource.model = testArrayResponse

        viewModel.event(SearchScreenEvent.Search(term = "test"))

        assertEquals(
            expected = SearchScreenState.Success(
                term = "test",
                gifs = testArrayResponse.data,
            ),
            actual = savedState["viewState"],
        )
    }

    @Test
    fun `test changing failed response after calling ScreenLoad`() {
        FakeSearchDataSource.errorMessage = badRequest.message

        viewModel.event(SearchScreenEvent.Search(term = "test"))

        FakeSearchDataSource.apply {
            reset()
            model = testArrayResponse
        }

        viewModel.event(SearchScreenEvent.ScreenLoad(term = "test"))

        val response = viewModel.obtainState.getOrAwaitValue()

        assertEquals(
            expected = SearchScreenState.Success(
                term = "test",
                gifs = testArrayResponse.data,
            ),
            actual = response,
        )
    }

    @Test
    fun `test maintaining old success state after calling ScreenLoad`() {
        FakeSearchDataSource.model = testArrayResponse

        viewModel.event(SearchScreenEvent.Search(term = "test"))

        FakeSearchDataSource.model = testArrayResponse.copy(
            data = emptyList()
        )

        viewModel.event(SearchScreenEvent.ScreenLoad(term = "test"))

        val response = viewModel.obtainState.getOrAwaitValue()

        assertEquals(
            expected = SearchScreenState.Success(
                term = "test",
                gifs = testArrayResponse.data,
            ),
            actual = response,
        )
    }

    @Test
    fun `test ToggleClearButtonVisibility effect`() = runTest {
        FakeSearchDataSource.model = testArrayResponse

        var viewEffect = viewModel.obtainEffect.getOrAwaitValue {
            viewModel.event(SearchScreenEvent.Search())
        }

        assertEquals(
            expected = ToggleClearButtonVisibility(visible = false),
            actual = viewEffect,
        )

        viewModel.event(SearchScreenEvent.Search(term = "test"))

        viewEffect = viewModel.obtainEffect.getOrAwaitValue()

        assertEquals(
            expected = ToggleClearButtonVisibility(visible = true),
            actual = viewEffect,
        )
    }

    @Test
    fun `test requesting ToggleSearchBoxFocus effect on ClearSearch`() = runTest {
        val viewEffect = viewModel.obtainEffect.getOrAwaitValue {
            viewModel.event(SearchScreenEvent.ClearSearch)
        }

        assertEquals(
            expected = ToggleSearchBoxFocus(requested = true),
            actual = viewEffect,
        )
    }

    @Test
    fun `test clearing ToggleSearchBoxFocus effect on StartScroll`() {
        viewModel.event(SearchScreenEvent.ScreenLoad())
        viewModel.event(SearchScreenEvent.StartScroll)

        val viewEffect = viewModel.obtainEffect.getOrAwaitValue()

        assertEquals(
            expected = ToggleSearchBoxFocus(requested = false),
            actual = viewEffect,
        )
    }
}