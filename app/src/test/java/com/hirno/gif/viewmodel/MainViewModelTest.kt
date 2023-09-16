package com.hirno.gif.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hirno.gif.data.testResponse
import com.hirno.gif.di.testAppModules
import com.hirno.gif.model.state.MainScreenEffect.NavigateBackToRandomScreen
import com.hirno.gif.model.state.MainScreenEffect.NavigateBackToSearchScreen
import com.hirno.gif.model.state.MainScreenEffect.NavigateToDetailsScreen
import com.hirno.gif.model.state.MainScreenEffect.NavigateToSearchScreen
import com.hirno.gif.model.state.MainScreenEvent.GifSelected
import com.hirno.gif.model.state.MainScreenEvent.NavigateBackToRandom
import com.hirno.gif.model.state.MainScreenEvent.NavigateBackToSearch
import com.hirno.gif.model.state.MainScreenEvent.StartSearch
import com.hirno.gif.util.getOrAwaitValue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class MainViewModelTest : KoinTest {
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        testAppModules()
    }

    private val viewModel: MainViewModel by inject()

    @Test
    fun `test StartSearch event`() {
        val viewState = viewModel.obtainEffect.getOrAwaitValue {
            viewModel.event(StartSearch)
        }

        assertEquals(
            expected = NavigateToSearchScreen,
            actual = viewState,
        )
    }

    @Test
    fun `test NavigateBackToRandom event`() {
        val viewState = viewModel.obtainEffect.getOrAwaitValue {
            viewModel.event(NavigateBackToRandom)
        }

        assertEquals(
            expected = NavigateBackToRandomScreen,
            actual = viewState,
        )
    }

    @Test
    fun `test GifSelected event`() {
        val model = testResponse.data
        val viewState = viewModel.obtainEffect.getOrAwaitValue {
            viewModel.event(GifSelected(gif = model))
        }

        assertEquals(
            expected = NavigateToDetailsScreen(
                gif = model,
            ),
            actual = viewState,
        )
    }

    @Test
    fun `test NavigateBackToSearch event`() {
        val viewState = viewModel.obtainEffect.getOrAwaitValue {
            viewModel.event(NavigateBackToSearch)
        }

        assertEquals(
            expected = NavigateBackToSearchScreen,
            actual = viewState,
        )
    }
}