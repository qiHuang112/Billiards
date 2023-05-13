package com.qi.billiards.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.databinding.DialogAddPlayerBinding
import com.qi.billiards.databinding.DialogDeletePlayerBinding
import com.qi.billiards.databinding.FragmentNewPlayerBinding
import com.qi.billiards.db.DbUtil
import com.qi.billiards.db.PlayerEntity
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.safeResume
import com.qi.billiards.util.toast
import kotlinx.coroutines.*
import kotlin.coroutines.suspendCoroutine

class NewPlayerFragment : BaseBindingFragment<FragmentNewPlayerBinding>() {

    private val players = mutableListOf<PlayerEntityAdapter.AddPlayer>()
    private val playerAdapter = PlayerEntityAdapter(players, ::showDeleteDialog)

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentNewPlayerBinding {
        return FragmentNewPlayerBinding.inflate(inflater, container, false)
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
            DbUtil.getAllPlayers().map { PlayerEntityAdapter.AddPlayer(it, false) }.let(players::addAll)
            playerAdapter.notifyItemRangeChanged(0, players.size)
        }

        binding.fabAdd.setOnClickListener {
            launch {
                val player = addPlayerWithDialog()
                if (player != null) {
                    player.id = DbUtil.addPlayer(player)
                    players.add(PlayerEntityAdapter.AddPlayer(player))
                    playerAdapter.notifyItemInserted(players.lastIndex)
                    binding.rvPlayerEntity.smoothScrollToPosition(players.lastIndex)
                }
            }
        }
    }

    private suspend fun addPlayerWithDialog() = suspendCoroutine { continuation ->
        var dBinding: DialogAddPlayerBinding? = DialogAddPlayerBinding.inflate(LayoutInflater.from(context))
        val dialogBinding = dBinding!!

        val dialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .create()

        dialog.setOnDismissListener {
            continuation.safeResume(null)
            dBinding = null
        }

        dialogBinding.tvSave.setOnClickListener {
            val text = dialogBinding.etInput.text.toString()
            if (text.isBlank()) {
                toast("请输入用户名")
            } else {
                continuation.safeResume(PlayerEntity(text, 0L))
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showDeleteDialog(position: Int) {

        val addPlayer = players[position]

        var dBinding: DialogDeletePlayerBinding? = DialogDeletePlayerBinding.inflate(LayoutInflater.from(context))
        val dialogBinding = dBinding!!

        val dialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .create()

        dialog.setOnDismissListener {
            dBinding = null
        }

        dialogBinding.apply {
            tvPlayer.text = addPlayer.player.toString()
            tvConfirm.setOnClickListener {
                launch {
                    DbUtil.deletePlayer(addPlayer.player)
                    players.removeAt(position)
                    playerAdapter.notifyItemRemoved(position)
                }
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}