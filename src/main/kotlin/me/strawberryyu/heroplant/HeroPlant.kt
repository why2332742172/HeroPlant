package me.strawberryyu.heroplant

import me.strawberryyu.heroplant.utils.PlantStageWatcher
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import taboolib.common.platform.Plugin
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.platform.BukkitPlugin

object HeroPlant : Plugin() {

    @Config
    lateinit var conf: Configuration
        private set


    val plugin by lazy {
        BukkitPlugin.getInstance()
    }

    var prefix = "&a[HERO-PLANT]".colored()

    override fun onEnable() {
        prefix = conf.getString("prefix", "&a[HERO-PLANT]")?.colored()!!

        PlantStageWatcher()
    }

    fun tell(msg: String) {
        Bukkit.getConsoleSender().sendMessage("$prefix $msg".colored())
    }

    fun CommandSender.tell(msg: String) {
        this.sendMessage("$prefix $msg".colored())

    }

    fun reload(){
        conf.reload()
    }
}