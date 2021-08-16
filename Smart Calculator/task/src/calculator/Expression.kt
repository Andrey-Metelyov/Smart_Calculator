package calculator

import java.math.BigInteger
import java.util.*
import kotlin.math.pow

class Expression {
    enum class State {
        READ_LITERAL,
        READ_OPERATOR,
    }

    private var postfix = mutableListOf<Token>()

    fun parse(string: String): Boolean {
        val stack = Stack<Token>()
        var state = State.READ_LITERAL
        val tokens = mutableListOf<Token>()
        var acc = ""
        for (ch in string.replace(" ", "")) {
//            System.err.println("process '$ch' $state ($string), $acc")
            when {
                ch == '(' -> {
                    when (state) {
                        State.READ_LITERAL -> {
                            // error. a + b (
                            // but not error if a + b ((
//                            throw IllegalArgumentException("Bracket ( after literal in '$string'")
                        }
                        State.READ_OPERATOR -> {
                            // ok. a + b + (
                            tokens.add(Token(acc, Token.Type.OPERATOR))
                            acc = ""
                            state = State.READ_LITERAL
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
                            state = State.READ_OPERATOR
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
        if (state == State.READ_LITERAL && acc.isNotEmpty()) {
            tokens.add(Token(acc, Token.Type.LITERAL))
        }
        System.err.println("tokens: ${tokens.joinToString()}")
        for (token in tokens) {
            if (token.type == Token.Type.LITERAL) {
                postfix.add(token)
            } else if (token.type == Token.Type.BRACKET && token.value == "(") {
                stack.push(token)
            } else if (token.type == Token.Type.BRACKET && token.value == ")") {
                while (!stack.isEmpty() && stack.peek().value != "(") {
                    postfix.add(stack.pop())
                }
                if (stack.isEmpty()) {
//                    throw java.lang.IllegalArgumentException("No opening bracket in $string")
                    System.err.println("No opening bracket in $string")
                    return false
                }
                stack.pop()
            } else {
                while (!stack.isEmpty() && prec(token) <= prec(stack.peek())){
                    postfix.add(stack.pop())
                }
                stack.push(token)
            }
        }
        while (!stack.isEmpty()) {
            if (stack.peek().value == "(") {
                return false
//                throw IllegalArgumentException("Invalid expression")
            }
            postfix.add(stack.pop())
        }
        System.err.println("$string -> ${postfix.joinToString()}")
        return true
/*        return false
        for (ch in string.replace(" ", "")) {
            System.err.println("process '$ch' ($string)")
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

 */
    }

    private fun prec(operation: Token) = when (operation.value.first()) {
        '+', '-' -> 1
        '*', '/' -> 2
        '^' -> 3
        else -> -1
    }

    fun eval(variables: MutableMap<String, BigInteger>): BigInteger {
        System.err.println("'${this.postfix}'.eval($variables)")
        val stack = Stack<BigInteger>()
        for (token in postfix) {
            if (token.type != Token.Type.OPERATOR) {
                stack.push(
                    token.value.toBigIntegerOrNull() ?: variables[token.value]
                    ?: throw IllegalArgumentException("Unknown token '${token.value}'")
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

    private fun operation(a: BigInteger, b: BigInteger, op: String): BigInteger {
        System.err.println("perform $a $op $b")
        val operator = if ("[+\\-]+".toRegex().matches(op)) {
            // just + and -
            if (op.count { it == '-' } % 2 == 0) {
                "+"
            } else {
                "-"
            }
        } else {
            op
        }
        return when (operator) {
            "+" -> a + b
            "-" -> a - b
            "/" -> a / b
            "*" -> a * b
            "^" -> a.pow(b.toInt())
            else -> throw UnsupportedOperationException("Invalid expression. Unknown operation '$op'")
        }
    }
}
