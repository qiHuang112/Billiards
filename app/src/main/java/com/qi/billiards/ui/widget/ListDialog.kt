package com.qi.billiards.ui.widget

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.databinding.DialogGameListBinding
import com.qi.billiards.ui.main.MainAdapter

class ListDialog(private val mContext: Context) {
    private val mDialog: Dialog
    private var dBinding: DialogGameListBinding? = DialogGameListBinding.inflate(LayoutInflater.from(mContext))
    private val dialogBinding = dBinding!!
    private lateinit var data: List<MainAdapter.MainItem>

    init {
        mDialog = AlertDialog.Builder(mContext)
            .setView(dialogBinding.root)
            .create()
        mDialog.setOnDismissListener {
            dBinding = null
        }
    }


    fun show() {
        mDialog.show()
    }

    fun dismiss() {
        mDialog.dismiss()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(_data: List<MainAdapter.MainItem>) {
        data = _data
        dialogBinding.rvContent.adapter?.notifyDataSetChanged()
    }

    fun setData(_data: List<MainAdapter.MainItem>) {
        dialogBinding.rvContent.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = MainAdapter(_data)
        }
    }
}