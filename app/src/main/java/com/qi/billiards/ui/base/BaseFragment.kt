package com.qi.billiards.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    abstract fun getLayoutId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)
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
}