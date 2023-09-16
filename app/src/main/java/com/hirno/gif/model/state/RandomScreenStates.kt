package com.hirno.gif.model.state

import android.os.Parcelable
import androidx.annotation.StringRes
import com.hirno.gif.model.Gif
import com.hirno.gif.view.random.RandomFragment
import kotlinx.parcelize.Parcelize

/**
 * States of the [RandomFragment] screen
 */
sealed class RandomScreenState {
    @Parcelize
    data object Loading : RandomScreenState(), Parcelable

    @Parcelize
    data class Error(
        val text: String? = null,
        @StringRes
        val resId: Int? = null,
    ) : RandomScreenState(), Parcelable

    @Parcelize
    data class Success(
        val gif: Gif,
    ) : RandomScreenState(), Parcelable
}

/**
 * Events occurring in [RandomFragment] screen
 */
sealed class RandomScreenEvent {
    data object ScreenLoad : RandomScreenEvent()
    data object StartSearching : RandomScreenEvent()
    data object Refresh : RandomScreenEvent()
    data object SwipeToRefresh : RandomScreenEvent()
}