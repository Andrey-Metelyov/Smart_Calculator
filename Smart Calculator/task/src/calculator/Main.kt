package calculator

import java.math.BigInteger

val variables = mutableMapOf<String, BigInteger>()

fun main() {
    while (true) {
        repeat(80) { System.err.print('-') }
        System.err.println()
        val input = readLine()!!.trim()
        System.err.println("input: '$input'")
        if (input.isEmpty()) {
            continue
        }
        if (input == "/exit") {
            break
        }
        if (input == "/help") {
            println("The program calculates the sum of numbers")
            continue
        }
        if ("/\\w+".toRegex().matches(input)) {
            println("Unknown command")
            continue
        }

//        if ("\\s*\\S+\\s*=.*".toRegex().matches(input)) {
        if (input.contains("=")) {
            handleAssignment(input, variables)
            continue
        }
//        val values = input.split(" ")
//        System.err.println("values: '$values'")
        val expression = Expression()
        if (expression.parse(input)) {
            try {
                println(expression.eval(variables))
            } catch (e: Exception) {
                println(e.message)
            }
        } else {
            println("Invalid expression")
        }
    }
    println("Bye!")
}

private fun handleAssignment(input: String, variables: MutableMap<String, BigInteger>): Boolean {
    // assignment
    val (identifier, value) = input.split("\\s*=\\s*".toRegex())
    System.err.println("identifier = '$identifier', value = '$value'")
    if (!"[A-Za-z]+".toRegex().matches(identifier)) {
        println("Invalid identifier")
        return false
    }
    val existantValue = variables[value]
    System.err.println("variables[$value] = $existantValue")
    if (existantValue != null) {
        System.err.println("existantValue: $value = $existantValue")
        variables[identifier] = existantValue
    } else if (!"(-?\\d+(\\.\\d+)?)".toRegex().matches(value)) {
        println("Invalid assignment")
        return false
    } else {
        variables[identifier] = value.toBigInteger()
        System.err.println("set variable '$identifier' = ${value.toDouble()}")
    }
    System.err.println("variables: $variables")
    return true
}
