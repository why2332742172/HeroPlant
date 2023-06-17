package me.strawberryyu.heroplant.listener

import ink.ptms.zaphkiel.api.event.PluginReloadEvent
import me.strawberryyu.heroplant.HeroPlant
import me.strawberryyu.heroplant.manager.PlantManager
import taboolib.common.platform.event.SubscribeEvent

object ZapListener {

    @SubscribeEvent
    fun onZapReload(e: PluginReloadEvent.Item){
        HeroPlant.conf.reload()
        PlantManager.load()
    }
}