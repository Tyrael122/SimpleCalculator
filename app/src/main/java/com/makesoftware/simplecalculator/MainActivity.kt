package com.makesoftware.simplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makesoftware.simplecalculator.ui.theme.SimpleCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: CalculatorViewModel = viewModel()

                    CompositionLocalProvider(
                        LocalCalculatorViewModel provides viewModel
                    ) {
                        CalculatorApp()
                    }
                }
            }
        }
    }
}

val LocalCalculatorViewModel = compositionLocalOf { CalculatorViewModel() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorApp(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        val viewModel = LocalCalculatorViewModel.current
        val uiState by viewModel.uiState.collectAsState()

        TextField(
            value = uiState.expression,
            onValueChange = {},
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            textStyle = TextStyle(
                fontSize = 30.sp, fontWeight = FontWeight.Normal
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        CalculatorButtons()
    }
}

@Composable
fun CalculatorButtons() {
    val viewModel = LocalCalculatorViewModel.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DefaultRow {
            DefaultButton(char = "C", onClick = {
                viewModel.onClearPressed()
            })

            DefaultButton(char = "%")
            DefaultButton(char = "")
            DefaultButton(char = "/")
        }

        DefaultRow {
            DefaultButton(char = "7")
            DefaultButton(char = "8")
            DefaultButton(char = "9")
            DefaultButton(char = "x")
        }

        DefaultRow {
            DefaultButton(char = "4")
            DefaultButton(char = "5")
            DefaultButton(char = "6")
            DefaultButton(char = "-")
        }

        DefaultRow {
            DefaultButton(char = "1")
            DefaultButton(char = "2")
            DefaultButton(char = "3")
            DefaultButton(char = "+")
        }

        DefaultRow {
            DefaultButton(char = "")
            DefaultButton(char = "0")
            DefaultButton(char = ".")
            DefaultButton(char = "=", onClick = {
                viewModel.onEqualsPressed()
            })
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
fun DefaultButton(modifier: Modifier = Modifier, char: String, onClick: (() -> Unit)? = null) {
    val viewModel = LocalCalculatorViewModel.current
    val onClickAction = if (onClick == null) {
        {
            viewModel.onButtonPressed(char)
        }
    } else {
        onClick
    }

    Button(
        onClick = {
            onClickAction()
        }, shape = CircleShape, modifier = modifier.size(buttonDefaultSize)
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

val buttonDefaultSize: Dp = 75.dp