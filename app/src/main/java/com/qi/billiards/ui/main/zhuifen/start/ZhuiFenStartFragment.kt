package com.qi.billiards.ui.main.zhuifen.start

import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R
import com.qi.billiards.config.DefaultConfig
import com.qi.billiards.game.During
import com.qi.billiards.game.OneGame
import com.qi.billiards.game.Operator
import com.qi.billiards.ui.base.BaseFragment
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

    private val wakeLock by lazy {
        val powerManager =
            ContextCompat.getSystemService(
                requireContext(),
                PowerManager::class.java
            ) as PowerManager
        powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "ZhuiFenStartFragment::MyWakeLockTag"
        )
    }

    override fun getLayoutId() = R.layout.fragment_zhuifen_start

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        wakeLock.setReferenceCounted(false)
        wakeLock.acquire(60 * 60 * 1000L)

        rootView = view
        initView()
        Log.d(TAG, "onViewCreated: $game")
    }

    private fun initView() {

        initGame()

        initScoreBoard() // 计分板 展示总分

        initOperatorGrid() // 操作面板 提供交互

        initGameBoard() // 对局记录板 查看所有操作记录

    }

    private fun initGame() {
        val sequenceList = game.players.map { it.name }
        game.games.add(
            OneGame(
                sequenceList,
                mutableListOf(),
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
            OperatorGridAdapter(DefaultConfig.ZhuiFen.userOperators) { operatorId ->
                if (currentPlayerIndex == -1) {
                    Toast.makeText(context, "请选择玩家", Toast.LENGTH_SHORT).show()
                } else {
                    currentGame.operators.add(
                        Operator(
                            operatorId,
                            game.players[currentPlayerIndex]
                        )
                    )
                }
            }
        rvScoreBoard.layoutManager = LinearLayoutManager(context)
    }

    private fun initGameBoard() {
        rvGameBoard.adapter = GameBoardAdapter(game) { oneGame->

        }

        rvGameBoard.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        wakeLock.release()
    }

}