package com.hirno.gif.model.state

import android.os.Parcelable
import androidx.annotation.StringRes
import com.hirno.gif.model.Gif
import com.hirno.gif.view.search.SearchFragment
import kotlinx.parcelize.Parcelize

/**
 * States of the [SearchFragment] screen
 */
sealed class SearchScreenState {
    @Parcelize
    data object Loading : SearchScreenState(), Parcelable

    @Parcelize
    data class Error(
        val text: String? = null,
        @StringRes
        val resId: Int? = null,
    ) : SearchScreenState(), Parcelable

    @Parcelize
    data class Success(
        val term: String? = null,
        val gifs: List<Gif> = listOf(),
    ) : SearchScreenState(), Parcelable
}

/**
 * Effects of the [SearchFragment] screen
 */
sealed class SearchScreenEffect {
    data class ToggleSearchBoxFocus(val requested: Boolean) : SearchScreenEffect()
    data class ToggleClearButtonVisibility(val visible: Boolean) : SearchScreenEffect()
}

/**
 * Events occurring in [SearchFragment] screen
 */
sealed class SearchScreenEvent {
    data class ScreenLoad(val term: String? = null) : SearchScreenEvent()
    data class Search(val term: String? = null) : SearchScreenEvent()
    data class SwipeToRefresh(val term: String? = null) : SearchScreenEvent()
    data object ClearSearch : SearchScreenEvent()
    data object StartScroll : SearchScreenEvent()
}