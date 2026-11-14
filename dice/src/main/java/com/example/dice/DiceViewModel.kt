package com.example.dice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.concurrent.thread
import kotlin.random.Random

class DiceViewModel : ViewModel() {

    private val _diceValues = MutableLiveData(List(5) { 1 })
    val diceValues: LiveData<List<Int>> = _diceValues

    private val _isRolling = MutableLiveData(false)
    val isRolling: LiveData<Boolean> = _isRolling

    private val currentValues = IntArray(5) { 1 }
    private val rollingThreads = mutableListOf<Thread>()

    fun rollDice() {
        if (_isRolling.value == true) return
        _isRolling.value = true
        rollingThreads.clear()

        val diceThreads = List(5) { index ->
            thread {
                try {
                    val steps = Random.nextInt(15, 31)
                    repeat(steps) {
                        synchronized(currentValues) {
                            currentValues[index] = Random.nextInt(1, 7)
                            _diceValues.postValue(currentValues.toList())
                        }
                        Thread.sleep(100)
                    }
                } catch (e: InterruptedException) {
                }
            }
        }

        val waiter = thread {
            try {
                diceThreads.forEach { it.join() }
                _isRolling.postValue(false)
            } catch (e: InterruptedException) {
            }
        }

        rollingThreads.addAll(diceThreads)
        rollingThreads.add(waiter)
    }

    override fun onCleared() {
        super.onCleared()
        rollingThreads.forEach { it.interrupt() }
    }
}