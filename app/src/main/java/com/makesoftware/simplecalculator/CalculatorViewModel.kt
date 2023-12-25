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
        when (char) {
            in CalculatorButtonType.OPERATOR.listOfChars -> addToExpressionList(
                char, CalculatorButtonType.OPERATOR
            )

            in CalculatorButtonType.CLEAR.listOfChars -> onClearPressed()
            in CalculatorButtonType.EQUALS.listOfChars -> onEqualsPressed()
            else -> addInputToExpressionList(char)
        }
    }

    private fun addInputToExpressionList(char: String) {
        val expression = uiState.value.expression
        val lastInput = expression.lastOrNull()

        if (lastInput == null || lastInput.type == CalculatorButtonType.OPERATOR) {
            addToExpressionList(char, CalculatorButtonType.INPUT_TO_EXPRESSION)
            return
        }

        val newExpression = expression.toMutableList()
        newExpression.removeLast()
        newExpression.add(
            InputText(
                lastInput.text + char, CalculatorButtonType.INPUT_TO_EXPRESSION
            )
        )

        _uiState.update {
            it.copy(
                expression = newExpression
            )
        }
    }

    private fun addToExpressionList(char: String, calculatorButtonType: CalculatorButtonType) {
        val newExpression = uiState.value.expression.toMutableList()
        newExpression.add(InputText(char, calculatorButtonType))

        _uiState.update {
            it.copy(
                expression = newExpression
            )
        }
    }

    private fun onEqualsPressed() {
        val textExpression = uiState.value.expression.joinToString("") { it.text }.replace("x", "*")

        val result = try {
            val floatValue = Expression(textExpression).evaluate().numberValue.toFloat()
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
                result = result.toString()
            )
        }
    }

    private fun onClearPressed() {
        _uiState.value = uiState.value.copy(
            expression = emptyList(), result = "", error = ""
        )
    }
}

data class CalculatorUiState(
    val expression: List<InputText> = emptyList(), val result: String = "", val error: String = ""
)

class InputText(
    val text: String, val type: CalculatorButtonType
)

enum class CalculatorButtonType(val listOfChars: List<String>) {
    INPUT_TO_EXPRESSION(emptyList()), OPERATOR(
        listOf(
            "+", "-", "x", "/"
        )
    ),
    CLEAR(listOf("C")), EQUALS(listOf("="))
}
