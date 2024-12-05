package org.example

import java.io.File
import kotlin.math.abs

const val INPUT_PATH = "src/main/resources/inputs/day1"

data class Input(
    val left: List<Int>,
    val right: List<Int>,
)

fun loadInput(): Input  {
    val file = File(INPUT_PATH)
    val left = mutableListOf<Int>()
    val right = mutableListOf<Int>()
    file.useLines { lines ->
        lines.forEach { line ->
            val tokens = line.split("   ")
            left.add(tokens[0].toInt())
            right.add(tokens[1].toInt())
        }
    }
    return Input(left, right)
}

fun computeDistance(input: Input): Int {
    val sortedInput = Input(input.left.sorted(), input.right.sorted())
    val zippedInput: List<Pair<Int, Int>> = sortedInput.left.zip(sortedInput.right)
    return zippedInput.fold(0) { a: Int, pair: Pair<Int, Int> ->
        a + abs(pair.first - pair.second)
    }
}

fun computeSimilarityScore(input: Input): Int {
    val rightListNumberToFrequency: Map<Int, Int> = buildMap {
        input.right.forEach { num ->
            putIfAbsent(num, 0)
            computeIfPresent(num) { _, v -> v + 1 }
        }
    }
    return input.left.fold(0) { a: Int, num: Int ->
        a + (num * (rightListNumberToFrequency[num] ?: 0))
    }
}

fun main() {
    val input = loadInput()
    val distance = computeDistance(input)
    val similarityScore = computeSimilarityScore(input)
    println("Distance: $distance")
    println("Similarity Score: $similarityScore")
}