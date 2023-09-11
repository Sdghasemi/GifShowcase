package com.hirno.gif.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirno.gif.R
import com.hirno.gif.data.source.repository.RandomRepository
import com.hirno.gif.model.state.RandomScreenEvent
import com.hirno.gif.model.state.RandomScreenEvent.Refresh
import com.hirno.gif.model.state.RandomScreenEvent.ScreenDestroy
import com.hirno.gif.model.state.RandomScreenEvent.ScreenLoad
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

    private var refreshTimerJob: Job? = null

    fun event(event: RandomScreenEvent) {
        when(event) {
            ScreenLoad -> {
                if (viewState.value !is Success)
                    loadRandomGif()
                else startRefreshTimer()
            }
            ScreenDestroy -> stopRefreshTimer()
            Refresh, SwipeToRefresh -> loadRandomGif()
        }
    }

    private fun loadRandomGif() {
        viewModelScope.launch {
            viewState.apply {
                value = Loading
                value = when (val result = repository.getRandomGif()) {
                    is NetworkResponse.Success -> {
                        startRefreshTimer()
                        Success(gif = result.body.data)
                    }
                    is NetworkResponse.ApiError -> Error.from(
                        error = result.body.meta.message ?: R.string.an_error_occurred,
                    )
                    is NetworkResponse.NetworkError -> Error.from(
                        error = R.string.failed_to_connect_to_remote_server,
                    )
                    is NetworkResponse.UnknownError -> Error.from(
                        error = R.string.an_error_occurred,
                    )
                }
            }
        }
    }

    private fun startRefreshTimer() {
        stopRefreshTimer()
        refreshTimerJob = viewModelScope.launch {
            while (true) {
                delay(REFRESH_INTERVAL_MILLIS)
                event(Refresh)
            }
        }
    }

    private fun stopRefreshTimer() {
        refreshTimerJob?.cancel()
    }
}