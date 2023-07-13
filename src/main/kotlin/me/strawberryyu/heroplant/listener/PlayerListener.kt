package me.strawberryyu.heroplant.listener

import ink.ptms.zaphkiel.api.event.ItemEvent
import me.strawberryyu.heroplant.entity.PlayerData
import me.strawberryyu.heroplant.hooks.WorldGuardHooks
import me.strawberryyu.heroplant.manager.PlantManager
import me.strawberryyu.heroplant.manager.PlayerDataManager
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.isNotAir
import java.util.*

object PlayerListener {

    @SubscribeEvent
    fun plant(e: ItemEvent.Interact) {
        if (e.hand == EquipmentSlot.HAND && e.action == Action.RIGHT_CLICK_BLOCK && !e.player.isSneaking) {
            if (e.clickedBlock == null) {
                return
            }
            val section = e.itemStream.getZaphkielItem().config.getConfigurationSection("plant") ?: return

            val id = e.itemStream.getZaphkielId()

            val plantLoc = e.clickedBlock!!.location.add(0.0, 1.0, 0.0)

            if(!WorldGuardHooks.canPlant(e.player,plantLoc,id,XMaterial.matchXMaterial(e.clickedBlock!!.type),e.clickedBlock!!.location)){
                return
            }



            val regionName = WorldGuardHooks.getRegionName(e.clickedBlock!!.location) ?: return
            //是种子 则直接种植
            if (e.item.isNotAir()) {
                val plantConfig = PlantManager.getPlantConfig(id) ?: return
                val data = PlayerDataManager.getData(e.player) ?: return
                e.item!!.amount -= 1

                val uid = UUID.randomUUID()
                data.addPlant(plantLoc, uid, id)

                plantConfig.spawn(
                    e.player,
                    plantLoc,
                    uid,
                    id,
                    data.getGrowedTime(uid)
                )


            }
        }


    }

    @SubscribeEvent
    fun join(e: PlayerJoinEvent) {
        PlayerDataManager.allData[e.player.uniqueId] = PlayerData(e.player)
    }

    @SubscribeEvent
    fun quit(e: PlayerQuitEvent) {
        PlayerDataManager.allData.remove(e.player.uniqueId)
    }

}