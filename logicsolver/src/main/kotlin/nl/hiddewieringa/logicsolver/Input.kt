package nl.hiddewieringa.logicsolver

import java.util.regex.Pattern

data class LogicSolveError(override val message: String) : Exception(message)

data class Coordinate(val a: Int, val b: Int) : Comparable<Coordinate> {
    override fun compareTo(other: Coordinate): Int {
        return compareBy<Coordinate>({ it.a }, { it.b })
                .compare(this, other)
    }
}

val sudokuRange = (1..9)

fun coordinates(range: IntRange, x: (i: Int) -> Int, y: (I: Int) -> Int): Set<Coordinate> {
    return range.map {
        Coordinate(x(it), y(it))
    }.toSet()
}

val sudokuPuzzleValues = (1..9).toSet()

fun Collection<Coordinate>.translate(dx: Int, dy: Int): Set<Coordinate> {
    return map {
        Coordinate(it.a + dx, it.b + dy)
    }.toSet()
}

fun Collection<Coordinate>.translateX(dx: Int): Set<Coordinate> {
    return translate(dx, 0)
}

fun Collection<Coordinate>.translateY(dy: Int): Set<Coordinate> {
    return translate(0, dy)
}

fun row(range: IntRange, i: Int): Set<Coordinate> {
    return coordinates(range, { i }, { it })
}

fun column(range: IntRange, i: Int): Set<Coordinate> {
    return coordinates(range, { it }, { i })
}

fun block(i: Int, width: Int, height: Int): Set<Coordinate> {
    return coordinates((1..(width * height)), {
        1 + height * ((i - 1) / height) + (it - 1) / width
    }, {
        1 + width * ((i - 1) % height) + (it - 1) % width
    })
}

fun diagonalTB(): Set<Coordinate> {
    return coordinates(sudokuRange, { it }, { sudokuRange.max()!! + 1 - it })
}

fun diagonalBT(): Set<Coordinate> {
    return coordinates(sudokuRange, { it }, { it })
}

fun hyperBlock(i: Int): Set<Coordinate> {
    return coordinates(sudokuRange, {
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

fun validSudokuValue(value: String): Boolean {
    return value.length == 1 && value.toIntOrNull() != null && value.toInt() >= 1 && value.toInt() <= 9
}

fun readValueMapFromString(s: String, coordinates: List<Coordinate>): Map<Coordinate, Int> {
    val split = s.split(Pattern.compile("\\s+")).filter { it.isNotEmpty() }
    if (split.size != coordinates.size) {
        throw Exception("Input size (${split.size}) should be ${coordinates.size} non-whitespace strings")
    }

    return split.zip(coordinates.sorted())
            .filter { validSudokuValue(it.first) }
            .map { it.second to it.first.toInt() }
            .toMap()
}

val sudokuGroups = sudokuRange.flatMap {
    listOf(row(sudokuRange, it), column(sudokuRange, it), block(it, 3, 3))
}

class Sudoku(values: Map<Coordinate, Int>) : SudokuInput(values, sudokuGroups, sudokuCoordinates, sudokuPuzzleValues) {
    companion object {
        fun readFromString(s: String): Sudoku {
            return Sudoku(readSudokuValueMapFromString(s))
        }
    }
}

val hyperGroups = sudokuGroups + (1..4).map { hyperBlock(it) }

class SudokuHyper(values: Map<Coordinate, Int>) : SudokuInput(values, hyperGroups, sudokuCoordinates, sudokuPuzzleValues) {
    companion object {
        fun readFromString(s: String): SudokuHyper {
            return SudokuHyper(readSudokuValueMapFromString(s))
        }
    }
}

class SudokuX(values: Map<Coordinate, Int>) : SudokuInput(values, sudokuGroups + listOf(diagonalBT(), diagonalTB()), sudokuCoordinates, sudokuPuzzleValues) {
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

val doubleSudokuGroups = doubleSudokuRange.map { row(sudokuRange, it) } +
        sudokuRange.flatMap { i -> listOf(column(sudokuRange, i), column(sudokuRange, i).translateX(6)) } +
        doubleSudokuRange.map { block(it, 3, 3) }

class SudokuDouble(values: Map<Coordinate, Int>) : SudokuInput(values, doubleSudokuGroups, doubleSudokuCoordinates, sudokuPuzzleValues) {
    companion object {
        fun readFromString(s: String): SudokuDouble {
            return SudokuDouble(readValueMapFromString(s, doubleSudokuCoordinates))
        }
    }
}

val samuraiCoordinates = (sudokuCoordinates +
        sudokuCoordinates.translate(6, 6) +
        sudokuCoordinates.translate(12, 12) +
        sudokuCoordinates.translateX(12) +
        sudokuCoordinates.translateY(12)
        ).toSet().toList()

val samuraiGroups = (sudokuGroups +
        sudokuGroups.map { it.translate(6, 6) } +
        sudokuGroups.map { it.translate(12, 12) } +
        sudokuGroups.map { it.translateX(12) } +
        sudokuGroups.map { it.translateY(12) }
        ).toSet().toList()

class SudokuSamurai(values: Map<Coordinate, Int>) : SudokuInput(values, samuraiGroups, samuraiCoordinates, sudokuPuzzleValues) {
    companion object {
        fun readFromString(s: String): SudokuSamurai {
            return SudokuSamurai(readValueMapFromString(s, samuraiCoordinates))
        }
    }
}

val sudokuTinyRange = (1..6)

val sudokuTinyCoordinates = sudokuTinyRange.flatMap { i ->
    sudokuTinyRange.map { j ->
        Coordinate(i, j)
    }
}

val sudokuTinyGroups = sudokuTinyRange.flatMap {
    listOf(row(sudokuTinyRange, it), column(sudokuTinyRange, it), block(it, 3, 2))
}

val sudokuTinyPuzzleValues = (1..6).toSet()

class SudokuTiny(values: Map<Coordinate, Int>) : SudokuInput(values, sudokuTinyGroups, sudokuTinyCoordinates, sudokuTinyPuzzleValues) {
    companion object {
        fun readFromString(s: String): SudokuTiny {
            return SudokuTiny(readValueMapFromString(s, sudokuTinyCoordinates))
        }
    }
}

open class SudokuInput(val values: Map<Coordinate, Int>, val groups: List<Set<Coordinate>>, val coordinates: List<Coordinate>, val puzzleValues: Set<Int>)
