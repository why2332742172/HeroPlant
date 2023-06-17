package me.strawberryyu.heroplant.entity

import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.impl.entity.trait.impl.setTraitTitle
import ink.ptms.adyeshach.impl.entity.trait.impl.setTraitTitleHeight
import me.strawberryyu.heroplant.HeroPlant.conf
import me.strawberryyu.heroplant.hooks.AdyHook.isPlant
import me.strawberryyu.heroplant.manager.PlantManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.function.warning
import taboolib.common.util.asList
import taboolib.common.util.replaceWithOrder
import java.util.*

/**
 * Furnitures
 * me.strawberryyu.heroplant.entity.PlayerData
 *
 * @author 坏黑
 * @since 2023/5/29 00:42
 */
class PlayerData(val player: Player) {

    private val plants = arrayListOf<Plant>()

    /** 数据是否加载 */
    var isLoaded = false
        private set

    init {
        submitAsync {
            plants += PlantManager.storages.get(player.uniqueId)
            init()
            isLoaded = true
        }
    }

    /** 初始化家具 */
    fun init() {
        plants.forEach { plant ->
            if (plant.active) {
                val world = Bukkit.getWorld(plant.world)
                if (world == null) {
                    warning("Cannot find world ${plant.world}.")
                    return@forEach
                }
                val loc = Location(world, plant.x, plant.y, plant.z, plant.yaw, plant.pitch)
                val plantConfig = PlantManager.plantConfigs[plant.plantItem]
                if (plantConfig == null) {
                    warning("Cannot find plant ${plant.plantItem}.")
                    return@forEach
                }
                val growedTime = getGrowedTime(plant.plantUid)
                if (growedTime != -1L) {
                    submit(delay = 20){
                        plantConfig.spawn(player, loc, plant.plantUid, plant.plantItem, growedTime)
                    }
                }
            }
        }
    }

    fun goStage(plantUid: UUID, toStage: Int) {
        val plant = plants.find { it.plantUid == plantUid && it.active } ?: return
        val zapId = plant.plantItem
        val config = PlantManager.getPlantConfig(zapId) ?: return
        val firstStage = config.stageMap.keys.first()
        var time = System.currentTimeMillis()
        for (i in firstStage until toStage) {
            val add = config.stageMap[i]!!.getTimeToNext()
            time -= add
        }
        plant.time = time
        plant.update()
        checkStageAndChange(plantUid)
    }

    fun checkStageAndChange(uid: UUID) {
        val manager = ink.ptms.adyeshach.core.Adyeshach.api().getPrivateEntityManager(player, ManagerType.TEMPORARY)
        val plant = plants.find { it.plantUid == uid && it.active } ?: return
        if (!plant.active) {
            return
        }
        val zapId = plant.plantItem
        val config = PlantManager.getPlantConfig(zapId)
        if (config != null) {
            val stage = config.getStage(getGrowedTime(plant.plantUid))
            if (stage > config.getLastStage()) {
                return
            }
            val nowStage = config.stageMap[stage]
            if (nowStage != null) {
                val entity = manager.getEntity {
                    it.isPlant()
                            &&
                            UUID.fromString(it.getTag("plant_uid").toString()) == plant.plantUid
                            &&
                            it.getTag("plant_item_id").toString() == plant.plantItem
                }
                entity?.setCustomName(nowStage.stageName)
                entity?.setTraitTitle(getTitle(nowStage,getTimeUntilNextPhase(plant.plantUid)).asList())
                entity?.setTraitTitleHeight(nowStage.titleHeight)
            }

        }
    }

