package com.qi.billiards.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.qi.billiards.databinding.LayoutTitlebarBinding

class TitleBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var _binding: LayoutTitlebarBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = LayoutTitlebarBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setOnBackClickListener(onClick: () -> Unit) {
        binding.ivBack.setOnClickListener {
            onClick()
        }
    }

    fun setOnMenuClickListener(onClick: () -> Unit) {
        binding.ivSave.visibility = View.VISIBLE
        binding.ivSave.setOnClickListener {
            onClick()
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
    }

}