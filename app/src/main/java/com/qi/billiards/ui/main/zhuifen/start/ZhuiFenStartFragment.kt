package com.qi.billiards.ui.main.zhuifen.start

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R
import com.qi.billiards.config.Config
import com.qi.billiards.game.During
import com.qi.billiards.game.OneGame
import com.qi.billiards.game.Operator
import com.qi.billiards.game.Player
import com.qi.billiards.ui.base.BaseFragment
import com.qi.billiards.util.toast
import java.util.ArrayList
import java.util.Date

private const val TAG = "ZhuiFenStartFragment"


class ZhuiFenStartFragment : BaseFragment() {
    private val args: ZhuiFenStartFragmentArgs by navArgs()
    val game by lazy { args.zhuiFenGame } // 一场游戏，包含多局游戏
    lateinit var rootView: View
    var currentPlayerIndex = -1
    val currentGame: OneGame
        get() {
            return game.games.last()
        }

    val rvScoreBoard by lazy { rootView.findViewById<RecyclerView>(R.id.rv_score_board) }
    val rvOperatorGrid by lazy { rootView.findViewById<RecyclerView>(R.id.rv_operator_grid) }
    val rvGameBoard by lazy { rootView.findViewById<RecyclerView>(R.id.rv_game_board) }

    override fun getLayoutId() = R.layout.fragment_zhuifen_start

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        rootView = view
        initView()
        Log.d(TAG, "onViewCreated: $game")
    }

    private fun initView() {

        initGame()

        initScoreBoard() // 计分板 展示总分

        initOperatorGrid() // 操作面板 提供交互

        initGameBoard() // 对局记录板 查看所有操作记录

        initButton()
    }

    private fun initButton() {
        rootView.findViewById<TextView>(R.id.tv_next).setOnClickListener {
            if (game.games.last().operators.oneGameOver()) {
                game.games.last().during.endTime = Date()
                game.games.add(
                    OneGame(
                        getSequences(game.games.last()),
                        mutableListOf(),
                        game.players.map { player -> Player(player.name, 0) },
                        During(Date())
                    )
                )
                rvGameBoard.adapter?.notifyItemInserted(game.games.lastIndex)
            } else {
                toast("这局还没结束呢")
            }

        }

    }

    private fun initGame() {
        val sequences = game.players.map { it.name }
        game.games.add(
            OneGame(
                sequences,
                mutableListOf(),
                game.players.map { player -> Player(player.name, 0) },
                During(Date())
            )
        )
    }

    private fun initScoreBoard() {
        rvScoreBoard.adapter = ScoreBoardAdapter(game.players) {
            currentPlayerIndex = it
            rvOperatorGrid.visibility = if (it == -1) View.GONE else View.VISIBLE
        }
        rvScoreBoard.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    private fun initOperatorGrid() {
        rvOperatorGrid.adapter =
            OperatorGridAdapter(Config.ZhuiFen.userOperators) { id ->
                if (id == Config.ZhuiFen.OP_7) {
                    if (currentGame.operators.isNotEmpty()) {
                        currentGame.operators.removeAt(currentGame.operators.lastIndex)
                        rvGameBoard.adapter?.notifyItemChanged(game.games.lastIndex)
                        notifyScoreBoardChanged()
                    }
                } else if (currentPlayerIndex == -1) {
                    toast("请选择玩家")
                } else {
                    if (currentGame.operators.oneGameOver()) {
                        toast("游戏结束了，点击下一局开始下一局")
                        return@OperatorGridAdapter
                    }
                    currentGame.operators.add(
                        Operator(
                            id,
                            game.players[currentPlayerIndex]
                        )
                    )
                    if (id != Config.ZhuiFen.OP_0 && id != Config.ZhuiFen.OP_1) {
                        currentGame.during.endTime = Date()
                    }
                    rvGameBoard.adapter?.notifyItemChanged(game.games.lastIndex)

                    notifyScoreBoardChanged()
                }
            }
        rvOperatorGrid.layoutManager = GridLayoutManager(context, 4)
    }

    private fun notifyScoreBoardChanged() {
        // qitodo 解决计分板问题
    }

    private fun initGameBoard() {
        rvGameBoard.adapter = GameBoardAdapter(game)

        rvGameBoard.layoutManager = LinearLayoutManager(context)
    }

    private fun getSequences(lastGame: OneGame): List<String> {
        val winner = lastGame.winner?.name ?: lastGame.sequences.first()
        val id = lastGame.operators.last().id
        val temp = if (id == Config.ZhuiFen.OP_3 || id == Config.ZhuiFen.OP_5) {
            ArrayList(lastGame.sequences)
        } else {
            lastGame.sequences.reversed()
        }
        return (temp + temp).slice(temp.indexOf(winner) until temp.indexOf(winner) + temp.size)
    }

    private fun List<Operator>.oneGameOver(): Boolean {
        if (this.isEmpty()) {
            return false
        }
        if (this.last().id <= Config.ZhuiFen.OP_1) {
            return false
        }
        return true
    }

}