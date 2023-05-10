package com.qi.billiards.ui.main.zhuifen.start

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.config.Config
import com.qi.billiards.databinding.FragmentZhuifenStartBinding
import com.qi.billiards.game.*
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.ui.widget.SummaryDialog
import com.qi.billiards.util.save
import com.qi.billiards.util.toast
import java.util.*

private const val TAG = "ZhuiFenStartFragment"


class ZhuiFenStartFragment : BaseBindingFragment<FragmentZhuifenStartBinding>() {
    private val args: ZhuiFenStartFragmentArgs by navArgs()
    private val game by lazy { args.zhuiFenGame } // 一场游戏，包含多局游戏
    var currentPlayerIndex = -1
    private val currentRound: Round // 当前游戏
        get() {
            return game.group.first()
        }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentZhuifenStartBinding {
        return FragmentZhuifenStartBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initView()
        Log.d(TAG, "onViewCreated: $game")
    }

    private fun initView() {

        initGame()

        initScoreBoard() // 计分板 展示总分

        initOperatorGrid() // 操作面板 提供交互

        initGameBoard() // 对局记录板 查看所有操作记录

        binding.tvSummary.setOnClickListener {
            showSummaryDialog()
        }

    }

    private fun showSummaryDialog() {
        val summaryDialog =
            SummaryDialog(requireContext(), listOf("玩家") + game.summaries.keys)
        val tableData = listOf("犯规", "普胜", "小金", "大金", "胜局", "败局").map {
            listOf(it + "次数") + game.summaries.keys.map { name ->
                game.summaries[name]?.getOrDefault(it, 0).toString()
            }
        } + listOf(listOf("总比分") + game.summaries.keys.map { name ->
            game.players.find { it.name == name }?.score.toString()
        }) + listOf(listOf("胜率") + game.summaries.keys.map { name ->
            val totalWin = game.summaries[name]?.getOrDefault("胜局", 0) ?: 0
            val totalCount = game.group.size - if (currentRound.gameOver()) 0 else 1
            String.format("%.2f%%", 100.0 * totalWin / totalCount)
        }) + listOf(listOf("败率") + game.summaries.keys.map { name ->
            val totalLose = game.summaries[name]?.getOrDefault("败局", 0) ?: 0
            val totalCount = game.group.size - if (currentRound.gameOver()) 0 else 1
            String.format("%.2f%%", 100.0 * totalLose / totalCount)
        })

        summaryDialog.updateData(tableData)
        summaryDialog.show()

    }

    private fun startNextGame() {
        game.group.first().during.endTime = Date()
        game.group.add(
            0,
            Round(
                getSequences(game.group.first()),
                mutableListOf(),
                Round.Profits(
                    game.players.map { player -> player.copy(name = player.name) },
                    mutableListOf()
                ),
                During(Date())
            )
        )
        binding.rvGameBoard.adapter?.notifyItemInserted(0)
        binding.rvGameBoard.scrollToPosition(0)
        save(Config.ZhuiFen.KEY_LAST_GAME, game)

    }

    private fun initGame() {
        if (!args.reload) {
            val sequences = game.players.map { it.name }
            game.group.add(
                Round(
                    sequences,
                    mutableListOf(),
                    Round.Profits(
                        game.players.map { player ->
                            player.copy(
                                name = player.name,
                                score = 0
                            )
                        },
                        mutableListOf()
                    ),
                    During(Date())
                )
            )
        }
    }

