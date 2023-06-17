package me.strawberryyu.heroplant.hooks

import ink.ptms.adyeshach.core.entity.EntityInstance

object AdyHook {

    fun EntityInstance.isPlant():Boolean{
        return (this.hasTag("plant_item_id") && this.hasTag("plant_uid"))
    }
}