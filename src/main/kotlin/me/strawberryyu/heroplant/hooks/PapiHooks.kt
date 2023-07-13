package me.strawberryyu.heroplant.hooks

import me.strawberryyu.heroplant.manager.PlayerDataManager
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object PapiHooks : PlaceholderExpansion {

    override val identifier: String = "heroplant"

    //%heroplant_num:region%
    //%heroplant_max:region%
    override fun onPlaceholderRequest(player: Player?, args: String): String {
        if (player != null && player.isOnline) {
            if (args.startsWith("num")) {
                val regionNameSS = args.split(":")
                if (regionNameSS.size < 2) {
                    return "-1"
                }
                val regionName = regionNameSS[1]
                val data = PlayerDataManager.getData(player) ?: return "0"
                return data.allRegionPlantsNum[regionName]?.toString() ?: "0"
            }else if(args.startsWith("max")){
                val regionNameSS = args.split(":")
                if (regionNameSS.size < 2) {
                    return "-1"
                }
                val regionName = regionNameSS[1]
                return LuckPermsHooks.getMaxCanPlantNum(player,regionName).toString()
            }
            return "null"
        }
        return "null"
    }

}