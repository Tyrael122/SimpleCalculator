package com.makesoftware.simplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorApp()
                }
            }
        }
    }
}

val LocalCalculatorViewModel = compositionLocalOf { CalculatorViewModel() }

@Composable
fun CalculatorApp(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        val viewModel: CalculatorViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsState()

        CompositionLocalProvider(
            LocalCalculatorViewModel provides viewModel
        ) {
            TextView(expression = uiState.expression,
                result = uiState.error.ifEmpty { uiState.result })

            CalculatorButtons()
        }
    }
}

@Composable
private fun TextView(expression: List<InputText>, result: String) {
    val expressionText = buildAnnotatedString {
        expression.forEach {
            val style = when (it.type) {
                CalculatorButtonType.OPERATOR -> SpanStyle(
                    color = Color.Red, fontSize = 30.sp
                )

                else -> {
                    SpanStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 30.sp
                    )
                }
            }

            withStyle(style) {
                append(it.text, " ")
            }
        }
    }

    Text(
        text = expressionText, modifier = Modifier.fillMaxWidth()
    )

    Text(
        text = result, style = TextStyle(
            fontSize = 30.sp, fontWeight = FontWeight.Normal
        ), modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CalculatorButtons() {
    val viewModel = LocalCalculatorViewModel.current

    val buttons = listOf(
        listOf("C", "%", "", "/"),
        listOf("7", "8", "9", "x"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("", "0", ".", "=")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(color = Color(0xFF292D36))
            .clip(RoundedCornerShape(10.dp))
    ) {
        buttons.forEach { row ->
            DefaultRow {
                row.forEach { char ->
                    DefaultButton(char = char, onClick = {
                        viewModel.onButtonPressed(char)
                    })
                }
            }
        }
    }
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
fun DefaultButton(modifier: Modifier = Modifier, char: String, onClick: () -> Unit) {
    Button(
        onClick = { onClick() }, shape = CircleShape, modifier = modifier.size(75.dp)
    ) {
        Text(
            text = char, style = TextStyle(
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

class CalculatorButton(val char: String? = null, val icon: ImageVector? = null)
