package me.strawberryyu.heroplant.utils

import taboolib.common.util.random

object AmountUtil {

    fun parseQuantity(quantity: String): Int {
        // 检查字符串是否包含"-"
        return if (quantity.contains("-")) {
            // 分割字符串并将结果转换为Int
            val rangeBounds = quantity.split("-").map { it.trim().toInt() }
            // 从指定的范围中随机选择一个数
            random(rangeBounds[0],rangeBounds[1])
//            Random.nextInt(rangeBounds[0], rangeBounds[1] + 1)
        } else {
            // 将字符串转换为Int
            quantity.toInt()
        }
    }
}