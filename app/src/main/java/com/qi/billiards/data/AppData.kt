package com.qi.billiards.data

import com.qi.billiards.bean.Game
import com.qi.billiards.bean.GlobalPlayer
import com.qi.billiards.bean.Player
import com.qi.billiards.util.fromJson
import com.qi.billiards.util.get
import com.qi.billiards.util.save
import com.qi.billiards.util.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope


/**
 * 保存运行时数据
 * 并在数据改动后保存到文件
 */
object AppData : CoroutineScope by MainScope() {

    private const val KEY_GLOBAL_GAMES = "global_games_"
    const val KEY_GAMES_KEY = "games_key_"
    var globalGames = mutableMapOf<String, List<Game>>()
        private set
    var globalPlayer = mutableMapOf<String, MutableMap<String, GlobalPlayer>>()
        private set
    var keys = mutableSetOf<String>()
        private set
    var keyUpdated = false

    fun initFromSp() {

        keys = get(KEY_GAMES_KEY, "").fromJson<MutableSet<String>>() ?: mutableSetOf()

        globalGames = keys.associateWith {
            get(KEY_GLOBAL_GAMES + it, "").fromJson<Array<Game>>()?.toMutableList() ?: listOf()
        }.toMutableMap()

        globalGames.forEach { (key, games) ->
            games.forEach { game ->
                game.players.forEach { player ->
                    addPlayer(key, player)
                }
            }
        }
    }

    fun addGlobalGame(key: String, value: List<Game>) {
        globalGames[key] = value.toMutableList()
        value.forEach(::addGame)
    }

    fun addGame(game: Game) {
        if (game.type.isEmpty()) {
            return
        }
        val games = globalGames[game.type]?.toMutableList() ?: mutableListOf()
        games.add(game)
        globalGames[game.type] = games

        game.players.forEach {
            addPlayer(game.type, it)
        }

        saveToSp()
    }

    private fun addPlayer(type: String, player: Player) {
        if (player.name.isEmpty()) {
            return
        }
        if (globalPlayer[type] == null) {
            globalPlayer[type] = mutableMapOf()
        }
        val globalPlayer = globalPlayer[type]!![player.name] ?: GlobalPlayer(player.name)
        globalPlayer.totalProfit += player.profit
        globalPlayer.totalCount++
        globalPlayer.totalCost += player.cost
        if (player.score > 0) {
            globalPlayer.winCount++
        }
        this.globalPlayer[type]!![player.name] = globalPlayer
    }

    private fun saveToSp() {
        globalGames.forEach {
            save(KEY_GLOBAL_GAMES + it.key, it.value.toJson())
        }
    }

    fun updateKeys(games: MutableList<String>) {
        keys.clear()
        keys.addAll(games)
        save(KEY_GAMES_KEY, keys.toJson())
        keyUpdated = true
    }


}