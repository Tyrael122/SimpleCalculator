package com.makesoftware.simplecalculator

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ezylang.evalex.Expression
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState = _uiState.asStateFlow()

    fun onButtonPressed(char: String) {
        _uiState.value = uiState.value.copy(
            expression = "${uiState.value.expression}$char"
        )
    }

    fun onEqualsPressed() {
        val expression = uiState.value.expression
        val result = try {
            val floatValue = Expression(expression).evaluate().numberValue.toFloat()
            if (floatValue % 1 == 0F) {
                floatValue.toInt()
            } else {
                floatValue
            }

        } catch (e: Exception) {
            Log.e("CalculatorViewModel", "Exception: ${e.message}")
            _uiState.update {
                it.copy(
                    error = e.message ?: "Unknown error"
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                expression = result.toString()
            )
        }
    }

    fun onClearPressed() {
        _uiState.value = uiState.value.copy(
            expression = "", result = "", error = ""
        )
    }
}

data class CalculatorUiState(
    val expression: String = "", val result: String = "", val error: String = ""
)
