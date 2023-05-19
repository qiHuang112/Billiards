package com.qi.billiards.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseBindingFragment<T : ViewBinding> : Fragment(), CoroutineScope by MainScope() {
    private var _binding: T? = null
    protected val binding get() = _binding!!
    protected var destroyed = false

    abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getBinding(inflater, container)
        return binding.root
    }

    open fun onCustomResume() {

    }

    open fun onCustomPause() {

    }

    override fun onResume() {
        super.onResume()
        onCustomResume()
    }

    override fun onPause() {
        super.onPause()
        onCustomPause()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            onCustomPause()
        } else {
            onCustomResume()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyed = true
        _binding = null
        cancel()
    }
}