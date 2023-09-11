package com.hirno.gif.view.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.hirno.gif.R
import com.hirno.gif.databinding.SearchFragmentBinding
import com.hirno.gif.model.Gif
import com.hirno.gif.model.state.MainScreenEvent.NavigateBackToRandom
import com.hirno.gif.model.state.SearchScreenEffect
import com.hirno.gif.model.state.SearchScreenEffect.ToggleClearButtonVisibility
import com.hirno.gif.model.state.SearchScreenEffect.ToggleSearchBoxFocus
import com.hirno.gif.model.state.SearchScreenEvent.ClearSearch
import com.hirno.gif.model.state.SearchScreenEvent.ScreenLoad
import com.hirno.gif.model.state.SearchScreenEvent.Search
import com.hirno.gif.model.state.SearchScreenEvent.StartScroll
import com.hirno.gif.model.state.SearchScreenEvent.SwipeToRefresh
import com.hirno.gif.model.state.SearchScreenState
import com.hirno.gif.model.state.SearchScreenState.Error
import com.hirno.gif.model.state.SearchScreenState.Loading
import com.hirno.gif.model.state.SearchScreenState.Success
import com.hirno.gif.util.addScrollListener
import com.hirno.gif.view.BaseFragment
import com.hirno.gif.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : BaseFragment<SearchFragmentBinding>() {

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var listAdapter: SearchAdapter

    private val searchedTerm get() = binding.searchBox.text?.toString()

    override fun onCreateBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = SearchFragmentBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButton()
        setupSearchBox()
        setupClearButton()
        setupSwipeRefresh()

        setupSearchList()

        observeViewState()
        observeViewEffect()

        viewModel.event(ScreenLoad(searchedTerm))
    }

    private fun setupBackButton() = with(binding.back) {
        setOnClickListener {
            mainViewModel.event(NavigateBackToRandom)
        }
    }
    private fun setupSearchBox() = with(binding.searchBox) {
        doAfterTextChanged { _ ->
            viewModel.event(Search(searchedTerm))
        }
    }
    private fun setupClearButton() = with(binding.clear) {
        setOnClickListener {
            binding.searchBox.text?.clear()
            viewModel.event(ClearSearch)
        }
    }
    private fun setupSwipeRefresh() = with(binding.swipeRefresh) {
        setOnRefreshListener {
            isRefreshing = false
            viewModel.event(SwipeToRefresh(searchedTerm))
        }
    }

    private fun setupSearchList() {
        listAdapter = SearchAdapter()
        binding.list.apply {
            adapter = listAdapter
            addScrollListener { _, dy ->
                if (dy > 15) viewModel.event(StartScroll)
            }
        }
    }

    private fun observeViewState() {
        viewModel.obtainState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun observeViewEffect() {
        viewModel.obtainEffect.observe(viewLifecycleOwner) { effect ->
            triggerEffect(effect)
        }
    }

    private fun renderState(state: SearchScreenState) {
        when (state) {
            is Loading -> showLoading()
            is Error -> showError(state)
            is Success -> showSuccess(state)
        }
    }

    private fun triggerEffect(effect: SearchScreenEffect) {
        when (effect) {
            is ToggleSearchBoxFocus -> toggleSearchBoxFocus(effect.requested)
            is ToggleClearButtonVisibility -> toggleClearButtonVisibility(effect.visible)
        }
    }

    private fun showLoading() = with(binding) {
        progress.show()
        swipeRefresh.isEnabled = false
        message.isVisible = false
        itemsContainer.isVisible = false
    }

    private fun showError(error: Error) = with(binding) {
        progress.hide()
        itemsContainer.isVisible = false
        showErrorMessage(error)
    }

    private fun SearchFragmentBinding.showErrorMessage(
        error: Error,
    ) {
        val errorText = error.text ?: getString(error.resId!!)
        val messageText = errorText + getString(R.string.swipe_to_refresh_hint)
        message.apply {
            text = messageText
            isVisible = true
        }
    }

    private fun showSuccess(state: Success) = with(binding) {
        swipeRefresh.isEnabled = !state.term.isNullOrBlank()
        progress.hide()
        message.isVisible = false
        itemsContainer.isVisible = true
        showGifInfo(state.gifs)
    }

    private fun showGifInfo(model: List<Gif>) {
        listAdapter.submitList(model)
    }

    private fun toggleSearchBoxFocus(requested: Boolean) = with(binding.searchBox) {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (requested) {
            requestFocus()
            inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        } else {
            clearFocus()
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }
    private fun toggleClearButtonVisibility(visible: Boolean) = with(binding.clear) {
        isVisible = visible
    }
}