package com.qi.billiards.ui.main.zhuifen.start

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.config.Config
import com.qi.billiards.databinding.FragmentZhuifenBinding
import com.qi.billiards.db.DbUtil
import com.qi.billiards.game.Operator
import com.qi.billiards.game.Player
import com.qi.billiards.game.Round
import com.qi.billiards.game.addOpProfit
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.ui.main.SETTINGS_KEY_
import com.qi.billiards.ui.main.yybb
import com.qi.billiards.ui.widget.SummaryDialog
import com.qi.billiards.util.get
import com.qi.billiards.util.toast
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

private const val TAG = "ZhuiFenFragment"

class ZhuiFenFragment : BaseBindingFragment<FragmentZhuifenBinding>() {
    private val args: ZhuiFenFragmentArgs by navArgs()
    private val game by lazy { args.zhuiFenGame } // 一场游戏，包含多局游戏
    private var currentPlayerIndex = -1
    private val enableTTS by lazy { get(SETTINGS_KEY_ + yybb, false) }
    private lateinit var textToSpeech: TextToSpeech
    private val currentRound: Round // 当前游戏
        get() {
            return game.group.first()
        }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentZhuifenBinding {
        return FragmentZhuifenBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initTTS()
        initView()
    }

    private fun initTTS() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // TTS引擎初始化成功
                val result = textToSpeech.setLanguage(Locale.CHINESE) // 设置语言
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 语言数据丢失或不支持
                    Log.e("TTS", "Language not supported")
                } else {
                    Log.d("TTS", "Initialization success")
                }
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }
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

    private fun speak(text: String) {
        Log.d(TAG, "speak:$text")
        if (enableTTS) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
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
            val totalCount = game.group.size - if (currentRound.roundOver()) 0 else 1
            String.format("%.2f%%", 100.0 * totalWin / totalCount)
        }) + listOf(listOf("败率") + game.summaries.keys.map { name ->
            val totalLose = game.summaries[name]?.getOrDefault("败局", 0) ?: 0
            val totalCount = game.group.size - if (currentRound.roundOver()) 0 else 1
            String.format("%.2f%%", 100.0 * totalLose / totalCount)
        })

        summaryDialog.updateData(tableData)
        summaryDialog.show()

    }

    private fun saveToDb() {
        launch {
            val gameId = DbUtil.addOrUpdateGame(game.toEntity())
            if (game.id == null) {
                game.id = gameId
                DbUtil.addOrUpdateGame(game.toEntity())
            }
        }
    }

    private fun initGame() {
        Log.d(TAG, "initGame: game id = ${game.id}")
        if (!args.reload) { // 是否要加一个进行中的状态，自动开始第一把
            val sequences = game.players.map { it.name }
            game.group.add(Round(sequences, Round.Profits(game.getRoundPlayers())))
        }
        if (game.id == null) {
            Log.d(TAG, "当前游戏是新开的游戏")
        } else {
            Log.d(TAG, "当前游戏是从历史记录进入的游戏")
        }
        saveToDb()
    }

    private fun initScoreBoard() {
        binding.rvScoreBoard.adapter = ScoreBoardAdapter(game.players) {
            currentPlayerIndex = it
            binding.rvOperatorGrid.visibility = if (it == -1) View.GONE else View.VISIBLE
        }
        binding.rvScoreBoard.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    private tailrec fun onClickUndo() {
        if (currentRound.operators.isNotEmpty()) {
            val operator = currentRound.operators.removeAt(currentRound.operators.lastIndex) // 删掉操作记录
            binding.rvGameBoard.adapter?.notifyItemChanged(0)

            val operatorProfit = getOperatorProfit(operator, true)
            currentRound.profits.opProfits.removeAt(currentRound.profits.opProfits.lastIndex) // 删掉单轮最后一次收益记录
            currentRound.profits.totalProfits.addOpProfit(operatorProfit) // 重新计算总收益
            game.players.addOpProfit(operatorProfit) // 重新计算总收益
            addToTotalScore(operatorProfit)
            binding.rvScoreBoard.adapter?.notifyItemRangeChanged(0, game.players.size)
        } else if (game.group.size > 1) {
            game.group.removeAt(0)
            binding.rvGameBoard.adapter?.notifyItemRemoved(0)
            onClickUndo()
        }
    }

    private fun initOperatorGrid() {
        binding.rvOperatorGrid.adapter =
            OperatorGridAdapter(Config.ZhuiFen.userOperators) { id ->
                if (id == Config.ZhuiFen.OP_7) {
                    onClickUndo()
                    speak("撤回")
                } else if (currentPlayerIndex == -1) {
                    toast("请选择玩家")
                } else {
                    val operator = Operator(id, game.players[currentPlayerIndex])
                    currentRound.operators.add(operator) // 操作记录
                    val operatorProfit = getOperatorProfit(operator)
                    val detailString = getDetailString(game.players[currentPlayerIndex].name, id, operatorProfit)
                    speak(detailString)
                    currentRound.profits.opProfits.add(operatorProfit) // 单局总收益统计
                    currentRound.profits.totalProfits.addOpProfit(operatorProfit) // 单局单次收益记录
                    game.players.addOpProfit(operatorProfit) // 整场游戏总收益统计
                    addToTotalScore(operatorProfit)
                    binding.rvGameBoard.adapter?.notifyItemChanged(0)
                    if (currentRound.roundOver()) {
                        // 当轮游戏结束，记录当轮结束时间，自动开启下一轮
                        currentRound.during.endTime = Date()
                        game.group.add(0, Round(getSequences(currentRound), Round.Profits(game.getRoundPlayers())))
                        binding.rvGameBoard.adapter?.notifyItemInserted(0)
                    }
                    binding.rvGameBoard.scrollToPosition(0)
                    binding.rvScoreBoard.adapter?.notifyItemRangeChanged(0, game.players.size)
                }
                saveToDb()
            }
        binding.rvOperatorGrid.layoutManager = GridLayoutManager(context, 4)
    }

    private fun getDetailString(name: String, operatorId: Int, operatorProfit: List<Player>): String {
        return buildString {
            append(name)
            append(Config.ZhuiFen.opMap.getOrDefault(operatorId, "[未定义操作]"))
            append(",")
            val currentPlayer = operatorProfit.first { player -> player.name == name }
            append(score2Text(currentPlayer.score))
            append(",")
            operatorProfit.filter { player -> player.name != name && player.score != 0 }.forEach { player ->
                append(player.name)
                append(",")
                append(score2Text(player.score))
                append(",")
            }
        }
    }

    private fun score2Text(score: Int): String {
        return if (score >= 0) {
            "得分${score}分"
        } else {
            "扣分${score.absoluteValue}分"
        }
    }

    private fun addToTotalScore(operatorProfit: List<Player>) {
        Log.d(TAG, "addToTotalScore: " + operatorProfit.joinToString { "${it.name}:${it.score}" })
        launch {
            val players = operatorProfit.map {
                DbUtil.getPlayerById(it.id).also { entity ->
                    if (entity != null) {
                        entity.totalScore += it.score * game.base
                    }
                }
            }
            Log.d(TAG, "totalScore: " + players.joinToString { "${it?.playerName}:${it?.totalScore}" })
            DbUtil.addPlayers(*players.filterNotNull().toTypedArray())
        }
    }

    /**
     * 拿到当前操作，产生的分数变化
     */
    private fun getOperatorProfit(operator: Operator, undo: Boolean = false): List<Player> {
        val sign = if (undo) -1 else 1
        val profits = game.players.map { player -> player.copy(name = player.name, score = 0) }
        val sequence = currentRound.sequences // 顺序表
        val opPlayer = operator.player.name // 操作玩家
        val rule = game.rule
        val curIndex = sequence.indexOf(opPlayer)
        if (curIndex == -1) return profits
        val last = if (curIndex == 0) sequence.last() else sequence[curIndex - 1] // 上家
        val next = if (curIndex == sequence.lastIndex) sequence.first() else sequence[curIndex + 1] // 下家
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

    companion object {
        /**
         * 从上一轮游戏拿到本次追分顺序
         */
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
        private fun Round.roundOver(): Boolean {
            return operators.isNotEmpty() && operators.last().id > Config.ZhuiFen.OP_1
        }
    }
}