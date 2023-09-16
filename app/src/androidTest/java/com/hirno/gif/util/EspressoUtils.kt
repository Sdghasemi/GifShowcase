package com.hirno.gif.util

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import kotlin.time.Duration


/**
 * Perform action of waiting for a specific time.
 */
fun waitFor(duration: Duration) = object : ViewAction {
    override fun getConstraints() = isRoot()

    override fun getDescription() = "Wait for ${duration.inWholeMilliseconds} milliseconds."

    override fun perform(uiController: UiController, view: View?) {
        uiController.loopMainThreadForAtLeast(duration.inWholeMilliseconds)
    }
}