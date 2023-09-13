package com.hirno.gif.model.state

import com.hirno.gif.model.Gif
import com.hirno.gif.view.MainActivity

/**
 * Events occurring in [MainActivity] screen
 */
sealed class MainScreenEvent {
    data object StartSearch : MainScreenEvent()
    data object NavigateBackToRandom : MainScreenEvent()
    data class GifSelected(val gif: Gif) : MainScreenEvent()
    data object NavigateBackToSearch : MainScreenEvent()
}

/**
 * Effects of the [MainActivity] screen
 */
sealed class MainScreenEffect {
    data object NavigateToSearchScreen : MainScreenEffect()
    data object NavigateBackToRandomScreen : MainScreenEffect()
    data class NavigateToDetailsScreen(val gif: Gif) : MainScreenEffect()
    data object NavigateBackToSearchScreen : MainScreenEffect()
}
