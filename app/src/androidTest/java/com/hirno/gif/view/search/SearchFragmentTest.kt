package com.hirno.gif.view.search

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.hasFocus
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import com.hirno.gif.R
import com.hirno.gif.data.badRequest
import com.hirno.gif.data.message
import com.hirno.gif.data.source.SearchDataSource
import com.hirno.gif.data.source.remote.FakeSearchDataSource
import com.hirno.gif.data.testArrayResponse
import com.hirno.gif.model.GifArrayResponse
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.startsWith
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module

@LargeTest
class SearchFragmentTest {
    private val testModules = module {
        single<SearchDataSource> { FakeSearchDataSource }
    }

    @Before
    fun setup() {
        loadKoinModules(testModules)
    }

    @After
    fun tearDown() {
        unloadKoinModules(testModules)
        FakeSearchDataSource.reset()
    }

    @Test
    fun launchFragment_fragmentIsLaunchedAndViewsAreShowing() {
        FakeSearchDataSource.model = testArrayResponse

        launch()

        onView(isRoot()).check(matches(isDisplayed()))
    }

    @Test
    fun successAfterScreenLoad_emptyListIsVisibleWithSearchBoxFocused() {
        FakeSearchDataSource.model = GifArrayResponse()

        launch()

        onView(withId(R.id.search_box)).check(matches(allOf(isDisplayed(), hasFocus())))
        onView(withId(R.id.clear)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.message)).check(matches(not(isDisplayed())))
        onView(withId(R.id.swipe_refresh)).check(matches(not(isEnabled())))
        onView(withText(R.string.searched_results)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).check(matches(allOf(isDisplayed(), hasChildCount(0))))
    }

    @Test
    fun searchForGifs_searchResultsDisplayedInList() {
        FakeSearchDataSource.model = testArrayResponse

        launch()

        onView(withId(R.id.search_box)).perform(typeText("test"))

        onView(withId(R.id.clear)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).check(matches(allOf(isDisplayed(), hasChildCount(3))))
    }

    @Test
    fun errorOccursAfterSearch_errorMessageIsDisplayedAndRefreshLayoutIsEnabled() {
        val error = badRequest.message
        FakeSearchDataSource.errorMessage = error

        launch()

        onView(withId(R.id.search_box)).perform(typeText("test"))

        onView(withId(R.id.clear)).check(matches(isDisplayed()))
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
        onView(withText(startsWith(error))).check(matches(isDisplayed()))
        onView(withId(R.id.swipe_refresh)).check(matches(isEnabled()))
    }

    @Test
    fun swipeToRefreshErrorState_pageRefreshesSuccessfullyAndSearchResultsAreShowing() {
        FakeSearchDataSource.errorMessage = badRequest.message

        launch()

        onView(withId(R.id.search_box)).perform(typeText("test"))

        FakeSearchDataSource.apply {
            reset()
            model = testArrayResponse
        }

        onView(withId(R.id.swipe_refresh)).perform(swipeDown())

        onView(withId(R.id.message)).check(matches(not(isDisplayed())))
        onView(withId(R.id.list)).check(matches(allOf(isDisplayed(), hasChildCount(3))))
    }

    @Test
    fun clearSearchBox_clearButtonHidesAndListBecomesEmpty() {
        FakeSearchDataSource.model = testArrayResponse

        launch()

        onView(withId(R.id.search_box)).perform(typeText("test"))
        onView(withId(R.id.clear)).perform(click())

        onView(withId(R.id.clear)).check(matches(not(isDisplayed())))
        onView(withId(R.id.list)).check(matches(allOf(isDisplayed(), hasChildCount(0))))
    }

    private fun launch() = launchFragmentInContainer<SearchFragment>(
        themeResId = R.style.Theme_GifViewer
    )
}