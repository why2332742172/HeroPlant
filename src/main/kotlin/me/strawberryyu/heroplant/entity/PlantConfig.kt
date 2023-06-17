package me.strawberryyu.heroplant.entity

import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.core.entity.type.AdySlime
import ink.ptms.adyeshach.impl.entity.trait.impl.setTraitTitle
import ink.ptms.adyeshach.impl.entity.trait.impl.setTraitTitleHeight
import me.strawberryyu.heroplant.HeroPlant.tell
import me.strawberryyu.heroplant.hooks.ZapHook
import me.strawberryyu.heroplant.manager.PlayerDataManager
import me.strawberryyu.heroplant.utils.AmountUtil
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.util.asList
import taboolib.common.util.replaceWithOrder
import taboolib.library.xseries.XMaterial
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.platform.util.buildItem
import taboolib.platform.util.giveItem
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max

class PlantConfig(
    var deleteBack: Boolean,
    stageMap: MutableMap<Int, GrowStage>,
    pickList: List<String>,
    var pickGo: String

) {

    val stageMap: MutableMap<Int, GrowStage> = sortedMapOf()
    val pickList: MutableList<String> = mutableListOf()

    init {
        this.stageMap.putAll(stageMap)
        this.pickList.addAll(pickList)
    }

    fun getNowStage(stage:Int):GrowStage?{
        return stageMap[stage]
    }

    fun getStage(growedTime: Long):Int{
        var stage = stageMap.keys.first()

        var leftTime = growedTime

        for (entry in stageMap) {
            val timeToNext = entry.value.getTimeToNext()
            if (leftTime >= timeToNext) {
                stage++
                leftTime -= timeToNext
            }else{
                break
            }
        }
        val last = getLastStage()
        return if(stage > last){
            last
        }else{
            stage
        }
    }

    /**
     * 生成作物
     */
    fun spawn(player: Player, location: Location, plantId: UUID, zapItemId: String, growedTime: Long) {

        val data = PlayerDataManager.getData(player)?:return

        val stage = getStage(growedTime)

        val growStage = stageMap[stage]
        if (growStage == null) {
            player.tell("作物${plantId} 的阶段${stage} 不存在!请检查配置文件!")
            return
        } else {
            val stageName = growStage.stageName
            val manager = ink.ptms.adyeshach.core.Adyeshach.api().getPrivateEntityManager(player, ManagerType.TEMPORARY)
            manager.create(EntityTypes.ARMOR_STAND, location.add(0.5,0.0,0.5)) {
                it.setCustomNameVisible(true)
                it.setCustomName(stageName)
                it.setTag("plant_uid", plantId)
                it.setTag("plant_item_id", zapItemId)
                it.setTraitTitle(data.getTitle(growStage,data.getTimeUntilNextPhase(plantId)).asList())
                it.setTraitTitleHeight(growStage.titleHeight)
            }
        }
    }


    override fun toString(): String {
        return "PlantConfig(deleteBack=$deleteBack, pickGo='$pickGo', stageMap=$stageMap, pickList=$pickList)"
    }

    fun getLastStage():Int{
        return stageMap.keys.last()
    }

    fun pick(p : Player){
        for (s in this.pickList) {
            if(s.startsWith("v:")){
                val replace = s.replace("v:", "")
                val split = replace.split(" ")
                var amount = 1
                if(split.size > 1){
                    amount = AmountUtil.parseQuantity(split[1])
                }
                val mat = XMaterial.matchXMaterial(split[0]).get()
                val item = buildItem(mat)
                p.giveItem(item,amount)
            }else if(s.startsWith("z:")){
                val replace = s.replace("z:", "")
                val split = replace.split(" ")
                var amount = 1
                if(split.size > 1){
                    amount = AmountUtil.parseQuantity(split[1])
                }
                val id = split[0]
                ZapHook.giveItem(id,p,amount)
            }else if(s.startsWith("k:")){
                val script = s.replace("k:","")
                KetherShell.eval(script, ScriptOptions.builder().sender(p).build())
            }
        }
    }




}