package com.qi.billiards.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.databinding.DialogDeletePlayerBinding
import com.qi.billiards.databinding.FragmentPlayerBinding
import com.qi.billiards.db.DbUtil
import com.qi.billiards.db.PlayerEntity
import com.qi.billiards.ui.base.BaseBindingFragment
import kotlinx.coroutines.launch

class PlayerFragment : BaseBindingFragment<FragmentPlayerBinding>() {

    private val players = mutableListOf<PlayerEntity>()
    private val playerAdapter = PlayerEntityAdapter(players, ::showDeleteDialog)

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        binding.rvPlayerEntity.apply {
            adapter = playerAdapter
            layoutManager = LinearLayoutManager(context)
        }

        launch {
            DbUtil.getAllPlayers().let(players::addAll)
            playerAdapter.notifyItemRangeChanged(0, players.size)
        }

    }

    private fun showDeleteDialog(position: Int) {

        val playerEntity = players[position]

        var dBinding: DialogDeletePlayerBinding? = DialogDeletePlayerBinding.inflate(LayoutInflater.from(context))
        val dialogBinding = dBinding!!

        val dialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .create()

        dialog.setOnDismissListener {
            dBinding = null
        }

        dialogBinding.apply {
            tvPlayer.text = playerEntity.toString()
            tvConfirm.setOnClickListener {
                launch {
                    DbUtil.deletePlayer(playerEntity)
                    players.removeAt(position)
                    playerAdapter.notifyItemRemoved(position)
                }
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}