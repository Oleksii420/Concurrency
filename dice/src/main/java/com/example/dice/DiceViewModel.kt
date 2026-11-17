package com.example.dice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DiceViewModel : ViewModel() {

    private val model = DiceModel()

    private val _diceValues = MutableLiveData(List(5) { 1 })
    val diceValues: LiveData<List<Int>> = _diceValues

    private val _isRolling = MutableLiveData(false)
    val isRolling: LiveData<Boolean> = _isRolling
    private val currentValues = IntArray(5) { 1 }

    private var job: Job? = null

    private val _buttonText = MutableLiveData("ROLL")
    val buttonText: LiveData<String> = _buttonText

    fun rollDice() {
        job?.cancel()

        _isRolling.value = true
        _buttonText.value = "RESTART"

        job = viewModelScope.launch {
            model.rollAll { index, value ->
                currentValues[index] = value
                _diceValues.value = currentValues.toList()
            }
            _isRolling.value = false
            _buttonText.value = "ROLL"
        }
    }
}