package calculator

fun main() {
    val variables = mutableMapOf<String, Double>()

    while (true) {
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
        if ("\\s*\\S+\\s*=.*".toRegex().matches(input)) {
            // assignment
            val (identifier, value) = input.split("\\s*=\\s*".toRegex())
            System.err.println("identifier = '$identifier', value = '$value'")
            if (!"[A-Za-z]+".toRegex().matches(identifier)) {
                println("Invalid identifier")
                continue
            }
            val existantValue = variables[value]
            System.err.println("variables[$value] = $existantValue")
            if (existantValue != null) {
                System.err.println("existantValue: $value = $existantValue")
                variables[identifier] = existantValue.toDouble()
            } else if (!"(\\d+(\\.\\d+)?)".toRegex().matches(value)) {
                println("Invalid assignment")
                continue
            } else {
                variables[identifier] = value.toDouble()
                System.err.println("set variable '$identifier' = ${value.toDouble()}")
            }
            System.err.println("variables: $variables")
            continue
        }
        val values = input.split(" ")
        System.err.println("values: '$values'")
        var sum = 0.0
        var isOp = false
        var op = Operation.PLUS
        for (value in values) {
            if (!isOp) {
                val operand = variables[value] ?: value.toDoubleOrNull()
                if (operand == null) {
                    println("Unknown variable")
                    continue
                }
                when (op) {
                    Operation.PLUS -> sum += operand
                    Operation.MINUS -> sum -= operand
                }
                System.err.println("sum = $sum")
            } else {
                if (!"(\\+|\\-)+".toRegex().matches(value)) {
                    println("Invalid expression")
                    continue
                }
                op = Operation.PLUS
                for (ch in value) {
                    op = when (ch) {
                        '+' -> op
                        '-' -> if (op == Operation.PLUS) Operation.MINUS else Operation.PLUS
                        else -> return
                    }
                }
                System.err.println("op = $op")
            }
            isOp = !isOp
        }
        println(sum.toInt())
    }
    println("Bye!")
}

enum class Operation {
    PLUS,
    MINUS,
}
