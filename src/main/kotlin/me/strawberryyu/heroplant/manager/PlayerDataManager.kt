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

        return if(playerData.isLoaded){
            playerData
        }else{
            player.tell("信息还未加载完毕!请重新进入服务器!")
            warning("玩家${player.name}的信息还未加载完毕!请重新进入服务器!")
            null
        }
    }

}