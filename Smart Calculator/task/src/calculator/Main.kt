package calculator

fun main() {
    while (true) {
        val input = readLine()!!
        System.err.println("'$input'")
        if (input == "/exit") {
            break
        }
        if (input.isEmpty()) {
            continue
        }
        val values = input.split(" ")
        System.err.println("'$values'")
        val a = values[0].toIntOrNull() ?: 0
        var b = 0
        if (values.size > 1) {
            b = values[1].toIntOrNull() ?: 0
        }
        println(a + b)
    }
    println("Bye!")
}
