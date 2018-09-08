package nl.hiddewieringa

import java.util.regex.Pattern

data class LogicSolveError(override val message: String) : Exception(message)

data class Coordinate(val a: Int, val b: Int)

fun row(i: Int): List<Coordinate> {
    return (1..9).map {
        Coordinate(i, it)
    }
}

fun column(i: Int): List<Coordinate> {
    return (1..9).map {
        Coordinate(it, i)
    }
}

fun block(i: Int): List<Coordinate> {
    return (1..9).map {
        Coordinate(1 + 3 * ((i - 1) / 3) + (it - 1) / 3, 1 + 3 * ((i - 1) % 3) + (it - 1) % 3)
    }
}

class Sudoku(values: Map<Coordinate, Int>) : SudokuInput(values, (1..9).flatMap {
    listOf(row(it), column(it), block(it))
}) {
    companion object {
        fun readFromString(s: String): Sudoku {
            val split = s.split(Pattern.compile("\\s+")).filter { it.isNotEmpty() }
            if (split.size != 81) {
                throw Exception("Input size should be 81 non-whitespace strings")
            }

            val valueMap = (1..9).flatMap { i ->
                (1..9).flatMap { j ->
                    val c = split[9 * (i - 1) + (j - 1)]
                    if (c.length == 1 && c.toIntOrNull() != null && c.toInt() >= 1 && c.toInt() <= 9) {
                        listOf(Coordinate(i, j) to c.toInt())
                    } else {
                        listOf()
                    }
                }
            }.toMap()

            return Sudoku(valueMap)
        }

    }
}

open class SudokuInput(val values: Map<Coordinate, Int>, val groups: List<List<Coordinate>>)
