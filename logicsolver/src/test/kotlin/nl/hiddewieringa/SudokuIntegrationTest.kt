package nl.hiddewieringa

import org.junit.Assert.assertEquals
import org.junit.Test

class SudokuIntegrationTest {

    @Test
    fun solveAlreadySolvedSudoku() {
        val valueMap = (1..9).flatMap { i ->
            (1..9).map { j ->
                Coordinate(i, j) to (1 + ((i - 1) + (j - 1)) % 9)
            }
        }.toMap()

        val input = Sudoku(valueMap)
        val solver = SudokuSolver(input)
        val output = solver.solve()

        val expected = SudokuOutput(valueMap)
        assertEquals(true, output.isLeft())
        assertEquals(expected, output.left())
    }

    @Test
    fun solveSudoku() {
        val valueMap = mapOf(
                Coordinate(1, 1) to 1
        )

        val input = Sudoku(valueMap)
        val solver = SudokuSolver(input)
        val output = solver.solve()

        val expected = SudokuOutput(mapOf(
                Coordinate(1, 1) to 1
        ))
        assertEquals(true, output.isLeft())
        assertEquals(expected, output.left())
    }

    @Test
    fun failToSolveUnsolveableSudoku() {
        val valueMap = mapOf(
                Coordinate(1, 1) to 1
        )

        val input = Sudoku(valueMap)
        val solver = SudokuSolver(input)
        val output = solver.solve()

        val expected = listOf(
                LogicSolveError("No more conclusions, cannot solve")
        )
        assertEquals(true, output.isRight())
        assertEquals(expected, output.right())
    }
}
