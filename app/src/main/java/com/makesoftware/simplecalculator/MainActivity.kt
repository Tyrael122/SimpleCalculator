package com.makesoftware.simplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makesoftware.simplecalculator.ui.theme.SimpleCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleCalculatorTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorApp()
                }
            }
        }
    }
}

val operatorWithSpaces = listOf("+", "-", "/", "x")

@Composable
fun CalculatorApp(
    modifier: Modifier = Modifier, viewModel: CalculatorViewModel = viewModel()
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier.fillMaxSize()
    ) {
        val uiState by viewModel.uiState.collectAsState()

        TextView(
            expressions = uiState.expressions,
            result = uiState.error.ifEmpty { uiState.result })

        Spacer(Modifier.height(20.dp))

        CalculatorButtons(viewModel::onButtonPressed)
    }
}

@Composable
private fun TextView(expressions: List<InputText>, result: String) {
    val expressionText = buildAnnotatedString {
        expressions.forEach {
            // TODO: Refactor the styles to a bunch of global variables.
            val style = when (it.type) {
                CalculatorButtonType.OPERATOR -> SpanStyle(
                    color = redColor, fontSize = 20.sp
                )

                else -> {
                    SpanStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 20.sp
                    )
                }
            }

            withStyle(style) {
                if (operatorWithSpaces.contains(it.text)) {
                    append(" ${it.text} ")
                } else {
                    append(it.text)
                }
            }
        }
    }

    Column(modifier = Modifier.padding(end = 30.dp)) {
        Text(
            text = expressionText, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = result, style = TextStyle(
                fontSize = 40.sp, fontWeight = FontWeight.Bold
            ), textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CalculatorButtons(onButtonPressed: (String) -> Unit) {
    val buttons = listOf(
        listOf("C", "√", "()", "/"),
        listOf("7", "8", "9", "x"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("⌫", "0", ".", "=")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .background(color = buttonBackgroundColor)
            .padding(30.dp)
    ) {
        buttons.forEachIndexed { rowIndex, row ->
            DefaultRow {
                row.forEachIndexed { columnIndex, char ->
                    val buttonTextColor = generateButtonTextColor(columnIndex, rowIndex)

                    DefaultButton(
                        char = char, textColor = buttonTextColor, onClick = onButtonPressed
                    )
                }
            }
        }
    }
}

@Composable
fun generateButtonTextColor(columnIndex: Int, rowIndex: Int): Color {
    if (columnIndex == 3) {
        return redColor
    }

    if (rowIndex == 0) {
        return greenButtonColor
    }

    return MaterialTheme.colorScheme.onSurface
}

@Composable
fun DefaultRow(content: @Composable () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        content()
    }
}

@Composable
fun DefaultButton(
    modifier: Modifier = Modifier, char: String, textColor: Color, onClick: (String) -> Unit
) {
    Button(
        onClick = { onClick(char) },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier.size(65.dp)
    ) {
        // TODO: Refactor the UI and the viewModel to allow for greater flexibility (icons and strings).
        //  Maybe use a class instead of a string only.
        if (char == "⌫") {
            Icon(
                Icons.Filled.KeyboardReturn,
                contentDescription = null,
                tint = textColor,
            )

        } else {
            Text(
                text = char, style = TextStyle(
                    fontSize = 25.sp, color = textColor, fontWeight = FontWeight.Normal
                )
            )
        }

    }
}

val redColor = Color(0xFFEC7B7B)
val buttonBackgroundColor = Color(0xFF292D36)
val buttonColor = Color(0xFF272B33)
val greenButtonColor = Color(0xFF26E2D7)

class CalculatorButton(val char: String? = null, val icon: ImageVector? = null)
