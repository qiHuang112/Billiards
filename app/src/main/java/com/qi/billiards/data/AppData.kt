package com.qi.billiards.data

import com.qi.billiards.bean.Game
import com.qi.billiards.bean.GlobalPlayer
import com.qi.billiards.bean.Player
import com.qi.billiards.http.api
import com.qi.billiards.http.apiHost
import com.qi.billiards.util.*
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

    // 服务器保存的每个key对应的数据条数
    var remoteSize = mutableMapOf<String, Int>()
        private set

    // 是否是在application启动后或者key更新后首次进入mainFragment
    var needUpdateRemoteKeyInMainFragment = true

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
        globalPlayer[key]?.clear()
        value.forEach { game ->
            game.players.forEach {
                addPlayer(key, it)
            }
        }
        save(KEY_GLOBAL_GAMES + key, value.toJson())

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
        if (player.profit > 0) {
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


    suspend fun updateRemoteSize(key: String) {
        api.getSize(apiHost, key).data
            ?.first()
            ?.safeAs<Double>()
            ?.toInt()
            ?.let {
                remoteSize[key] = it
            }
    }

    /**
     * 返回值大于0表示需要拉数据
     * 返回值小于0表示需要上传数据
     */
    fun getRemoteSizeDiff(key: String): Int {
        return (remoteSize[key] ?: 0) - (globalGames[key]?.size ?: 0)
    }

    //用内存中的对应key-size，更新对应key的size
    fun updateRemoteSizeByAppData(key: String) {
        remoteSize[key] = globalGames[key]?.size ?: 0
    }

}