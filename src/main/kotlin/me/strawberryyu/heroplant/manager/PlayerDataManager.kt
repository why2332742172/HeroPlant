package me.strawberryyu.heroplant.manager

import me.strawberryyu.heroplant.HeroPlant.tell
import me.strawberryyu.heroplant.entity.PlayerData
import org.bukkit.entity.Player
import taboolib.common.platform.function.warning
import java.util.*

object PlayerDataManager {

    val allData : MutableMap<UUID,PlayerData> = mutableMapOf()

    fun getData(player: Player):PlayerData?{
        val uuid = player.uniqueId
        if(!allData.containsKey(uuid)){
            allData[uuid] = PlayerData(player)
        }

        val playerData = allData[uuid]!!

        return playerData
    }

}