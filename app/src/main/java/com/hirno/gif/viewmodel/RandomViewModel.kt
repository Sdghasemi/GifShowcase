package com.hirno.gif.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirno.gif.R
import com.hirno.gif.data.source.repository.RandomRepository
import com.hirno.gif.model.state.RandomScreenEvent
import com.hirno.gif.model.state.RandomScreenEvent.Refresh
import com.hirno.gif.model.state.RandomScreenEvent.ScreenLoad
import com.hirno.gif.model.state.RandomScreenEvent.StartSearching
import com.hirno.gif.model.state.RandomScreenEvent.SwipeToRefresh
import com.hirno.gif.model.state.RandomScreenState
import com.hirno.gif.model.state.RandomScreenState.Error
import com.hirno.gif.model.state.RandomScreenState.Loading
import com.hirno.gif.model.state.RandomScreenState.Success
import com.hirno.gif.network.response.NetworkResponse
import com.hirno.gif.util.liveData
import com.hirno.gif.view.random.RandomFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The [RandomFragment] view model. Stores and manipulates state of the fragment
 *
 * @property repository The repository instance used to retrieve gifs search result
 */
class RandomViewModel(
    savedState: SavedStateHandle,
    private val repository: RandomRepository,
) : ViewModel() {
    companion object {
        private const val REFRESH_INTERVAL_MILLIS = 10_000L
    }

    private val viewState: MutableLiveData<RandomScreenState> by savedState.liveData()

    val obtainState: LiveData<RandomScreenState> = viewState

    @VisibleForTesting(otherwise = PRIVATE)
    var refreshTimerJob: Job? = null

    fun event(event: RandomScreenEvent) {
        when(event) {
            ScreenLoad -> {
                if (viewState.value !is Success)
                    loadRandomGif()
                else startRefreshTimer()
            }
            StartSearching -> stopRefreshTimer()
            Refresh, SwipeToRefresh -> loadRandomGif()
        }
    }

    private fun loadRandomGif() {
        viewModelScope.launch {
            viewState.apply {
                value = Loading
                value = when (val result = repository.getRandomGif()) {
                    is NetworkResponse.Success -> {
                        stopRefreshTimer()
                        startRefreshTimer()
                        Success(gif = result.body.data)
                    }
                    is NetworkResponse.ApiError -> Error(
                        text = result.body.meta.message,
                    )
                    is NetworkResponse.NetworkError -> Error(
                        resId = R.string.failed_to_connect_to_remote_server,
                    )
                    is NetworkResponse.UnknownError -> Error(
                        resId = R.string.an_error_occurred,
                    )
                }
            }
        }
    }

    private fun startRefreshTimer() {
        if (refreshTimerJob == null) {
            refreshTimerJob = viewModelScope.launch {
                while (true) {
                    delay(REFRESH_INTERVAL_MILLIS)
                    event(Refresh)
                }
            }
        }
    }

    private fun stopRefreshTimer() {
        refreshTimerJob?.cancel()
        refreshTimerJob = null
    }
}