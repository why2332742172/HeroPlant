package me.strawberryyu.heroplant.hooks

import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.cacheddata.CachedMetaData
import org.bukkit.entity.Player
import taboolib.common.platform.function.info


object LuckPermsHooks {

    fun getMaxCanPlantNum(player: Player, meta: String): Int {
        val metaKey = "plant_$meta"
        val metaData: CachedMetaData = LuckPermsProvider.get().getPlayerAdapter(Player::class.java).getMetaData(player)
        val metaValue = metaData.getMetaValue(metaKey)
        return metaValue?.toInt() ?: 0
    }

}