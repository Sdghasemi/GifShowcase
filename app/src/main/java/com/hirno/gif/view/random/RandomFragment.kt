package com.hirno.gif.view.random

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.hirno.gif.R
import com.hirno.gif.databinding.RandomFragmentBinding
import com.hirno.gif.model.Gif
import com.hirno.gif.model.state.MainScreenEvent.StartSearch
import com.hirno.gif.model.state.RandomScreenEvent.ScreenLoad
import com.hirno.gif.model.state.RandomScreenEvent.StartSearching
import com.hirno.gif.model.state.RandomScreenEvent.SwipeToRefresh
import com.hirno.gif.model.state.RandomScreenState
import com.hirno.gif.model.state.RandomScreenState.Error
import com.hirno.gif.model.state.RandomScreenState.Loading
import com.hirno.gif.model.state.RandomScreenState.Success
import com.hirno.gif.view.BaseFragment
import com.hirno.gif.viewmodel.RandomViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RandomFragment : BaseFragment<RandomFragmentBinding>() {

    private val viewModel: RandomViewModel by viewModel()

    override fun onCreateBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RandomFragmentBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchBox()
        setupSwipeRefresh()

        observeViewState()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        viewModel.event(ScreenLoad)
    }

    private fun setupSearchBox() = with(binding.searchBox) {
        setOnClickListener {
            startSearch()
        }
        setOnLongClickListener {
            startSearch()
            true
        }
    }
    private fun startSearch() {
        viewModel.event(StartSearching)
        sharedViewModel.event(StartSearch)
    }
    private fun setupSwipeRefresh() = with(binding.swipeRefresh) {
        setOnRefreshListener {
            isRefreshing = false
            viewModel.event(SwipeToRefresh)
        }
    }

    private fun observeViewState() {
        viewModel.obtainState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: RandomScreenState) {
        when (state) {
            is Loading -> showLoading()
            is Error -> showError(state)
            is Success -> showSuccess(state)
        }
    }

    private fun showLoading() = with(binding) {
        progress.show()
        swipeRefresh.isEnabled = false
        message.isVisible = false
        gifContainer.isVisible = false
    }
    private fun showError(error: Error) = with(binding) {
        progress.hide()
        swipeRefresh.isEnabled = true
        gifContainer.isVisible = false
        showErrorMessage(error)
    }
    private fun RandomFragmentBinding.showErrorMessage(
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
        progress.hide()
        swipeRefresh.isEnabled = true
        message.isVisible = false
        gifContainer.isVisible = true
        showGifInfo(state.gif)
    }
    private fun showGifInfo(model: Gif) = with(binding) {
        gif.apply {
            layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                dimensionRatio = model.images.original.ratio
            }
            contentDescription = model.title
        }
        Glide.with(this@RandomFragment)
            .load(model.images.original.url)
            .into(gif)
        title.text = model.title
        url.text = model.url
        rating.text = model.rating
    }
}