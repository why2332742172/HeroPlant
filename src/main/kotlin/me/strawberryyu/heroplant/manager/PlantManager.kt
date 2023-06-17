package me.strawberryyu.heroplant.manager

import ink.ptms.zaphkiel.Zaphkiel
import me.strawberryyu.heroplant.HeroPlant
import me.strawberryyu.heroplant.HeroPlant.tell
import me.strawberryyu.heroplant.database.DataBase
import me.strawberryyu.heroplant.entity.GrowStage
import me.strawberryyu.heroplant.entity.PlantConfig
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.util.asList
import taboolib.common.util.unsafeLazy

object PlantManager {

    val storages by unsafeLazy { DataBase() }

    //<zapid,config>
    val plantConfigs : MutableMap<String,PlantConfig> = mutableMapOf()

    //存储全部的Plant限制
    val allRestrict : MutableMap<String,Pair<List<String>,List<String>>> = mutableMapOf()

    fun getRestrict(regionId : String):Pair<List<String>,List<String>>?{
        return allRestrict[regionId]
    }

    fun getPlantConfig(zapid : String):PlantConfig?{
        return plantConfigs[zapid]
    }

    @Awake(LifeCycle.ENABLE)
    fun load(){
        allRestrict.clear()
        val section = HeroPlant.conf.getConfigurationSection("Plant")
        if(section != null){
            for (key in section.getKeys(false)) {
                val pair = Pair(section.getStringList("${key}.plant"),section.getStringList("${key}.plant_block"))
                allRestrict[key] = pair
            }
        }

        plantConfigs.clear()
        Zaphkiel.api().getItemManager().getItemMap().forEach { (id, zapitem) ->
            val plantSection = zapitem.config.getConfigurationSection("plant")
            if(plantSection != null){
                //是plant
                val deleteBack = plantSection.getBoolean("delete-back")
                val stageMap = sortedMapOf<Int,GrowStage>()
                for (s in plantSection.getStringList("grow")) {
                    val split = s.split(" ")
                    val stage = GrowStage(split[1],split[2],split[3],split[4].toDouble())
                    stageMap[split[0].toInt()] = stage
                }

                val pickList = plantSection.getStringList("pick")
                val pickGo = plantSection.getString("pick-go","delete")!!

                val plantConfig = PlantConfig(
                    deleteBack,
                    stageMap,
                    pickList,
                    pickGo
                )

                plantConfigs[id] = plantConfig
                tell("已加载Plant: $id --详情为: ${plantConfig.toString()}")

            }

        }
    }

}