package com.qi.billiards.db

import androidx.room.Room
import com.qi.billiards.AppContext
import com.qi.billiards.game.DeGame
import com.qi.billiards.game.DePlayer
import com.qi.billiards.util.fromJson

private val gameDb by lazy {
    Room.databaseBuilder(AppContext, GameDatabase::class.java, "a.db").build()
}

object DbUtil : PlayerDao by gameDb.playerDao(), GameDao by gameDb.gameDao() {
    suspend fun updatePlayersByGames(games: Array<GameEntity>) {
        DbUtil.deleteAllPlayers()
        val playerMap = mutableMapOf<String, PlayerEntity>()

        games.forEach { gameEntity ->
            gameEntity.detail.fromJson<DeGame>()?.let { deGame ->
                deGame.players.forEach { dePlayer ->
                    val player = if (playerMap.contains(dePlayer.name)) {
                        playerMap[dePlayer.name]!!
                    } else {
                        val playerEntity = getPlayerEntity(dePlayer.name)
                        playerMap[dePlayer.name] = playerEntity
                        playerEntity
                    }
                    player.plusAssign(deGame, dePlayer)
                }

            }
        }
        DbUtil.addPlayers(*playerMap.values.toTypedArray())
    }

    suspend fun getPlayerEntity(name: String): PlayerEntity {
        var entity = DbUtil.getPlayerByName(name)
        if (entity == null) {
            entity = PlayerEntity(name)
            val id = DbUtil.addPlayer(entity)
            entity.id = id
        }
        return entity
    }


    private fun PlayerEntity.plusAssign(game: DeGame, dePlayer: DePlayer) {
        this.totalCount++
        this.totalCost += dePlayer.cost
        this.totalScore += (dePlayer.score - (dePlayer.buyCount + 1) * game.configs["单次买入"]!!.toInt()) / game.configs["汇率"]!!
        val win = if (dePlayer.profit > 0) 1 else 0
        this.winCount += win
    }
}