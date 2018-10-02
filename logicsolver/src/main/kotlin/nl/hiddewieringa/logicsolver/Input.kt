package nl.hiddewieringa.logicsolver

import java.util.regex.Pattern

data class LogicSolveError(override val message: String) : Exception(message)

data class Coordinate(val a: Int, val b: Int)

val sudokuRange = (1..9)

fun coordinates(x: (i: Int) -> Int, y: (I: Int) -> Int): List<Coordinate> {
    return sudokuRange.map {
        Coordinate(x(it), y(it))
    }
}

fun row(i: Int): List<Coordinate> {
    return coordinates({ i }, { it })
}

fun column(i: Int): List<Coordinate> {
    return coordinates({ it }, { i })
}

fun block(i: Int): List<Coordinate> {
    return coordinates({
        1 + 3 * ((i - 1) / 3) + (it - 1) / 3
    }, {
        1 + 3 * ((i - 1) % 3) + (it - 1) % 3
    })
}

fun diagonalTB(): List<Coordinate> {
    return coordinates({ it }, { 10 - it })
}

fun diagonalBT(): List<Coordinate> {
    return coordinates({ it }, { it })
}

fun hyperBlock(i: Int): List<Coordinate> {
    return coordinates({
        2 + 4 * ((i - 1) / 2) + (it - 1) / 3
    }, {
        2 + 4 * ((i - 1) % 2) + (it - 1) % 3
    })
}

val sudokuCoordinates = sudokuRange.flatMap { i ->
    sudokuRange.map { j ->
        Coordinate(i, j)
    }
}

fun readSudokuValueMapFromString(s: String): Map<Coordinate, Int> {
    return readValueMapFromString(s, sudokuCoordinates)
}

fun readValueMapFromString(s: String, coordinates: List<Coordinate>): Map<Coordinate, Int> {
    val split = s.split(Pattern.compile("\\s+")).filter { it.isNotEmpty() }
    if (split.size != coordinates.size) {
        throw Exception("Input size should be 81 non-whitespace strings")
    }

    return split.zip(coordinates).flatMap { pair ->
        val c = pair.first
        if (c.length == 1 && c.toIntOrNull() != null && c.toInt() >= 1 && c.toInt() <= 9) {
            listOf(pair.second to c.toInt())
        } else {
            listOf()
        }
    }.toMap()
}

val sudokuGroups = sudokuRange.flatMap {
    listOf(row(it), column(it), block(it))
}

class Sudoku(values: Map<Coordinate, Int>) : SudokuInput(values, sudokuGroups, sudokuCoordinates) {
    companion object {
        fun readFromString(s: String): Sudoku {
            return Sudoku(readSudokuValueMapFromString(s))
        }
    }
}

class SudokuHyper(values: Map<Coordinate, Int>) : SudokuInput(values, sudokuGroups + (1..4).map { hyperBlock(it) }, sudokuCoordinates) {
    companion object {
        fun readFromString(s: String): SudokuHyper {
            return SudokuHyper(readSudokuValueMapFromString(s))
        }
    }
}

class SudokuX(values: Map<Coordinate, Int>) : SudokuInput(values, sudokuGroups + listOf(diagonalBT(), diagonalTB()), sudokuCoordinates) {
    companion object {
        fun readFromString(s: String): SudokuX {
            return SudokuX(readSudokuValueMapFromString(s))
        }
    }
}

val doubleSudokuRange = (1..15)

val doubleSudokuCoordinates = doubleSudokuRange.flatMap { i ->
    sudokuRange.map { j ->
        Coordinate(i, j)
    }
}

val doubleSudokuGroups = doubleSudokuRange.map { row(it) } +
        sudokuRange.flatMap { i -> listOf(column(i), coordinates({ it + 6 }, { i })) } +
        doubleSudokuRange.map { block(it) }

class SudokuDouble(values: Map<Coordinate, Int>) : SudokuInput(values, doubleSudokuGroups, doubleSudokuCoordinates) {
    companion object {
        fun readFromString(s: String): SudokuDouble {
            return SudokuDouble(readValueMapFromString(s, doubleSudokuCoordinates))
        }
    }
}

open class SudokuInput(val values: Map<Coordinate, Int>, val groups: List<List<Coordinate>>, val coordinates: List<Coordinate>)
