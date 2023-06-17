package me.strawberryyu.heroplant.entity

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import me.strawberryyu.heroplant.database.DataBase
import me.strawberryyu.heroplant.manager.PlantManager
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.expansion.BundleMap
import taboolib.expansion.Id
import taboolib.expansion.Key
import taboolib.expansion.Length
import java.util.*

data class Plant(

    //作物主人
    @Id
    val username: UUID,

    @Key
    val plantUid: UUID,

    @Length(32)
    val plantItem: String,

    //作物种下的时间
    var time : Long,

    //作物的位置
    @Length(32)
    var world: String,
    var x: Double,
    var y: Double,
    var z: Double,
    var yaw: Float,
    var pitch: Float,

    //是否有效
    var active: Boolean,

) {

    /** 更新数据 */
    fun update(): Plant {
        PlantManager.storages.update(this)
        return this
    }


    companion object {

        @JvmStatic
        fun wrap(map: BundleMap): Plant {
            return Plant(
                map["username"],
                map["plant_uid"],
                map["plant_item"],
                map["time"],
                map["world"],
                map["x"],
                map["y"],
                map["z"],
                map["yaw"],
                map["pitch"],
                map["active"]
            )
        }
    }
}