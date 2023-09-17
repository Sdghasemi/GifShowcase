package com.hirno.gif.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirno.gif.R
import com.hirno.gif.data.source.repository.SearchRepository
import com.hirno.gif.model.state.SearchScreenEffect
import com.hirno.gif.model.state.SearchScreenEffect.ToggleClearButtonVisibility
import com.hirno.gif.model.state.SearchScreenEffect.ToggleSearchBoxFocus
import com.hirno.gif.model.state.SearchScreenEvent
import com.hirno.gif.model.state.SearchScreenEvent.ClearSearch
import com.hirno.gif.model.state.SearchScreenEvent.ScreenLoad
import com.hirno.gif.model.state.SearchScreenEvent.Search
import com.hirno.gif.model.state.SearchScreenEvent.StartScroll
import com.hirno.gif.model.state.SearchScreenEvent.SwipeToRefresh
import com.hirno.gif.model.state.SearchScreenState
import com.hirno.gif.model.state.SearchScreenState.Error
import com.hirno.gif.model.state.SearchScreenState.Success
import com.hirno.gif.network.response.NetworkResponse
import com.hirno.gif.util.liveData
import com.hirno.gif.view.search.SearchFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * The [SearchFragment] view model. Stores and manipulates state of the fragment
 *
 * @property repository The repository instance used to retrieve random gif info
 */
class SearchViewModel(
    savedState: SavedStateHandle,
    private val repository: SearchRepository,
) : ViewModel() {

    private val viewState: MutableLiveData<SearchScreenState> by savedState.liveData()

    private val viewAction = MutableLiveData<SearchScreenEffect>()

    val obtainState: LiveData<SearchScreenState> = viewState

    val obtainEffect: LiveData<SearchScreenEffect> = viewAction

    @VisibleForTesting(otherwise = PRIVATE)
    var searchJob: Job? = null

    fun event(event: SearchScreenEvent) {
        when(event) {
            is ScreenLoad -> {
                val isFirstLoad = viewAction.value == null
                if (isFirstLoad) viewAction.postValue(ToggleSearchBoxFocus(requested = true))
                if (viewState.value?.let { it !is Success || it.term != event.term } != false)
                    searchForGifs(event.term)
            }
            is Search -> searchForGifs(event.term)
            is SwipeToRefresh -> searchForGifs(event.term)
            is ClearSearch -> viewAction.value = ToggleSearchBoxFocus(requested = true)
            is StartScroll -> viewAction.value = ToggleSearchBoxFocus(requested = false)
        }
    }

    private fun searchForGifs(term: String?) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            viewState.apply {
                viewAction.value = ToggleClearButtonVisibility(visible = !term.isNullOrEmpty())
                if (term.isNullOrBlank()) value = Success()
                else {
                    val previousValue = value
                    val isTyping = (previousValue as? Success)?.term?.isBlank() == false
                    if (!isTyping) value = SearchScreenState.Loading
                    value = when (val result = repository.searchGifs(term)) {
                        is NetworkResponse.Success -> Success(
                            term = term,
                            gifs = result.body.data,
                        )
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
    }
}