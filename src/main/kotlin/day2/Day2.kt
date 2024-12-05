package org.example.day2

import java.io.File
import kotlin.math.abs

private const val INPUT_PATH = "src/main/resources/inputs/day2"

private fun loadInput(): List<List<Int>> {
    val file = File(INPUT_PATH)
    return buildList {
        file.useLines { lines ->
            lines.forEach { line ->
                val tokens = line.split(" ")
                val numTokens = tokens.map { it.toInt() }
                add(numTokens)
            }
        }
    }
}

private fun List<Int>.isReportSafe(): Boolean {
    if (!isIncrementsBetween(1, 3)) {
        return false
    }
    return (isIncreasing() || isDecreasing())
}

private fun List<Int>.isReportSafeWithDampener(): Boolean {
    if (isReportSafe()) {
        return true
    }
    return indices.any { index ->
        (subList(0, index) + subList(index + 1, size)).isReportSafe()
    }
}

private fun List<Int>.isIncreasing(): Boolean {
    return zipWithNext().all { (l, r) -> l > r }
}

private fun List<Int>.isDecreasing(): Boolean {
    return zipWithNext().all { (l, r) -> l < r }
}

private fun List<Int>.isIncrementsBetween(min: Int, max: Int): Boolean {
    return zipWithNext().all { (l, r) -> abs(l - r) in min..max }
}

fun main() {
    val reports = loadInput()
    val safeReportCount = reports.count { it.isReportSafe() }
    val safeWithDampenerCount = reports.count { it.isReportSafeWithDampener() }
    println(safeReportCount)
    println(safeWithDampenerCount)
}