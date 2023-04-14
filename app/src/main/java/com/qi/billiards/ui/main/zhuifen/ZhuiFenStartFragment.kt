package com.qi.billiards.ui.main.zhuifen

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.qi.billiards.R
import com.qi.billiards.config.ZhuiFenScoreConfig
import com.qi.billiards.ui.base.BaseFragment
import com.qi.billiards.util.safeToInt

private const val TAG = "ZhuiFenStartFragment"

class ZhuiFenStartFragment : BaseFragment() {

    var currentSelectPlayer = 0

    private val scoreConfig = getScoreConfig()
    private val playerAndScoreList by lazy { getPlayerAndScoreLists() }
    private val operators =
        MutableList(1) {
            OperatorAdapter.Companion.Operator(
                -1,
                playerAndScoreList,
                scoreConfig
            )
        }


    private val wakeLock by lazy {
        val powerManager =
            getSystemService(requireContext(), PowerManager::class.java) as PowerManager
        powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "ZhuiFenStartFragment::MyWakeLockTag"
        )
    }

    override fun getLayoutId() = R.layout.fragment_zhuifen_start

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        wakeLock.setReferenceCounted(false)
        wakeLock.acquire(10 * 60 * 1000L)

        initView(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView(view: View) {
        val rvTotalScore = view.findViewById<RecyclerView>(R.id.rv_total_score)
        val totalScoreAdapter = TotalScoreAdapter(playerAndScoreList)
        rvTotalScore.adapter = totalScoreAdapter
        rvTotalScore.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        totalScoreAdapter.setOnItemClickListener { pos ->
            playerAndScoreList.find { it.selected }?.selected = false
            playerAndScoreList[pos].selected = true
            currentSelectPlayer = pos
            totalScoreAdapter.notifyDataSetChanged()
        }

        val rvOperator = view.findViewById<RecyclerView>(R.id.rv_score_operator)
        val operatorAdapter = OperatorAdapter(operators)
        rvOperator.layoutManager = LinearLayoutManager(context)
        rvOperator.adapter = operatorAdapter

        view.findViewById<TextView>(R.id.tv_foul).setOnClickListener {
            val op = OperatorAdapter.Companion.Operator(
                OP_FOUL,
                playerAndScoreList.deepCopy(),
                scoreConfig
            )
            operators.add(op)
            Log.d(TAG, "$op ")
            operateScore(playerAndScoreList, op)
            operatorAdapter.notifyDataSetChanged()
            totalScoreAdapter.notifyDataSetChanged()
            rvOperator.smoothScrollToPosition(operators.lastIndex)
        }
        view.findViewById<TextView>(R.id.tv_foul_r).setOnClickListener {
            val op = OperatorAdapter.Companion.Operator(
                OP_FOUL_R,
                playerAndScoreList.deepCopy(),
                scoreConfig
            )
            operators.add(op)
            operateScore(playerAndScoreList, op)
            operatorAdapter.notifyDataSetChanged()
            totalScoreAdapter.notifyDataSetChanged()
            rvOperator.smoothScrollToPosition(operators.lastIndex)
        }
        view.findViewById<TextView>(R.id.tv_win).setOnClickListener {
            val op = OperatorAdapter.Companion.Operator(
                OP_WIN,
                playerAndScoreList.deepCopy(),
                scoreConfig
            )
            operators.add(op)
            operateScore(playerAndScoreList, op)
            operatorAdapter.notifyDataSetChanged()
            totalScoreAdapter.notifyDataSetChanged()
            rvOperator.smoothScrollToPosition(operators.lastIndex)
        }
        view.findViewById<TextView>(R.id.tv_win_r).setOnClickListener {
            val op = OperatorAdapter.Companion.Operator(
                OP_WIN_R,
                playerAndScoreList.deepCopy(),
                scoreConfig
            )
            operators.add(op)
            operateScore(playerAndScoreList, op)
            operatorAdapter.notifyDataSetChanged()
            totalScoreAdapter.notifyDataSetChanged()
            rvOperator.smoothScrollToPosition(operators.lastIndex)
        }
        view.findViewById<TextView>(R.id.tv_xiaojin).setOnClickListener {
            val op = OperatorAdapter.Companion.Operator(
                OP_XIAOJIN,
                playerAndScoreList.deepCopy(),
                scoreConfig
            )
            operators.add(op)
            operateScore(playerAndScoreList, op)
            operatorAdapter.notifyDataSetChanged()
            totalScoreAdapter.notifyDataSetChanged()
            rvOperator.smoothScrollToPosition(operators.lastIndex)
        }
        view.findViewById<TextView>(R.id.tv_xiaojin_r).setOnClickListener {
            val op = OperatorAdapter.Companion.Operator(
                OP_XIAOJIN_R,
                playerAndScoreList.deepCopy(),
                scoreConfig
            )
            operators.add(op)
            operateScore(playerAndScoreList, op)
            operatorAdapter.notifyDataSetChanged()
            totalScoreAdapter.notifyDataSetChanged()
            rvOperator.smoothScrollToPosition(operators.lastIndex)
        }
        view.findViewById<TextView>(R.id.tv_dajin).setOnClickListener {
            val op = OperatorAdapter.Companion.Operator(
                OP_DAJIN,
                playerAndScoreList.deepCopy(),
                scoreConfig
            )
            operators.add(op)
            operateScore(playerAndScoreList, op)
            operatorAdapter.notifyDataSetChanged()
            totalScoreAdapter.notifyDataSetChanged()
            rvOperator.smoothScrollToPosition(operators.lastIndex)
        }
        view.findViewById<TextView>(R.id.tv_undo).setOnClickListener {
            if (operators.size > 1) {
                val last = operators.removeLast()
                playerAndScoreList.forEachIndexed { index, playerAndScore ->
                    playerAndScore.score = last.currentPlayersAndScore[index].score
                }
            }
            operatorAdapter.notifyDataSetChanged()
            totalScoreAdapter.notifyDataSetChanged()
            rvOperator.smoothScrollToPosition(operators.lastIndex)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        wakeLock.release()
    }

    companion object {
        private fun getPlayerAndScoreLists(): List<PlayerAndScore> {
            return listOf(
                PlayerAndScore("黄麒", "100", true),
                PlayerAndScore("李逸波", "100", false),
                PlayerAndScore("辛宇", "100", false),
            )
        }

        private fun List<PlayerAndScore>.deepCopy(): List<PlayerAndScore> {

            return List(size) {
                PlayerAndScore(this[it].name, this[it].score, this[it].selected)
            }
        }

        data class PlayerAndScore(val name: String, var score: String, var selected: Boolean) {
            override fun toString(): String {
                return "$name($score)"
            }
        }

        const val OP_FOUL = 0
        const val OP_FOUL_R = 1
        const val OP_WIN = 2
        const val OP_WIN_R = 3
        const val OP_XIAOJIN = 4
        const val OP_XIAOJIN_R = 5
        const val OP_DAJIN = 6
        const val OP_UNDO = 7
        private fun PlayerAndScore.addScore(num: Int) {
            score = (score.safeToInt() + num).toString()
        }

        private fun operateScore(
            playerAndScoreList: List<PlayerAndScore>,
            op: OperatorAdapter.Companion.Operator
        ) {
            val selectedIndex = playerAndScoreList.indexOfFirst { it.selected }
            if (selectedIndex == -1) return
            val size = playerAndScoreList.size

            val cur = playerAndScoreList[selectedIndex]
            val last = playerAndScoreList[(selectedIndex - 1 + size) % size]
            val next = playerAndScoreList[(selectedIndex + 1) % size]

            val scoreConfig = op.scoreConfig
            when (op.op) {
                OP_FOUL -> {
                    cur.addScore(-scoreConfig.foul)
                    last.addScore(scoreConfig.foul)
                }
                OP_FOUL_R -> {
                    cur.addScore(-scoreConfig.foul)
                    next.addScore(scoreConfig.foul)
                }
                OP_WIN -> {
                    cur.addScore(scoreConfig.win)
                    last.addScore(-scoreConfig.win)
                }
                OP_WIN_R -> {
                    cur.addScore(scoreConfig.win)
                    next.addScore(-scoreConfig.win)
                }
                OP_XIAOJIN -> {
                    cur.addScore(scoreConfig.xiaojin)
                    last.addScore(-scoreConfig.xiaojin)
                }
                OP_XIAOJIN_R -> {
                    cur.addScore(scoreConfig.xiaojin)
                    next.addScore(-scoreConfig.xiaojin)
                }
                OP_DAJIN -> {
                    playerAndScoreList.forEach {
                        if (it.selected) {
                            it.addScore(scoreConfig.dajin * (size - 1))
                        } else {
                            it.addScore(-scoreConfig.dajin)
                        }
                    }

                }
            }

        }

        private fun getScoreConfig(): ZhuiFenScoreConfig {
            return ZhuiFenScoreConfig()
        }

    }

}