package calculator

fun main() {
    while (true) {
        val input = readLine()!!
        System.err.println("'$input'")
        if (input == "/exit") {
            break
        }
        if (input == "/help") {
            println("The program calculates the sum of numbers")
            continue
        }
        if (input.isEmpty()) {
            continue
        }
        val values = input.split(" ")
        System.err.println("'$values'")
        var sum = 0
        for (value in values) {
            sum += value.toIntOrNull() ?: 0
        }
        println(sum)
    }
    println("Bye!")
}
