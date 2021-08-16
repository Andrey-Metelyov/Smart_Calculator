package calculator

data class Token(val value: String, val type: Type) {
    enum class Type {
        OPERATOR,
        LITERAL,
        BRACKET
    }

    override fun toString(): String {
        return value
    }
}
