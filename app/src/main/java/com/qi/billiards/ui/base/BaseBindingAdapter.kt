package com.qi.billiards.ui.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseBindingAdapter<T : ViewBinding> :
    RecyclerView.Adapter<BaseBindingAdapter.BaseBindingViewHolder<T>>() {
    abstract fun getBinding(parent: ViewGroup): T

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder<T> {
        val binding = getBinding(parent)
        return object : BaseBindingViewHolder<T>(binding) {}
    }

    abstract class BaseBindingViewHolder<T : ViewBinding>(val binding: T) :
        RecyclerView.ViewHolder(binding.root)
}