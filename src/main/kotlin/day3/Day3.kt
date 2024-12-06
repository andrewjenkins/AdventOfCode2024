package day3

import java.io.File
import kotlin.math.abs

private const val INPUT_PATH = "src/main/resources/inputs/day3"

private val VALID_EXPRESSION_REGEX = Regex("mul\\(([0-9]+),([0-9]+)\\)")

private fun loadInput(): List<String> {
    val file = File(INPUT_PATH)
    return buildList {
        file.useLines { lines ->
            lines.forEach { line ->
                add(line.trim())
            }
        }
    }
}

private fun String.enabledOnly(): String {
    val tokens = this.split("don't()")
    if (tokens.size == 1) {
        return tokens[0]
    }
    return buildString {
        append(tokens[0])
        tokens.subList(1, tokens.size).forEach { token ->
            val doTokens = token.split("do()", limit = 2)
            if (doTokens.size == 2) {
                append(doTokens[1])
            }
        }
    }
}

private fun String.multiplyAndAdd(): Int {
    val matches = VALID_EXPRESSION_REGEX.findAll(this)
    return matches.sumOf { match ->
        val (left, right) = match.destructured
        return@sumOf left.toInt() * right.toInt()
    }
}

fun main() {
    val computations = loadInput()
    val result = computations.sumOf { it.multiplyAndAdd() }
    println("Part 1 result: $result")
    val result2 = computations.joinToString("").enabledOnly().multiplyAndAdd()
    println("Part 2 result: $result2")
}