    fun checkStageAndChange() {
        val manager = ink.ptms.adyeshach.core.Adyeshach.api().getPrivateEntityManager(player, ManagerType.TEMPORARY)
        for (plant in plants) {
            if (!plant.active) {
                continue
            }
            val zapId = plant.plantItem
            val config = PlantManager.getPlantConfig(zapId)
            if (config != null) {
                val stage = config.getStage(getGrowedTime(plant.plantUid))
                if (stage > config.getLastStage()) {
                    continue
                }
                val nowStage = config.stageMap[stage]
                if (nowStage != null) {

                    val entity = manager.getEntity {
                        it.isPlant() &&
                                UUID.fromString(it.getTag("plant_uid").toString()) == plant.plantUid
                                &&
                                it.getTag("plant_item_id").toString() == plant.plantItem
                    }
                    entity?.setCustomName(nowStage.stageName)
                    entity?.setTraitTitle(getTitle(nowStage,getTimeUntilNextPhase(plant.plantUid)).asList())
                    entity?.setTraitTitleHeight(nowStage.titleHeight)
                }

            }
        }
    }

    fun addPlant(location: Location, uid: UUID, zapItemId: String) {
        // 如果作物已经存在，则更新信息
        val plant = plants.find { it.plantUid == uid }
        if (plant != null) {
            plant.world = location.world!!.name
            plant.x = location.x
            plant.y = location.y
            plant.z = location.z
            plant.yaw = location.yaw
            plant.pitch = location.pitch
            plant.active = true
            plant.update()
        } else {
            // 否则创建新的家具
            val time = System.currentTimeMillis()
            plants += Plant(
                player.uniqueId,
                uid,
                zapItemId,
                time,
                location.world!!.name,
                location.x,
                location.y,
                location.z,
                location.yaw,
                location.pitch,
                true
            ).update()
        }
    }

    fun removePlant(uid: UUID) {
        val plant = plants.find { it.plantUid == uid && it.active }
        if (plant != null) {
            plant.active = false
            plant.update()
        }
    }

    fun count(): Int {
        return plants.count { it.active }
    }


    fun getGrowedTime(plantUid: UUID): Long {
        val plant = plants.find { it.plantUid == plantUid && it.active}
        return if (plant != null) {
            //获取它的种植时间 根据当前时间 计算时间差值 然后获得阶段
            val now = System.currentTimeMillis()
            val originTime = plant.time
            val diff = now - originTime
            diff
        } else {
            -1
        }
    }

    fun getTimeUntilNextPhase(plantUid: UUID) : Long{
        val plant = plants.find { it.plantUid == plantUid && it.active}
        if(plant != null) {
            //首先获得当前阶段 然后获得到下一阶段的时间
            //然后获得当前已经长了的时间
            //然后距离下一阶段的时间-已经长了的时间就是还剩下的时间
            val config = PlantManager.getPlantConfig(plant.plantItem) ?: return -1L
            var growedTime = getGrowedTime(plantUid)
            val stageNum = config.getStage(growedTime)
            if (stageNum >= config.getLastStage()) {
                //成熟了
                return -2L
            }
            val stage = config.getNowStage(stageNum) ?: return -1L
            for (i in config.stageMap.keys.first() until stageNum) {
                growedTime -= config.getNowStage(i)!!.getTimeToNext()
            }
            val nextTime = stage.getTimeToNext()
            return nextTime - growedTime
        }
        return -1L

    }

    fun getTitle(stage: GrowStage, timeUntilNext: Long): String {
        return stage.title.replaceWithOrder(formatTime(timeUntilNext))
    }

    fun formatTime(millis: Long): String {

        if(millis == -2L){
            return ""
        }

        if(millis == -1L){
            return "错误"
        }

        var remainingMillis = millis

        val hours = remainingMillis / (60 * 60 * 1000)
        remainingMillis %= (60 * 60 * 1000)

        val minutes = remainingMillis / (60 * 1000)
        remainingMillis %= (60 * 1000)

        val seconds = remainingMillis / 1000

        return when {
            hours > 0 -> "${hours}${conf.getString("h")}${minutes}${conf.getString("m")}${seconds}${conf.getString("s")}"
            minutes > 0 -> "${minutes}${conf.getString("m")}${seconds}${conf.getString("s")}"
            else -> "${seconds}${conf.getString("s")}"
        }
    }

    fun hasPlant(location: Location): Boolean {
        val plant = plants.find {
            it.world == location.world?.name
                    &&
                    it.x == location.x
                    &&
                    it.y == location.y
                    &&
                    it.z == location.z
                    &&
                    it.active
        }
        return (plant != null && plant.active)
    }
}