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
import com.qi.billiards.db.PlayerEntity
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
    private lateinit var lastDePlayers: List<DePlayer> // 用于数据库统计收益

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
        lastDePlayers = dePlayers.map { it.copy() }
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
                val removed = dePlayers.removeAt(position)
                updateCost()
                dialog.dismiss()
                removePlayerEntity(removed)
            }
        }

        dialog.show()
    }

    private fun removePlayerEntity(removed: DePlayer) = launch {
        val entity = getPlayerEntity(removed.name)
        entity -= removed
        DbUtil.addPlayer(entity)
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
        savePlayer()
    }

    private fun savePlayer() = launch {
        lastDePlayers.map { dePlayer ->
            val entity = getPlayerEntity(dePlayer.name)
            entity -= dePlayer
            entity
        }.toTypedArray().let {
            DbUtil.addPlayers(*it)
        }

        lastDePlayers = dePlayers.map { it.copy() }

        dePlayers.map { dePlayer ->
            val entity = getPlayerEntity(dePlayer.name)
            entity += dePlayer
            entity
        }.toTypedArray().let {
            DbUtil.addPlayers(*it)
        }
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

        val dePlayer = if (position == -1) DePlayer("", 0.0) else dePlayers[position]
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

            val updatePlayerProfit: (DePlayer) -> Unit = {
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
                dePlayer.size = it.toString().safeToInt()
                val totalSize = dePlayers.fold(if (position == -1) dePlayer.size else 0) { acc, player ->
                    acc + player.size
                }
                dePlayer.cost = dePlayer.size * game.configs["台费"]!! / totalSize
            }
            etInputAa.setText(dePlayer.size.toString())

            tvConfirm.setOnClickListener {
                updatePlayerProfit(dePlayer)
                continuation.safeResume(dePlayer)
                dialog.dismiss()
            }
        }

        dialog.show()

    }

    private operator fun PlayerEntity.minusAssign(dePlayer: DePlayer) {
        this.totalCount--
        this.totalCost -= dePlayer.cost
        this.totalScore -= (dePlayer.score - (dePlayer.buyCount + 1) * game.configs["单次买入"]!!.toInt()) / game.configs["汇率"]!!
        val win = if (dePlayer.profit > 0) 1 else 0
        this.winCount -= win
    }

    private operator fun PlayerEntity.plusAssign(dePlayer: DePlayer) {
        this.totalCount++
        this.totalCost += dePlayer.cost
        this.totalScore += (dePlayer.score - (dePlayer.buyCount + 1) * game.configs["单次买入"]!!.toInt()) / game.configs["汇率"]!!
        val win = if (dePlayer.profit > 0) 1 else 0
        this.winCount += win
    }

    companion object {
        fun getDeConfigs() = linkedMapOf(
            "单次买入" to 3000.0,
            "汇率" to 10.0,
            "台费" to 0.0,
            "误差筹码" to 0.0
        )

        suspend fun getPlayerEntity(name: String): PlayerEntity {
            var entity = DbUtil.getPlayerByName(name)
            if (entity == null) {
                entity = PlayerEntity(name)
                val id = DbUtil.addPlayer(entity)
                entity.id = id
            }
            return entity
        }

    }


}