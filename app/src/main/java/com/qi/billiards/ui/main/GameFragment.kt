package com.qi.billiards.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.databinding.DialogDeletePlayerBinding
import com.qi.billiards.databinding.DialogEditDePlayerBinding
import com.qi.billiards.databinding.FragmentGameBinding
import com.qi.billiards.bean.Game
import com.qi.billiards.bean.Player
import com.qi.billiards.data.AppData
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.safeResume
import com.qi.billiards.util.safeToInt
import com.qi.billiards.util.toast
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

@SuppressLint("NotifyDataSetChanged")
class GameFragment : BaseBindingFragment<FragmentGameBinding>() {

    private val args: GameFragmentArgs by navArgs()
    private lateinit var game: Game
    private lateinit var configs: LinkedHashMap<String, Double>
    private lateinit var players: MutableList<Player>
    private lateinit var lastDePlayers: List<Player> // 用于数据库统计收益

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentGameBinding {
        return FragmentGameBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initView()
    }

    private fun initView() {

        initGame()

        binding.tbTitle.apply {
            setTitle("${game.type}:${game.date.split(" ")[0]}")
            setOnBackClickListener {
                findNavController().navigateUp()
            }
            setOnMenuClickListener {
                launch {
                    if (saveGame()) {
                        findNavController().navigateUp()
                    }
                }
            }
        }

        binding.rvConfig.apply {
            adapter = ConfigAdapter(configs)
            layoutManager = GridLayoutManager(context, 2)
        }

        binding.rvPlayer.apply {
            adapter = PlayerAdapter(players, ::showDeleteDialog, ::showEditDialog)
            layoutManager = LinearLayoutManager(context)
        }

        binding.fabAdd.setOnClickListener {
            launch {
                val dePlayer = getPlayerByDialog(-1)
                if (dePlayer != null) {
                    players.add(dePlayer)
                    updateCost()
                }
            }
        }
    }

    private fun initGame() {
        game = args.game
        configs = game.configs
        players = game.players
        lastDePlayers = players.map { it.copy() }
    }

    private fun showDeleteDialog(position: Int) {

        val dePlayer = players[position]

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
                players.removeAt(position)
                updateCost()
                dialog.dismiss()
            }
        }

        dialog.show()
    }


    private fun showEditDialog(position: Int) = launch {
        val dePlayer = getPlayerByDialog(position)
        if (dePlayer != null) {
            players[position] = dePlayer
            updateCost()
        }
    }

    private fun updateCost() {
        val totalSize = players.fold(0) { acc, player ->
            acc + player.aa
        }
        players.forEach { player ->
            player.cost = if (player.aa <= 0) 0.0 else {
                player.aa * configs.getOrDefault("台费", 0.0) / totalSize
            }
        }
        notifyPlayerChanged()
    }

    private fun notifyPlayerChanged() {
        binding.rvPlayer.adapter?.notifyDataSetChanged()
        game.configs["误差筹码"] = players.fold(0.0) { acc, dePlayer ->
            acc + dePlayer.profit * game.configs["汇率"]!!
        }
        binding.rvConfig.adapter?.notifyItemChanged(3)
    }

    private suspend fun getPlayerByDialog(position: Int) = suspendCoroutine { continuation ->

        val dePlayer = if (position == -1) Player("", score = 0.0) else players[position]
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
                etInputName.hint = "请输入名称"
                etInputName.requestFocus()
            } else {
                tvTitle.text = dePlayer.name
                tvName.visibility = View.GONE
                etInputName.visibility = View.GONE
            }

            if (dePlayer.score == 0.0 && position == -1) {
                etInputScore.hint = "请输入剩余筹码"
                etInputScore.setText("")
            } else {
                etInputScore.setText(dePlayer.score.toInt().toString())
            }
            etInputBuyCount.setText(dePlayer.buyCount.toString())

            val updatePlayerProfit: (Player) -> Unit = {
                val buyUnit = game.configs["单次买入"]!!
                it.profit = (-it.buyCount * buyUnit + it.score - buyUnit) / game.configs["汇率"]!!
            }

            etInputName.doAfterTextChanged {
                dePlayer.name = it.toString()
            }
            etInputBuyCount.doAfterTextChanged {
                dePlayer.buyCount = it.toString().safeToInt()
            }
            etInputScore.doAfterTextChanged {
                dePlayer.score = it.toString().safeToInt().toDouble()
            }
            etInputAa.doAfterTextChanged {
                dePlayer.aa = it.toString().safeToInt()
                val totalSize = players.fold(if (position == -1) dePlayer.aa else 0) { acc, player ->
                    acc + player.aa
                }
                dePlayer.cost = dePlayer.aa * game.configs["台费"]!! / totalSize
            }
            etInputAa.setText(dePlayer.aa.toString())

            tvConfirm.setOnClickListener {
                updatePlayerProfit(dePlayer)
                continuation.safeResume(dePlayer)
                dialog.dismiss()
            }
        }

        dialog.show()

    }

    private fun saveGame(): Boolean {
        if (players.isEmpty()) {
            toast("请添加玩家")
            return false
        }

        AppData.addGame(game)
        return true
    }

    companion object {
        fun getConfigs() = linkedMapOf(
            "单次买入" to 1000.0,
            "汇率" to 5.0,
            "台费" to 0.0,
            "误差筹码" to 0.0
        )

    }


}