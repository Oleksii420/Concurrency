package com.example.dice

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.random.Random

class DiceModel {

    suspend fun rollDie(onUpdate: (Int) -> Unit): Int {
        val steps = Random.nextInt(15, 31)   // від 1,5 до 3 секунд
        var value = 1
        repeat(steps) {
            value = Random.nextInt(1, 7)
            onUpdate(value)
            delay(100)
        }
        return value
    }

    suspend fun rollAll(onUpdate: (index: Int, value: Int) -> Unit): List<Int> = coroutineScope {
        List(5) { index ->
            async {
                rollDie { value -> onUpdate(index, value) }
            }
        }.awaitAll()
    }
}