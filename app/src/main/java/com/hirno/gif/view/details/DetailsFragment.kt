package com.hirno.gif.view.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.hirno.gif.databinding.DetailsFragmentBinding
import com.hirno.gif.model.Gif
import com.hirno.gif.model.state.MainScreenEvent
import com.hirno.gif.view.BaseFragment

class DetailsFragment : BaseFragment<DetailsFragmentBinding>() {

    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DetailsFragmentBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButton()

        showGifInfo(args.gif)
    }

    private fun setupBackButton() = with(binding.back) {
        setOnClickListener {
            sharedViewModel.event(MainScreenEvent.NavigateBackToSearch)
        }
    }

    private fun showGifInfo(model: Gif) = with(binding) {
        pageTitle.text = model.title
        gif.apply {
            layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                dimensionRatio = model.images.original.ratio
            }
            contentDescription = model.title
        }
        Glide.with(this@DetailsFragment)
            .load(model.images.original.url)
            .into(gif)
        title.text = model.title
        url.text = model.url
        rating.text = model.rating
    }
}