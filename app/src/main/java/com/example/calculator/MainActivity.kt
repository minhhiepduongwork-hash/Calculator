// Main screen of the calculator app.
package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.Black
import com.example.calculator.ui.theme.CalculatorTheme
import com.example.calculator.ui.theme.Grey10
import com.example.calculator.ui.theme.Grey40

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Calculator()
            }
        }
    }
}

@Composable
fun Calculator(
    modifier: Modifier = Modifier
) {
    var expression by remember {
        mutableStateOf("")
    }
    var result by remember {
        mutableStateOf("0")
    }
    var startNewInput by remember {
        mutableStateOf(true)
    }
    var firstOperand by remember {
        mutableIntStateOf(0)
    }
    var operator by remember {
        mutableStateOf("")
    }
    var clearOnNextDigit by remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            FirstCalculatorScreen(
                value = expression,
                onValueChange = { expression = it },
                modifier = Modifier.weight(0.4f)
            )
            SecondCalculatorScreen(
                value = result,
                onValueChange = { value ->
                    if (clearOnNextDigit) {
                        expression = ""
                        firstOperand = 0
                        operator = ""
                        clearOnNextDigit = false
                    }
                    result = value
                    startNewInput = false
                },
                modifier = Modifier.weight(0.6f)
            )
            CalculatorButtonGroup(
                modifier = Modifier.weight(3f),
                onKeyClick = keyClick@{ key ->
                    when(key) {
                        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> {
                            if (clearOnNextDigit) {
                                expression = ""
                                firstOperand = 0
                                operator = ""
                                startNewInput = true
                                clearOnNextDigit = false
                            }
                            if (result == "0" || startNewInput) {
                                result = key
                                startNewInput = false
                            } else {
                                result += key
                            }
                        }
                        "+", "-", "×", "÷" -> {
                            val input = result.toIntOrNull()
                            if (input != null) {
                                if (operator.isEmpty()) {
                                    firstOperand = input
                                } else if (startNewInput) {
                                    operator = key
                                    startNewInput = true
                                } else {
                                    val answer = calculate(firstOperand, input, operator)
                                    if (answer == null) {
                                        expression = "$firstOperand $operator $input ="
                                        startNewInput = false
                                        clearOnNextDigit = false
                                        return@keyClick
                                    }
                                    firstOperand = answer
                                    result = answer.toString()
                                }
                                operator = key
                                expression = "$firstOperand $operator"
                                startNewInput = true
                                clearOnNextDigit = false
                            }
                        }
                        "±" -> {
                            if (result != "0" && result.isNotEmpty()) {
                                result = if (result.startsWith("-")) {
                                    result.removePrefix("-")
                                } else {
                                    "-$result"
                                }
                            }
                        }
                        "C" -> {
                            result = "0"
                            expression = ""
                            firstOperand = 0
                            operator = ""
                            startNewInput = true
                            clearOnNextDigit = false
                        }
                        "CE" -> {
                            result = "0"
                            startNewInput = false
                        }
                        "⌫" -> {
                            if (clearOnNextDigit) {
                                expression = ""
                                firstOperand = 0
                                operator = ""
                                clearOnNextDigit = false
                            }
                            result = result.dropLast(1).ifEmpty { "0" }
                            if (result == "-") {
                                result = "0"
                            }
                            startNewInput = false
                        }
                        "=" -> {
                            val secondOperand = result.toIntOrNull()
                            if (secondOperand != null) {
                                val answer = calculate(firstOperand, secondOperand, operator)
                                if (answer != null) {
                                    expression = "$firstOperand $operator $secondOperand $key"
                                    result = answer.toString()
                                    startNewInput = true
                                    operator = ""
                                    clearOnNextDigit = true
                                } else if (operator == "÷") {
                                    expression = "$firstOperand $operator $secondOperand $key"
                                    startNewInput = false
                                    clearOnNextDigit = false
                                }
                            }
                        }
                        else -> { }
                    }
                }
            )
        }
    }
}

@Composable
fun FirstCalculatorScreen(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = true,
        modifier = modifier
            .fillMaxWidth(),
        textStyle = TextStyle(
            fontSize = 40.sp,
            textAlign = TextAlign.Right
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun SecondCalculatorScreen(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = TextStyle(
            fontSize = 80.sp,
            textAlign = TextAlign.Right
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
    )
}

@Composable
fun RowScope.CalculatorButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Black
        ),
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .padding(2.dp)
    ) {
        Text(
            text = text,
            fontSize = 32.sp
        )
    }
}

@Composable
fun CalculatorButtonGroup(
    modifier: Modifier = Modifier,
    onKeyClick: (String) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            CalculatorButton(
                text = stringResource(R.string.clear_entry),
                color = Grey40,
                onClick = { onKeyClick("CE") }
            )
            CalculatorButton(
                text = stringResource(R.string.clear),
                color = Grey40,
                onClick = { onKeyClick("C") }
            )
            CalculatorButton(
                text = stringResource(R.string.backspace),
                color = Grey40,
                onClick = { onKeyClick("⌫") }
            )
            CalculatorButton(
                text = stringResource(R.string.divide),
                color = Grey40,
                onClick = { onKeyClick("÷") }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            CalculatorButton(
                text = stringResource(R.string.seven),
                color = Grey10,
                onClick = { onKeyClick("7") }
            )
            CalculatorButton(
                text = stringResource(R.string.eight),
                color = Grey10,
                onClick = { onKeyClick("8") }
            )
            CalculatorButton(
                text = stringResource(R.string.nine),
                color = Grey10,
                onClick = { onKeyClick("9") }
            )
            CalculatorButton(
                text = stringResource(R.string.multiply),
                color = Grey40,
                onClick = { onKeyClick("×") }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            CalculatorButton(
                text = stringResource(R.string.four),
                color = Grey10,
                onClick = { onKeyClick("4") }
            )
            CalculatorButton(
                text = stringResource(R.string.five),
                color = Grey10,
                onClick = { onKeyClick("5") }
            )
            CalculatorButton(
                text = stringResource(R.string.six),
                color = Grey10,
                onClick = { onKeyClick("6") }
            )
            CalculatorButton(
                text = stringResource(R.string.minus),
                color = Grey40,
                onClick = { onKeyClick("-") }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            CalculatorButton(
                text = stringResource(R.string.one),
                color = Grey10,
                onClick = { onKeyClick("1") }
            )
            CalculatorButton(
                text = stringResource(R.string.two),
                color = Grey10,
                onClick = { onKeyClick("2") }
            )
            CalculatorButton(
                text = stringResource(R.string.three),
                color = Grey10,
                onClick = { onKeyClick("3") }
            )
            CalculatorButton(
                text = stringResource(R.string.plus),
                color = Grey40,
                onClick = { onKeyClick("+") }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            CalculatorButton(
                text = stringResource(R.string.plus_minus),
                color = Grey40,
                onClick = { onKeyClick("±") }
            )
            CalculatorButton(
                text = stringResource(R.string.zero),
                color = Grey10,
                onClick = { onKeyClick("0") }
            )
            CalculatorButton(
                text = stringResource(R.string.decimal_point),
                color = Grey40,
                onClick = { onKeyClick(".") }
            )
            CalculatorButton(
                text = stringResource(R.string.equal),
                color = Grey40,
                onClick = { onKeyClick("=") }
            )
        }
    }
}

fun calculate (left: Int, right: Int, operator: String): Int? {
    return when(operator) {
        "+" -> left + right
        "-" -> left - right
        "×" -> left * right
        "÷" -> {
            if (right != 0) left / right
            else null
        }
        else -> null
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalculatorTheme {
        Calculator()
    }
}
