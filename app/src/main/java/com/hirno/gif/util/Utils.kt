package com.hirno.gif.util

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener

inline fun RecyclerView.addScrollListener(crossinline scrollListener: RecyclerView.(dx: Int, dy: Int) -> Unit) = apply {
    addOnScrollListener(object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            scrollListener(dx, dy)
        }
    })
}

val View.inflater: LayoutInflater
    get() = LayoutInflater.from(context)