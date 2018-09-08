package nl.hiddewieringa


// Input


data class LogicSolveError(override val message: String) : Exception(message)

interface LogicPuzzleInput<I, O> {
    fun solve(): OneOf<O, List<LogicSolveError>>
}

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
        Coordinate(i / 3 + it / 3, i % 3 + it % 3)
    }
}

class Sudoku(values: Map<Coordinate, Int>) : SudokuInput(values, (1..9).flatMap {
    listOf(row(it), column(it), block(it))
})

open class SudokuInput(val values: Map<Coordinate, Int>, val groups: List<List<Coordinate>>) : LogicPuzzleInput<SudokuInput, SudokuOutput> {
    override fun solve(): OneOf<SudokuOutput, List<LogicSolveError>> {
        return OneOf.left(SudokuOutput(mapOf()))
    }
}
