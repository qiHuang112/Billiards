package com.qi.billiards.ui.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.DialogZhuifenSummaryBinding

class SummaryDialog(context: Context, headerData: List<String>) : Dialog(context) {

    private var _binding: DialogZhuifenSummaryBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = DialogZhuifenSummaryBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        // 设置对话框大小
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        binding.tvSummary.apply {
            setTableHeaderData(headerData)
            setHeaderTextSize(16f)
            setHeaderBackgroundColor(Color.BLUE)
            setHeaderTextColor(Color.WHITE)
            setHeaderHeight(100)
        }

    }

    fun updateData(tableData: List<List<String>>) {
        binding.tvSummary.setTableData(tableData)
    }

    override fun dismiss() {
        super.dismiss()
        // 在 dialog 销毁时解除 binding 中对布局控件的引用
        _binding = null
    }
}