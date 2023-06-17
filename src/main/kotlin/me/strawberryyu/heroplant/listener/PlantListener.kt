package me.strawberryyu.heroplant.listener

import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import me.strawberryyu.heroplant.HeroPlant.tell
import me.strawberryyu.heroplant.hooks.AdyHook.isPlant
import me.strawberryyu.heroplant.hooks.ZapHook
import me.strawberryyu.heroplant.manager.PlantManager
import me.strawberryyu.heroplant.manager.PlayerDataManager
import taboolib.common.platform.event.SubscribeEvent
import java.util.*

object PlantListener {

    /**
     * 玩家右键采摘作物
     */
    @SubscribeEvent
    fun pick(e: AdyeshachEntityInteractEvent) {
        if (!e.player.isSneaking && e.isMainHand  && e.entity.isPlant() ) {
            val data = PlayerDataManager.getData(e.player) ?: return
            //获取当前阶段 然后如果是成熟的 那么就执行pick 然后去pickgo
            val zapid = e.entity.getTag("plant_item_id").toString()
            val uuid = UUID.fromString(e.entity.getTag("plant_uid").toString())
            val plantConfig = PlantManager.getPlantConfig(zapid) ?: return
            //看是否成熟
            val stage = plantConfig.getStage(data.getGrowedTime(uuid))
            if(stage != plantConfig.getLastStage()){
                //未成熟
                if(stage > plantConfig.getLastStage()){
                    e.player.tell("作物${zapid} 的阶段${stage} 不存在!请检查配置文件!")
                }
                return
            }
            //执行pick
            plantConfig.pick(e.player)
            //前往pickgo
            if (plantConfig.pickGo == "delete") {
                data.removePlant(uuid)
                e.entity.remove()
            } else {
                //前往指定阶段
                data.goStage(uuid, plantConfig.pickGo.toInt())
            }
        }
    }

    /**
     * 玩家挖掘作物 直接删除
     */
    @SubscribeEvent
    fun dig(e: AdyeshachEntityInteractEvent) {
        if (e.player.isSneaking && e.isMainHand && e.entity.isPlant()) {
            val data = PlayerDataManager.getData(e.player) ?: return
            val tag = e.entity.getTag("plant_uid").toString()
            val uuid = UUID.fromString(tag)
            data.removePlant(uuid)
            e.entity.remove()
            val zapid = e.entity.getTag("plant_item_id").toString()
            val config = PlantManager.getPlantConfig(zapid)?:return
            if(config.deleteBack){
                ZapHook.giveItem(zapid,e.player)
            }

        }
    }
}