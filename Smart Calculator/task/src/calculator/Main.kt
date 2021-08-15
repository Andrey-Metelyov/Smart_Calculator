package calculator

fun main() {
    while (true) {
        val input = readLine()!!
        System.err.println("'$input'")
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
        val values = input.split(" ")
        System.err.println("'$values'")
        var sum = 0
        var isOp = false
        var op = Operation.PLUS
        for (value in values) {
            if (!isOp) {
                val operand = value.toIntOrNull()
                if (operand == null) {
                    println("Invalid expression")
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
        println(sum)
    }
    println("Bye!")
}

enum class Operation {
    PLUS,
    MINUS,
}
