package com.hirno.gif.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hirno.gif.model.state.MainScreenEffect
import com.hirno.gif.model.state.MainScreenEffect.NavigateBackToRandomScreen
import com.hirno.gif.model.state.MainScreenEffect.NavigateBackToSearchScreen
import com.hirno.gif.model.state.MainScreenEffect.NavigateToDetailsScreen
import com.hirno.gif.model.state.MainScreenEffect.NavigateToSearchScreen
import com.hirno.gif.model.state.MainScreenEvent
import com.hirno.gif.model.state.MainScreenEvent.GifSelected
import com.hirno.gif.model.state.MainScreenEvent.NavigateBackToRandom
import com.hirno.gif.model.state.MainScreenEvent.NavigateBackToSearch
import com.hirno.gif.model.state.MainScreenEvent.StartSearch
import com.hirno.gif.util.FreshLiveData
import com.hirno.gif.view.MainActivity

/**
 * The [MainActivity] view model. Stores and manipulates state of the activity
 */
class MainViewModel : ViewModel() {

    private val viewAction = FreshLiveData<MainScreenEffect>()

    val obtainEffect: LiveData<MainScreenEffect> = viewAction

    fun event(event: MainScreenEvent) {
        when(event) {
            is StartSearch -> viewAction.value = NavigateToSearchScreen
            is NavigateBackToRandom -> viewAction.value = NavigateBackToRandomScreen
            is GifSelected -> viewAction.value = NavigateToDetailsScreen(event.gif)
            is NavigateBackToSearch -> viewAction.value = NavigateBackToSearchScreen
        }
    }
}