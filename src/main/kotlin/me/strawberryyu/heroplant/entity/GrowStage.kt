package me.strawberryyu.heroplant.entity

import java.util.regex.Pattern

class GrowStage(
    val timeToNext : String,
    val stageName : String,
    val title : String,
    val titleHeight : Double
) {


    fun getTimeToNext(): Long {
        val p = Pattern.compile("(\\d+)([smh])")
        val m = p.matcher(timeToNext)

        var totalMilliseconds: Long = 0
        while (m.find()) {
            val amount = m.group(1).toLong()
            val unit = m.group(2)

            totalMilliseconds += when (unit) {
                "s" -> amount * 1000
                "m" -> amount * 60 * 1000
                "h" -> amount * 60 * 60 * 1000
                else -> throw IllegalArgumentException("Invalid duration unit")
            }
        }
        return totalMilliseconds
    }

    override fun toString(): String {
        return "GrowStage(timeToNext='$timeToNext', stageName='$stageName', title='$title', titleHeight=$titleHeight)"
    }


}