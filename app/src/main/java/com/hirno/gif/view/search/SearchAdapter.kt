package com.hirno.gif.view.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.hirno.gif.databinding.SearchGifItemBinding
import com.hirno.gif.model.Gif
import com.hirno.gif.util.inflater

class SearchAdapter : ListAdapter<Gif, SearchAdapter.GifViewHolder>(GifDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GifViewHolder(
        binding = SearchGifItemBinding.inflate(parent.inflater, parent, false)
    )

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class GifViewHolder(
        private val binding: SearchGifItemBinding,
    ) : ViewHolder(binding.root) {
        fun onBind(model: Gif) = with(binding.gif) {
            Glide.with(context)
                .load(model.images.originalStill.url)
                .into(this)
            contentDescription = model.title
        }
    }
}

class GifDiffCallback : DiffUtil.ItemCallback<Gif>() {
    override fun areItemsTheSame(oldItem: Gif, newItem: Gif) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Gif, newItem: Gif) =
        oldItem == newItem
}