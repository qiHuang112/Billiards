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
import com.qi.billiards.util.save
import com.qi.billiards.util.toast
import java.util.*

private const val TAG = "ZhuiFenStartFragment"


class ZhuiFenStartFragment : BaseBindingFragment<FragmentZhuifenStartBinding>() {
    private val args: ZhuiFenStartFragmentArgs by navArgs()
    private val globalGame by lazy { args.zhuiFenGame } // 一场游戏，包含多局游戏
    var currentPlayerIndex = -1
    private val currentGame: Game // 当前游戏
        get() {
            return globalGame.group.first()
        }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentZhuifenStartBinding {
        return FragmentZhuifenStartBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initView()
        Log.d(TAG, "onViewCreated: $globalGame")
    }

    private fun initView() {

        initGame()

        initScoreBoard() // 计分板 展示总分

        initOperatorGrid() // 操作面板 提供交互

        initGameBoard() // 对局记录板 查看所有操作记录

        initButton()
    }

    private fun initButton() {
        binding.tvNext.setOnClickListener {
            if (globalGame.group.first().gameOver()) {
                globalGame.group.first().during.endTime = Date()
                globalGame.group.add(
                    0,
                    Game(
                        getSequences(globalGame.group.first()),
                        mutableListOf(),
                        Game.Profits(
                            globalGame.players.map { player -> player.copy(name = player.name) },
                            mutableListOf()
                        ),
                        During(Date())
                    )
                )
                binding.rvGameBoard.adapter?.notifyItemInserted(0)
                binding.rvGameBoard.scrollToPosition(0)
            } else {
                toast("这局还没结束呢")
            }
        }

    }

    private fun initGame() {
        if (!args.reload) {
            val sequences = globalGame.players.map { it.name }
            globalGame.group.add(
                Game(
                    sequences,
                    mutableListOf(),
                    Game.Profits(
                        globalGame.players.map { player ->
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
        binding.rvScoreBoard.adapter = ScoreBoardAdapter(globalGame.players) {
            currentPlayerIndex = it
            binding.rvOperatorGrid.visibility = if (it == -1) View.GONE else View.VISIBLE
        }
        binding.rvScoreBoard.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initOperatorGrid() {
        binding.rvOperatorGrid.adapter =
            OperatorGridAdapter(Config.ZhuiFen.userOperators) { id ->
                if (id == Config.ZhuiFen.OP_7) {
                    if (currentGame.operators.isNotEmpty()) {
                        currentGame.operators.removeAt(currentGame.operators.lastIndex)
                        binding.rvGameBoard.adapter?.notifyItemChanged(0)

                        val operatorProfit =
                            currentGame.profits.opProfits.removeAt(currentGame.profits.opProfits.lastIndex)
                        currentGame.profits.totalProfits.removeOpProfit(operatorProfit)
                        globalGame.players.removeOpProfit(operatorProfit)
                        binding.rvScoreBoard.adapter?.notifyDataSetChanged()
                    }
                } else if (currentPlayerIndex == -1) {
                    toast("请选择玩家")
                } else {
                    if (currentGame.gameOver()) {
                        toast("游戏结束了，点击下一局开始下一局")
                        return@OperatorGridAdapter
                    }
                    val operator = Operator(id, globalGame.players[currentPlayerIndex])
                    currentGame.operators.add(operator)
                    if (id > Config.ZhuiFen.OP_1) {
                        currentGame.during.endTime = Date()
                    }
                    binding.rvGameBoard.adapter?.notifyItemChanged(0)

                    val operatorProfit = getOperatorProfit(operator)
                    currentGame.profits.opProfits.add(operatorProfit)
                    currentGame.profits.totalProfits.addOpProfit(operatorProfit)
                    globalGame.players.addOpProfit(operatorProfit)
                    binding.rvScoreBoard.adapter?.notifyDataSetChanged()
                    binding.rvGameBoard.scrollToPosition(0)
                }
                save(Config.ZhuiFen.KEY_LAST_GAME, globalGame)
            }
        binding.rvOperatorGrid.layoutManager = GridLayoutManager(context, 4)
    }

    /**
     * 拿到当前操作，产生的分数变化
     */
    private fun getOperatorProfit(operator: Operator): List<Player> {
        val profits =
            globalGame.players.map { player -> player.copy(name = player.name, score = 0) }
        val sequence = currentGame.sequences // 顺序表
        val opPlayer = operator.player.name // 操作玩家
        val rule = globalGame.rule
        val curIndex = sequence.indexOf(opPlayer)
        if (curIndex == -1) return profits
        val last = if (curIndex == 0) sequence.last() else sequence[curIndex - 1] // 上家
        val next =
            if (curIndex == sequence.lastIndex) sequence.first() else sequence[curIndex + 1] // 下家
        val addScore: (String, Int) -> Unit = { name, score ->
            val player = profits.find { it.name == name }
            if (player != null) {
                player.score += score
            }
        }
        when (operator.id) {
            Config.ZhuiFen.OP_0 -> {
                addScore(opPlayer, -rule.foul)
                addScore(last, rule.foul)
            }
            Config.ZhuiFen.OP_1 -> {
                addScore(opPlayer, -rule.foul)
                addScore(next, rule.foul)
            }
            Config.ZhuiFen.OP_2 -> {
                currentGame.winner = opPlayer
                addScore(opPlayer, rule.win)
                addScore(last, -rule.win)
            }
            Config.ZhuiFen.OP_3 -> {
                currentGame.winner = opPlayer
                addScore(opPlayer, rule.win)
                addScore(next, -rule.win)
            }
            Config.ZhuiFen.OP_4 -> {
                currentGame.winner = opPlayer
                addScore(opPlayer, rule.xiaojin)
                addScore(last, -rule.xiaojin)
            }
            Config.ZhuiFen.OP_5 -> {
                currentGame.winner = opPlayer
                addScore(opPlayer, rule.xiaojin)
                addScore(next, -rule.xiaojin)
            }
            Config.ZhuiFen.OP_6 -> {
                currentGame.winner = opPlayer
                addScore(opPlayer, rule.dajin * profits.size)
                profits.forEach { player -> addScore(player.name, -rule.dajin) }
            }
            else -> Unit
        }
        return profits
    }

    private fun initGameBoard() {
        binding.rvGameBoard.adapter = GameBoardAdapter(globalGame)

        binding.rvGameBoard.layoutManager = LinearLayoutManager(context)
    }

    private fun getSequences(lastGame: Game): List<String> {
        val winner = lastGame.winner ?: lastGame.sequences.first()
        val id = lastGame.operators.last().id
        val temp = if (id == Config.ZhuiFen.OP_3 || id == Config.ZhuiFen.OP_5) { // 解球赢球，准分顺序不变
            ArrayList(lastGame.sequences)
        } else {
            lastGame.sequences.reversed()
        }
        return (temp + temp).slice(temp.indexOf(winner) until temp.indexOf(winner) + temp.size)
    }

    /**
     * 当局游戏是否结束
     * 操作表不为空，且操作表中最后一次操作不是犯规
     */
    private fun Game.gameOver(): Boolean {
        return operators.isNotEmpty() && operators.last().id > Config.ZhuiFen.OP_1
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: $globalGame")
    }

}