    private fun initScoreBoard() {
        binding.rvScoreBoard.adapter = ScoreBoardAdapter(game.players) {
            currentPlayerIndex = it
            binding.rvOperatorGrid.visibility = if (it == -1) View.GONE else View.VISIBLE
        }
        binding.rvScoreBoard.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    private fun onClickUndo() {
        if (currentRound.operators.isNotEmpty()) {
            val operator =
                currentRound.operators.removeAt(currentRound.operators.lastIndex)
            binding.rvGameBoard.adapter?.notifyItemChanged(0)

            val operatorProfit = getOperatorProfit(operator, true)
            currentRound.profits.opProfits.removeAt(currentRound.profits.opProfits.lastIndex)
            currentRound.profits.totalProfits.addOpProfit(operatorProfit)
            game.players.addOpProfit(operatorProfit)
            binding.rvScoreBoard.adapter?.notifyDataSetChanged()
        } else if (game.group.size > 1) {
            game.group.removeAt(0)
            binding.rvGameBoard.adapter?.notifyItemRemoved(0)
            onClickUndo()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initOperatorGrid() {
        binding.rvOperatorGrid.adapter =
            OperatorGridAdapter(Config.ZhuiFen.userOperators) { id ->
                if (id == Config.ZhuiFen.OP_7) {
                    onClickUndo()
                } else if (currentPlayerIndex == -1) {
                    toast("请选择玩家")
                } else {
                    val operator = Operator(id, game.players[currentPlayerIndex])
                    currentRound.operators.add(operator)
                    if (id > Config.ZhuiFen.OP_1) {
                        currentRound.during.endTime = Date()
                    }
                    binding.rvGameBoard.adapter?.notifyItemChanged(0)

                    val operatorProfit = getOperatorProfit(operator)
                    currentRound.profits.opProfits.add(operatorProfit)
                    currentRound.profits.totalProfits.addOpProfit(operatorProfit)
                    game.players.addOpProfit(operatorProfit)
                    binding.rvScoreBoard.adapter?.notifyDataSetChanged()
                    binding.rvGameBoard.scrollToPosition(0)
                    binding.root.post {
                        if (currentRound.gameOver()) {
                            startNextGame()
                        }
                    }
                }
                save(Config.ZhuiFen.KEY_LAST_GAME, game)
            }
        binding.rvOperatorGrid.layoutManager = GridLayoutManager(context, 4)
    }

    /**
     * 拿到当前操作，产生的分数变化
     */
    private fun getOperatorProfit(operator: Operator, undo: Boolean = false): List<Player> {
        val sign = if (undo) -1 else 1
        val profits =
            game.players.map { player -> player.copy(name = player.name, score = 0) }
        val sequence = currentRound.sequences // 顺序表
        val opPlayer = operator.player.name // 操作玩家
        val rule = game.rule
        val curIndex = sequence.indexOf(opPlayer)
        if (curIndex == -1) return profits
        val last = if (curIndex == 0) sequence.last() else sequence[curIndex - 1] // 上家
        val next =
            if (curIndex == sequence.lastIndex) sequence.first() else sequence[curIndex + 1] // 下家
        val addScore: (String, Int) -> Unit = { name, score ->
            val player = profits.find { it.name == name }
            if (player != null) {
                player.score += score * sign
            }
        }
        val addSummary: (String, String) -> Unit = { name, op ->
            val playerSummary = game.summaries[name]
            if (playerSummary != null) {
                val count = playerSummary.getOrDefault(op, 0)
                playerSummary[op] = count + sign
            }
        }
        when (operator.id) {
            Config.ZhuiFen.OP_0 -> {
                addSummary(opPlayer, "犯规")
                addScore(opPlayer, -rule.foul)
                addScore(last, rule.foul)
            }
            Config.ZhuiFen.OP_1 -> {
                addSummary(opPlayer, "犯规")
                addScore(opPlayer, -rule.foul)
                addScore(next, rule.foul)
            }
            Config.ZhuiFen.OP_2 -> {
                addSummary(opPlayer, "普胜")
                addSummary(opPlayer, "胜局")
                addSummary(last, "败局")
                currentRound.winner = opPlayer
                addScore(opPlayer, rule.win)
                addScore(last, -rule.win)
            }
            Config.ZhuiFen.OP_3 -> {
                addSummary(opPlayer, "普胜")
                addSummary(opPlayer, "胜局")
                addSummary(next, "败局")
                currentRound.winner = opPlayer
                addScore(opPlayer, rule.win)
                addScore(next, -rule.win)
            }
            Config.ZhuiFen.OP_4 -> {
                addSummary(opPlayer, "小金")
                addSummary(opPlayer, "胜局")
                addSummary(last, "败局")
                currentRound.winner = opPlayer
                addScore(opPlayer, rule.xiaojin)
                addScore(last, -rule.xiaojin)
            }
            Config.ZhuiFen.OP_5 -> {
                addSummary(opPlayer, "小金")
                addSummary(opPlayer, "胜局")
                addSummary(next, "败局")
                currentRound.winner = opPlayer
                addScore(opPlayer, rule.xiaojin)
                addScore(next, -rule.xiaojin)
            }
            Config.ZhuiFen.OP_6 -> {
                addSummary(opPlayer, "大金")
                addSummary(opPlayer, "胜局")
                currentRound.winner = opPlayer
                addScore(opPlayer, rule.dajin * profits.size)
                profits.forEach { player ->
                    addScore(player.name, -rule.dajin)
                    if (player.name != opPlayer) {
                        addSummary(player.name, "败局")
                    }
                }
            }
            else -> Unit
        }
        return profits
    }

    private fun initGameBoard() {
        binding.rvGameBoard.adapter = GameBoardAdapter(game)

        binding.rvGameBoard.layoutManager = LinearLayoutManager(context)
    }

    private fun getSequences(lastRound: Round): List<String> {
        val winner = lastRound.winner ?: lastRound.sequences.first()
        val id = lastRound.operators.last().id
        val temp = if (id == Config.ZhuiFen.OP_3 || id == Config.ZhuiFen.OP_5) { // 解球赢球，准分顺序不变
            ArrayList(lastRound.sequences)
        } else {
            lastRound.sequences.reversed()
        }
        return (temp + temp).slice(temp.indexOf(winner) until temp.indexOf(winner) + temp.size)
    }

    /**
     * 当局游戏是否结束
     * 操作表不为空，且操作表中最后一次操作不是犯规
     */
    private fun Round.gameOver(): Boolean {
        return operators.isNotEmpty() && operators.last().id > Config.ZhuiFen.OP_1
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: $game")
    }

}