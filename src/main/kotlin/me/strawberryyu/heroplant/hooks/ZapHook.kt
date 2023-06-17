package me.strawberryyu.heroplant.hooks

import ink.ptms.zaphkiel.Zaphkiel
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ZapHook {

    fun giveItem(id: String, player: Player, amount: Int = 1) {
        Zaphkiel.api().getItemManager().giveItem(player,id,amount)
    }

}