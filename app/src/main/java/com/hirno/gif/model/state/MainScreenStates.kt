package com.hirno.gif.model.state

import com.hirno.gif.view.MainActivity

/**
 * Events occurring in [MainActivity] screen
 */
sealed class MainScreenEvent {
    data object StartSearch : MainScreenEvent()
    data object NavigateBackToRandom : MainScreenEvent()
}

/**
 * Effects of the [MainActivity] screen
 */
sealed class MainScreenEffect {
    data object NavigateToSearchScreen : MainScreenEffect()
    data object NavigateBackToRandomScreen : MainScreenEffect()
}
