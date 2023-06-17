package me.strawberryyu.heroplant.hooks

import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.ApplicableRegionSet
import me.strawberryyu.heroplant.HeroPlant.tell
import me.strawberryyu.heroplant.manager.PlantManager
import me.strawberryyu.heroplant.manager.PlayerDataManager
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.asLangText

object WorldGuardHooks {

    /**
     * 判断worldguard区域
     * 同时判断当前位置是否已经种了植物（）
     */
    fun canPlant(p: Player, loc: Location, plantId: String,groundBlockType : XMaterial,regionLoc : Location): Boolean {
        val data = PlayerDataManager.getData(p)
            ?: return false


        if (data.hasPlant(loc)) {
            p.tell(p.asLangText("already-plant"))
            return false
        }


        val name = getRegionName(regionLoc)
        if(name == null){
            p.tell(p.asLangText("area-wrong"))
            return false
        }

        val restrict = PlantManager.getRestrict(name)?:return false
        if(restrict.first.contains(plantId)){
            if(restrict.second.contains(groundBlockType.name)){
                return true
            }
        }
        p.tell(p.asLangText("block-wrong"))
        return false

    }

    fun getRegionName(location: Location): String? {
        // 获取 WorldGuard 插件
        val wgPlugin = WorldGuardPlugin.inst()

        wgPlugin?.let {
            // 获取 RegionManager
            val regionManager = it.getRegionManager(location.world)
            // 获取在特定Location上的所有区域
            val applicableRegions: ApplicableRegionSet = regionManager.getApplicableRegions(location)

            // 遍历并返回第一个找到的区域的名字
            for (region in applicableRegions) {
                return region.id
            }
        }

        // 如果没有找到 WorldGuard 插件或者区域，返回null
        return null
    }


}