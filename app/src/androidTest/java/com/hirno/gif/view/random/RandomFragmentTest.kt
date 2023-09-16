package com.hirno.gif.view.random

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import com.hirno.gif.R
import com.hirno.gif.data.badRequest
import com.hirno.gif.data.message
import com.hirno.gif.data.source.RandomDataSource
import com.hirno.gif.data.source.remote.FakeRandomDataSource
import com.hirno.gif.data.testResponse
import com.hirno.gif.util.waitFor
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.startsWith
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

@LargeTest
class RandomFragmentTest {
    private val testModules = module {
        single<RandomDataSource> { FakeRandomDataSource }
    }

    @Before
    fun setup() {
        loadKoinModules(testModules)
    }

    @After
    fun tearDown() {
        unloadKoinModules(testModules)
        FakeRandomDataSource.reset()
    }

    @Test
    fun launchFragment_fragmentIsLaunchedAndViewsAreShowing() {
        FakeRandomDataSource.model = testResponse

        launch()

        onView(isRoot()).check(matches(isDisplayed()))
    }

    @Test
    fun errorOccursWhileScreenLoad_errorMessageIsDisplayedAndRefreshLayoutIsEnabled() {
        val error = badRequest.message
        FakeRandomDataSource.errorMessage = error

        launch()

        onView(withId(R.id.search_box)).check(matches(isDisplayed()))
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
        onView(withText(startsWith(error))).check(matches(isDisplayed()))
        onView(withId(R.id.swipe_refresh)).check(matches(isEnabled()))
    }

    @Test
    fun successAfterScreenLoad_contentViewsAreVisibleWithCorrectData() {
        val model = testResponse
        FakeRandomDataSource.model = model

        launch()

        onView(withId(R.id.search_box)).check(matches(isDisplayed()))
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.message)).check(matches(not(isDisplayed())))
        onView(withId(R.id.swipe_refresh)).check(matches(isEnabled()))
        onView(withText(R.string.random_selected_gif)).check(matches(isDisplayed()))
        onView(withId(R.id.gif)).check(matches(isDisplayed()))
        onView(withText(model.data.title)).check(matches(isDisplayed()))
        onView(withText(model.data.url)).check(matches(isDisplayed()))
        onView(withText(model.data.rating)).check(matches(isDisplayed()))
    }

    @Test
    fun waitToRefreshGif_randomGifGetsRefreshedAfter10Seconds() {
        val model = testResponse
        FakeRandomDataSource.model = testResponse

        launch()

        onView(withText(model.data.title)).check(matches(isDisplayed()))

        val newModel = testResponse.copy(
            data = testResponse.data.copy(
                _title = "New GIF"
            )
        )
        FakeRandomDataSource.model = newModel

        onView(isRoot()).perform(waitFor(10.seconds))
        onView(withText(newModel.data.title)).check(matches(isDisplayed()))
    }

    @Test
    fun swipeToRefreshErrorState_pageRefreshesSuccessfullyAndRandomGifDisplays() {
        FakeRandomDataSource.errorMessage = badRequest.message

        launch()

        val newModel = testResponse
        FakeRandomDataSource.apply {
            reset()
            model = newModel
        }

        onView(withId(R.id.swipe_refresh)).perform(swipeDown())

        onView(withId(R.id.search_box)).check(matches(isEnabled()))
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.message)).check(matches(not(isDisplayed())))
        onView(withId(R.id.swipe_refresh)).check(matches(isEnabled()))
        onView(withText(R.string.random_selected_gif)).check(matches(isDisplayed()))
        onView(withId(R.id.gif)).check(matches(isDisplayed()))
        onView(withText(newModel.data.title)).check(matches(isDisplayed()))
        onView(withText(newModel.data.url)).check(matches(isDisplayed()))
        onView(withText(newModel.data.rating)).check(matches(isDisplayed()))
    }

    private fun launch() = launchFragmentInContainer<RandomFragment>(
        themeResId = R.style.Theme_GifViewer
    )
}