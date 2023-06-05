package com.qi.billiards.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.config.Config
import com.qi.billiards.databinding.DialogDeletePlayerBinding
import com.qi.billiards.databinding.DialogEditDePlayerBinding
import com.qi.billiards.databinding.FragmentDeBinding
import com.qi.billiards.db.DbUtil
import com.qi.billiards.db.GameEntity
import com.qi.billiards.game.DeGame
import com.qi.billiards.game.DePlayer
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.safeResume
import com.qi.billiards.util.safeToInt
import com.qi.billiards.util.toJson
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

@SuppressLint("NotifyDataSetChanged")
class DeFragment : BaseBindingFragment<FragmentDeBinding>() {

    private val args: DeFragmentArgs by navArgs()
    private lateinit var game: DeGame
    private lateinit var deConfigs: LinkedHashMap<String, Double>
    private lateinit var dePlayers: MutableList<DePlayer>

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDeBinding {
        return FragmentDeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initView()
    }

    private fun initView() {

        initGame()

        binding.rvConfig.apply {
            adapter = DeConfigAdapter(deConfigs)
            layoutManager = GridLayoutManager(context, 2)
        }

        binding.rvPlayer.apply {
            adapter = DePlayerAdapter(dePlayers, ::showDeleteDialog, ::showEditDialog)
            layoutManager = LinearLayoutManager(context)
        }

        binding.fabAdd.setOnClickListener {
            launch {
                val dePlayer = getPlayerByDialog(-1)
                if (dePlayer != null) {
                    dePlayers.add(dePlayer)
                    updateCost()
                }
            }
        }
    }

    private fun initGame() {
        game = args.deGame
        deConfigs = game.configs
        dePlayers = game.players
    }

    private fun showDeleteDialog(position: Int) {

        val dePlayer = dePlayers[position]

        var dBinding: DialogDeletePlayerBinding? = DialogDeletePlayerBinding.inflate(LayoutInflater.from(context))
        val dialogBinding = dBinding!!

        val dialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .create()

        dialog.setOnDismissListener {
            dBinding = null
        }

        dialogBinding.apply {
            tvPlayer.text = dePlayer.name
            tvConfirm.setOnClickListener {
                dePlayers.removeAt(position)
                updateCost()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showEditDialog(position: Int) = launch {
        val dePlayer = getPlayerByDialog(position)
        if (dePlayer != null) {
            dePlayers[position] = dePlayer
            updateCost()
        }
    }

    private fun updateCost() {
        val totalSize = dePlayers.fold(0) { acc, dePlayer ->
            acc + dePlayer.size
        }
        dePlayers.forEach { player ->
            player.cost = if (player.size <= 0) 0.0 else {
                player.size * deConfigs.getOrDefault("台费", 0.0) / totalSize
            }
        }
        notifyPlayerChanged()
    }

    private fun notifyPlayerChanged() {
        binding.rvPlayer.adapter?.notifyDataSetChanged()
        game.configs["误差筹码"] = dePlayers.fold(0.0) { acc, dePlayer ->
            acc + dePlayer.profit * game.configs["汇率"]!!
        }
        binding.rvConfig.adapter?.notifyItemChanged(3)
        saveGame()
    }

    private fun saveGame() = launch {
        if (game.id == null) {
            val gameEntity = GameEntity(Config.TYPE_DE, game.startTime, game.startTime, game.toJson())
            game.id = DbUtil.addOrUpdateGame(gameEntity)
        } else {
            val gameEntity = GameEntity(Config.TYPE_DE, game.startTime, game.startTime, game.toJson(), game.id)
            DbUtil.addOrUpdateGame(gameEntity)
        }
    }

    private suspend fun getPlayerByDialog(position: Int) = suspendCoroutine { continuation ->

        val dePlayer = if (position == -1) DePlayer("", game.configs["单次买入"]!!.toInt()) else dePlayers[position]
        var dBinding: DialogEditDePlayerBinding? = DialogEditDePlayerBinding.inflate(LayoutInflater.from(context))
        val dialogBinding = dBinding!!

        val dialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .create()

        dialog.setOnDismissListener {
            continuation.safeResume(null)
            dBinding = null
        }

        dialogBinding.apply {
            if (position == -1) {
                tvTitle.text = "新增玩家"
                tvName.visibility = View.VISIBLE
                etInputName.visibility = View.VISIBLE
                etInputName.requestFocus()
            } else {
                tvTitle.text = dePlayer.name
                tvName.visibility = View.GONE
                etInputName.visibility = View.GONE
            }

            etInputScore.setText(dePlayer.score.toString())
            etInputBuyCount.setText(dePlayer.buyCount.toString())

            val updatePlayerProfit: (DePlayer) -> Unit = {
                val buyUnit = game.configs["单次买入"]!!
                it.profit = (-it.buyCount * buyUnit + it.score - buyUnit) / game.configs["汇率"]!!
            }

            etInputName.doAfterTextChanged {
                dePlayer.name = it.toString()
            }
            etInputBuyCount.doAfterTextChanged {
                dePlayer.buyCount = it.toString().safeToInt()
                updatePlayerProfit(dePlayer)
            }
            etInputScore.doAfterTextChanged {
                dePlayer.score = it.toString().safeToInt()
                updatePlayerProfit(dePlayer)
            }
            etInputAa.doAfterTextChanged {
                dePlayer.size = it.toString().safeToInt()
                val totalSize = dePlayers.fold(if (position == -1) dePlayer.size else 0) { acc, player ->
                    acc + player.size
                }
                dePlayer.cost = dePlayer.size * game.configs["台费"]!! / totalSize
            }
            etInputAa.setText(dePlayer.size.toString())

            tvConfirm.setOnClickListener {
                continuation.safeResume(dePlayer)
                dialog.dismiss()
            }
        }

        dialog.show()

    }

    companion object {
        fun getDeConfigs() = linkedMapOf(
            "单次买入" to 1000.0,
            "汇率" to 5.0,
            "台费" to 0.0,
            "误差筹码" to 0.0
        )

    }


}