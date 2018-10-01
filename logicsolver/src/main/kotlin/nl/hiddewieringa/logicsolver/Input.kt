package nl.hiddewieringa.logicsolver

import java.util.regex.Pattern

data class LogicSolveError(override val message: String) : Exception(message)

data class Coordinate(val a: Int, val b: Int)

val sudokuRange = (1..9)

fun row(i: Int): List<Coordinate> {
    return sudokuRange.map {
        Coordinate(i, it)
    }
}

fun column(i: Int): List<Coordinate> {
    return sudokuRange.map {
        Coordinate(it, i)
    }
}

fun block(i: Int): List<Coordinate> {
    return sudokuRange.map {
        Coordinate(1 + 3 * ((i - 1) / 3) + (it - 1) / 3, 1 + 3 * ((i - 1) % 3) + (it - 1) % 3)
    }
}

fun diagonalTB(): List<Coordinate> {
    return sudokuRange.map {
        Coordinate(it, 10 - it)
    }
}

fun diagonalBT(): List<Coordinate> {
    return sudokuRange.map {
        Coordinate(it, it)
    }
}

fun hyperBlock(i: Int): List<Coordinate> {
    return sudokuRange.map {
        Coordinate(2 + 4 * ((i - 1) / 2) + (it - 1) / 3, 2 + 4 * ((i - 1) % 2) + (it - 1) % 3)
    }
}

fun readValueMapFromString(s: String): Map<Coordinate, Int> {
    val split = s.split(Pattern.compile("\\s+")).filter { it.isNotEmpty() }
    if (split.size != 81) {
        throw Exception("Input size should be 81 non-whitespace strings")
    }

    return sudokuRange.flatMap { i ->
        sudokuRange.flatMap { j ->
            val c = split[9 * (i - 1) + (j - 1)]
            if (c.length == 1 && c.toIntOrNull() != null && c.toInt() >= 1 && c.toInt() <= 9) {
                listOf(Coordinate(i, j) to c.toInt())
            } else {
                listOf()
            }
        }
    }.toMap()
}

class Sudoku(values: Map<Coordinate, Int>) : SudokuInput(values, sudokuRange.flatMap {
    listOf(row(it), column(it), block(it))
}) {
    companion object {
        fun readFromString(s: String): Sudoku {
            return Sudoku(readValueMapFromString(s))
        }
    }
}

class SudokuHyper(values: Map<Coordinate, Int>) : SudokuInput(values, sudokuRange.flatMap {
    listOf(row(it), column(it), block(it))
} + (1..4).map { hyperBlock(it) }) {
    companion object {
        fun readFromString(s: String): SudokuHyper {
            return SudokuHyper(readValueMapFromString(s))
        }
    }
}

class SudokuX(values: Map<Coordinate, Int>) : SudokuInput(values, sudokuRange.flatMap {
    listOf(row(it), column(it), block(it))
} + listOf(diagonalBT(), diagonalTB())) {
    companion object {
        fun readFromString(s: String): SudokuX {
            return SudokuX(readValueMapFromString(s))
        }
    }
}

open class SudokuInput(val values: Map<Coordinate, Int>, val groups: List<List<Coordinate>>)
