package me.strawberryyu.heroplant.utils

import me.strawberryyu.heroplant.manager.PlayerDataManager
import taboolib.common.platform.function.submitAsync
import taboolib.platform.util.onlinePlayers

class PlantStageWatcher {

    init {
        submitAsync(period = 20){
            //异步每秒检测一次全部的plant
            for (player in onlinePlayers) {
                val data = PlayerDataManager.getData(player)
                data?.checkStageAndChange()
            }
        }
    }

}