package com.example.calculator

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView

    private var currentInput = ""
    private var operator = ""
    private var firstOperand = 0.0
    private var isResultShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.tvResult)

        val numberButtons = mapOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8",
            R.id.btn9 to "9", R.id.btn00 to "00"
        )
        numberButtons.forEach { (id, value) ->
            findViewById<Button>(id).setOnClickListener { onNumber(value) }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener { onDot() }
        findViewById<Button>(R.id.btnPlus).setOnClickListener { onOperator("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { onOperator("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperator("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperator("÷") }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { onPercent() }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEquals() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { onClear() }
        findViewById<Button>(R.id.btnBack).setOnClickListener { onBack() }
    }

    // ---------- MENU ----------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear -> { onClear(); true }
            R.id.action_back -> { onBack(); true }
            R.id.action_about -> {
                Toast.makeText(this, "Calculator v1.0 — built with Kotlin", Toast.LENGTH_LONG).show()
                true
            }
            R.id.action_exit -> { finish(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ---------- CALCULATOR LOGIC ----------
    private fun onNumber(num: String) {
        if (isResultShown) {
            currentInput = ""
            isResultShown = false
            tvExpression.text = ""
        }
        if (currentInput == "0") currentInput = ""
        currentInput += num
        tvResult.text = currentInput
    }

    private fun onDot() {
        if (isResultShown) {
            currentInput = ""
            isResultShown = false
            tvExpression.text = ""
        }
        if (currentInput.isEmpty()) currentInput = "0"
        if (!currentInput.contains(".")) {
            currentInput += "."
            tvResult.text = currentInput
        }
    }

    private fun onOperator(op: String) {
        if (currentInput.isEmpty() && operator.isEmpty()) return
        if (currentInput.isNotEmpty()) {
            if (operator.isNotEmpty()) {
                val second = currentInput.toDoubleOrNull() ?: 0.0
                val result = calculate(firstOperand, second, operator)
                firstOperand = result
                tvResult.text = format(result)
            } else {
                firstOperand = currentInput.toDoubleOrNull() ?: 0.0
            }
        }
        operator = op
        currentInput = ""
        isResultShown = false
        tvExpression.text = "${format(firstOperand)} $op"
    }

    private fun onPercent() {
        if (currentInput.isEmpty()) return
        val value = currentInput.toDoubleOrNull() ?: 0.0
        val result = value / 100.0
        currentInput = result.toString()
        tvResult.text = format(result)
    }

    private fun onEquals() {
        if (operator.isEmpty() || currentInput.isEmpty()) return
        val second = currentInput.toDoubleOrNull() ?: 0.0
        val result = calculate(firstOperand, second, operator)
        tvExpression.text = "${format(firstOperand)} $operator ${format(second)} ="
        tvResult.text = format(result)
        currentInput = result.toString()
        operator = ""
        isResultShown = true
    }

    private fun onClear() {
        currentInput = ""
        operator = ""
        firstOperand = 0.0
        isResultShown = false
        tvExpression.text = ""
        tvResult.text = "0"
    }

    private fun onBack() {
        if (isResultShown) { onClear(); return }
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            tvResult.text = if (currentInput.isEmpty()) "0" else currentInput
        }
    }

    private fun calculate(a: Double, b: Double, op: String): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> if (b == 0.0) Double.NaN else a / b
            else -> b
        }
    }

    private fun format(value: Double): String {
        if (value.isNaN()) return "Error"
        val df = DecimalFormat("#.##########")
        return df.format(value)
    }
}