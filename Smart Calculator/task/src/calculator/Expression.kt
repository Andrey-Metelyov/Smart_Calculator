package calculator

import java.util.*
import kotlin.math.pow

class Expression {
    enum class State {
        READ_LITERAL,
        READ_OPERATOR,
    }

    private var postfix = mutableListOf<Token>()

    fun parse(string: String): Boolean {
        val stack = Stack<Char>()
        var literal = ""
        var operator = ""
        var state = State.READ_LITERAL
        val tokens = mutableListOf<Token>()
        var acc = ""
        for (ch in string.replace(" ", "")) {
            System.err.println("process '$ch' $state ($string), $acc")
            when {
                ch == '(' -> {
                    when (state) {
                        State.READ_LITERAL -> {
                            // error. a + b (
                            throw IllegalArgumentException("Bracket ( after literal in '$string'")
                        }
                        State.READ_OPERATOR -> {
                            // ok. a + b + (
                            tokens.add(Token(acc, Token.Type.OPERATOR))
                        }
                    }
                    tokens.add(Token(ch.toString(), Token.Type.BRACKET))
                }
                ch == ')' -> {
                    when (state) {
                        State.READ_LITERAL -> {
                            // ok. a + b)
                            tokens.add(Token(acc, Token.Type.LITERAL))
                            acc = ""
                        }
                        State.READ_OPERATOR -> {
                            // error. a + b + )
                            throw IllegalArgumentException("Bracket ) after operator in '$string'")
                        }
                    }
                    tokens.add(Token(ch.toString(), Token.Type.BRACKET))
                }
                "-+*/^".contains(ch) -> {
                    when (state) {
                        State.READ_LITERAL -> {
                            tokens.add(Token(acc, Token.Type.LITERAL))
                            acc = ch.toString()
                            state = State.READ_OPERATOR
                        }
                        State.READ_OPERATOR -> {
                            acc += ch
                        }
                    }
                }
                ch.isLetterOrDigit() -> {
                    when (state) {
                        State.READ_OPERATOR -> {
                            tokens.add(Token(acc, Token.Type.OPERATOR))
                            acc = ch.toString()
                            state = State.READ_LITERAL
                        }
                        State.READ_LITERAL -> {
                            acc += ch
                        }
                    }
                }
                else -> {
                    System.err.println("!!!error!!!")
                }
            }
        }
        System.err.println("tokens: ${tokens.joinToString()}")
//        return false
        for (token in tokens) {
            System.err.println("process '$token' ($string)")
                when (state) {
                    State.READ_LITERAL -> {
                        if (!ch.isLetterOrDigit()) {
                            postfix.add(Token(literal, Token.Type.LITERAL))
                            literal = ""
                            if (ch == '(') {
                                stack.push(ch)
                            } else if (ch == ')') {
                                while (!stack.isEmpty() && stack.peek() != '(') {
                                    postfix.add(Token(stack.pop().toString(), Token.Type.OPERATOR))
                                }
                                stack.pop()
                            } else {
                                state = State.READ_OPERATOR
                                operator += ch
                            }
                        } else {
                            literal += ch
                        }
                    }
                    State.READ_OPERATOR -> {
                        if (ch.isLetterOrDigit() || ch == '(' || ch == ')') {
                            if (operator.length > 1) {
                                if ("[+\\-]+".toRegex().matches(operator)) {
                                    // just + and -
                                    if (operator.count { it == '-' } % 2 == 0) {
                                        operator = "+"
                                    } else {
                                        operator = "-"
                                    }
                                } else {
//                                throw IllegalArgumentException("Invalid operator '$operator'")
                                    return false
                                }
                            }
                            if (ch == '(') {
                                stack.push(ch)
                                state = State.READ_LITERAL
                            } else if (ch == ')') {
                                while (!stack.isEmpty() && stack.peek() != '(') {
                                    postfix.add(Token(stack.pop().toString(), Token.Type.OPERATOR))
                                }
                                stack.pop()
                            } else {
                                while (!stack.isEmpty() && prec(ch) <= prec(stack.peek())) {
                                    postfix.add(Token(stack.pop().toString(), Token.Type.OPERATOR))
                                }
                                stack.push(operator.first())
                                operator = ""
                                state = State.READ_LITERAL
                                literal += ch
                            }
                        } else {
                            operator += ch
                        }
                    }
                }

            System.err.println("literal: $literal")
            System.err.println("operator: $operator")
            System.err.println("result: $postfix")
            System.err.println("stack: ${stack.joinToString()}")
            System.err.println("state: $state")
            System.err.println()
        }
        if (state == State.READ_OPERATOR) {
            return false
//            throw IllegalArgumentException("Invalid expression")
        }
        if (literal.isNotEmpty()) {
            postfix.add(Token(literal, Token.Type.LITERAL))
        }
        while (!stack.isEmpty()) {
            if (stack.peek() == '(') {
                return false
//                throw IllegalArgumentException("Invalid expression")
            }
            postfix.add(Token(stack.pop().toString(), Token.Type.OPERATOR))
        }
        System.err.println("$string -> ${postfix.joinToString()}")
        return true
    }

    private fun prec(operation: Char) = when (operation) {
        '+', '-' -> 1
        '*', '/' -> 2
        '^' -> 3
        else -> -1
    }

    fun eval(variables: MutableMap<String, Double>): Double {
        System.err.println("'${this.postfix}'.eval($variables)")
        val stack = Stack<Double>()
        for (token in postfix) {
            if (token.type != Token.Type.OPERATOR) {
                stack.push(
                    token.value.toDoubleOrNull() ?: variables[token.value]
                    ?: throw IllegalArgumentException("Unknow token '${token.value}'")
                )
            } else {
                val a = stack.pop()
                val b = stack.pop()
                stack.push(operation(b, a, token.value))
            }
            System.err.println(stack.joinToString())
        }
        System.err.println(stack.joinToString())
        return stack.peek()
    }

    private fun operation(a: Double, b: Double, op: String): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "/" -> a / b
            "*" -> a * b
            "^" -> a.pow(b)
            else -> throw UnsupportedOperationException("Unknown operation '$op'")
        }
    }
}
