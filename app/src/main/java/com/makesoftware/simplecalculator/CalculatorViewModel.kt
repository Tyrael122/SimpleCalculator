package com.makesoftware.simplecalculator

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ezylang.evalex.Expression
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class CalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState = _uiState.asStateFlow()

    fun onButtonPressed(char: String) {
        when (char) {
            in CalculatorButtonType.OPERATOR.listOfChars -> addOperatorToExpressionList(char)

            in CalculatorButtonType.CLEAR.listOfChars -> onClearPressed()
            in CalculatorButtonType.EQUALS.listOfChars -> onEqualsPressed()
            in CalculatorButtonType.BACKSPACE.listOfChars -> onBackspacePressed()
            else -> addInputToExpressionList(char)
        }
    }

    private fun onBackspacePressed() {
        val expression = uiState.value.expressions
        val lastInput = expression.lastOrNull() ?: return

        val newExpression = expression.toMutableList()
        newExpression.removeLast()

        if (lastInput.type == CalculatorButtonType.INPUT_TO_EXPRESSION) {
            val newText = lastInput.text.substring(0, lastInput.text.length - 1)
            if (newText.isNotEmpty()) {
                newExpression.add(
                    InputText(
                        newText, CalculatorButtonType.INPUT_TO_EXPRESSION
                    )
                )
            }
        }

        _uiState.update {
            it.copy(
                expressions = newExpression
            )
        }
    }

    private fun addOperatorToExpressionList(char: String) {
        when (char) {
            "√" -> addToExpressionList(
                "$char(", CalculatorButtonType.OPERATOR
            )

            "()" -> addParenthesesToExpressionList()
            else -> addToExpressionList(
                char, CalculatorButtonType.OPERATOR
            )
        }
    }

    private fun addParenthesesToExpressionList() {
        val numberOfOpenParentheses = uiState.value.expressions.count { it.text.contains("(") }
        val numberOfCloseParentheses = uiState.value.expressions.count { it.text.contains(")") }

        if (numberOfOpenParentheses > numberOfCloseParentheses) {
            addToExpressionList(
                ")", CalculatorButtonType.OPERATOR
            )
        } else {
            addToExpressionList(
                "(", CalculatorButtonType.OPERATOR
            )
        }
    }

    private fun addInputToExpressionList(char: String) {
        val expression = uiState.value.expressions
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
                expressions = newExpression
            )
        }
    }

    private fun addToExpressionList(char: String, calculatorButtonType: CalculatorButtonType) {
        val newExpression = uiState.value.expressions.toMutableList()
        newExpression.add(InputText(char, calculatorButtonType))

        _uiState.update {
            it.copy(
                expressions = newExpression
            )
        }
    }

    private fun onEqualsPressed() {
        val textExpression =
            uiState.value.expressions.joinToString("") { it.text }.replace("x", "*")
                .replace("√", "sqrt")

        val result = try {
            Expression(textExpression).evaluate().numberValue.toFloat()

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
                result = formatResult(result)
            )
        }
    }

    private fun formatResult(result: Float): String {
        val formatSpecifier = if (result % 1 == 0F) "%,.0f" else "%,.2f"
        return String.format(Locale.ENGLISH, formatSpecifier, result)
    }

    private fun onClearPressed() {
        _uiState.value = uiState.value.copy(
            expressions = emptyList(), result = "", error = ""
        )
    }
}

data class CalculatorUiState(
    val expressions: List<InputText> = emptyList(), val result: String = "", val error: String = ""
)

class InputText(
    val text: String, val type: CalculatorButtonType
)

enum class CalculatorButtonType(val listOfChars: List<String>) {
    INPUT_TO_EXPRESSION(emptyList()), OPERATOR(
        listOf(
            "+", "-", "x", "/", "%", "√", "()"
        )
    ),
    CLEAR(listOf("C")), EQUALS(listOf("=")), BACKSPACE(listOf("⌫"));
}
