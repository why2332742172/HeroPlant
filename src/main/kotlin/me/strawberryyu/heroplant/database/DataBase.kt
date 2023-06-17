package me.strawberryyu.heroplant.database

import me.strawberryyu.heroplant.entity.Plant
import taboolib.expansion.persistentContainer
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DataBase {

    val container = persistentContainer { new<Plant>("player_plant") }

    /** 线程池 */
    val pool: ExecutorService = Executors.newFixedThreadPool(16)

    /** 获取数据 */
    fun get(username: UUID): List<Plant> {
        return container["player_plant"].find(username)
    }

    /** 写入数据 */
    fun update(plant: Plant) {
        pool.submit { container["player_plant"].updateByKey(plant) }
    }

    fun close() {
        container.close()
    }
}