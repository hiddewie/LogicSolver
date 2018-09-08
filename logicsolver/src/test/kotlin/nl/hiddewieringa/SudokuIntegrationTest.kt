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
        /*
            Input:
                    4 5     9
            9 5       7 3 2
              8 2   6   1
            6 7   4   8 9   2
              1 5 4   9
                            8
              3     7       1
                7 8       4
              4 6 3   2 7   5

            Output:
            3 6 1 2 4 5 8 7 9
            9 5 4 1 8 7 3 2 6
            7 8 2 9 6 3 1 5 4
            6 7 3 4 5 8 9 1 2
            8 1 5 6 2 9 4 3 7
            4 2 9 7 3 1 5 6 8
            2 3 8 5 7 4 6 9 1
            5 9 7 8 1 6 2 4 3
            1 4 6 3 9 2 7 8 5
         */
        val valueMap = mapOf(
                Coordinate(1, 5) to 4,
                Coordinate(1, 6) to 5,
                Coordinate(1, 9) to 9,
                Coordinate(2, 1) to 9,
                Coordinate(2, 2) to 5,
                Coordinate(2, 6) to 7,
                Coordinate(2, 7) to 3,
                Coordinate(2, 8) to 2,
                Coordinate(3, 2) to 8,
                Coordinate(3, 3) to 2,
                Coordinate(3, 5) to 6,
                Coordinate(3, 7) to 1,
                Coordinate(4, 1) to 6,
                Coordinate(4, 2) to 7,
                Coordinate(4, 4) to 4,
                Coordinate(4, 6) to 8,
                Coordinate(4, 7) to 9,
                Coordinate(4, 9) to 2,
                Coordinate(5, 2) to 1,
                Coordinate(5, 3) to 5,
                Coordinate(5, 4) to 6,
                Coordinate(5, 6) to 9,
                Coordinate(5, 7) to 4,
                Coordinate(6, 9) to 8,
                Coordinate(7, 2) to 3,
                Coordinate(7, 5) to 7,
                Coordinate(7, 9) to 1,
                Coordinate(8, 3) to 7,
                Coordinate(8, 4) to 8,
                Coordinate(8, 8) to 4,
                Coordinate(9, 2) to 4,
                Coordinate(9, 3) to 6,
                Coordinate(9, 4) to 3,
                Coordinate(9, 6) to 2,
                Coordinate(9, 7) to 7,
                Coordinate(9, 9) to 5
        )

        val input = Sudoku(valueMap)
        val solver = SudokuSolver(input)
        val output = solver.solve()

        val expected = SudokuOutput(mapOf(
                Coordinate(1, 1) to 3,
                Coordinate(1, 2) to 6,
                Coordinate(1, 3) to 1,
                Coordinate(1, 4) to 2,
                Coordinate(1, 5) to 4,
                Coordinate(1, 6) to 5,
                Coordinate(1, 7) to 8,
                Coordinate(1, 8) to 7,
                Coordinate(1, 9) to 9,
                Coordinate(2, 1) to 9,
                Coordinate(2, 2) to 5,
                Coordinate(2, 3) to 4,
                Coordinate(2, 4) to 1,
                Coordinate(2, 5) to 8,
                Coordinate(2, 6) to 7,
                Coordinate(2, 7) to 3,
                Coordinate(2, 8) to 2,
                Coordinate(2, 9) to 6,
                Coordinate(3, 1) to 7,
                Coordinate(3, 2) to 8,
                Coordinate(3, 3) to 2,
                Coordinate(3, 4) to 9,
                Coordinate(3, 5) to 6,
                Coordinate(3, 6) to 3,
                Coordinate(3, 7) to 1,
                Coordinate(3, 8) to 5,
                Coordinate(3, 9) to 4,
                Coordinate(4, 1) to 6,
                Coordinate(4, 2) to 7,
                Coordinate(4, 3) to 3,
                Coordinate(4, 4) to 4,
                Coordinate(4, 5) to 5,
                Coordinate(4, 6) to 8,
                Coordinate(4, 7) to 9,
                Coordinate(4, 8) to 1,
                Coordinate(4, 9) to 2,
                Coordinate(5, 1) to 8,
                Coordinate(5, 2) to 1,
                Coordinate(5, 3) to 5,
                Coordinate(5, 4) to 6,
                Coordinate(5, 5) to 2,
                Coordinate(5, 6) to 9,
                Coordinate(5, 7) to 4,
                Coordinate(5, 8) to 3,
                Coordinate(5, 9) to 7,
                Coordinate(6, 1) to 4,
                Coordinate(6, 2) to 2,
                Coordinate(6, 3) to 9,
                Coordinate(6, 4) to 7,
                Coordinate(6, 5) to 3,
                Coordinate(6, 6) to 1,
                Coordinate(6, 7) to 5,
                Coordinate(6, 8) to 6,
                Coordinate(6, 9) to 8,
                Coordinate(7, 1) to 2,
                Coordinate(7, 2) to 3,
                Coordinate(7, 3) to 8,
                Coordinate(7, 4) to 5,
                Coordinate(7, 5) to 7,
                Coordinate(7, 6) to 4,
                Coordinate(7, 7) to 6,
                Coordinate(7, 8) to 9,
                Coordinate(7, 9) to 1,
                Coordinate(8, 1) to 5,
                Coordinate(8, 2) to 9,
                Coordinate(8, 3) to 7,
                Coordinate(8, 4) to 8,
                Coordinate(8, 5) to 1,
                Coordinate(8, 6) to 6,
                Coordinate(8, 7) to 2,
                Coordinate(8, 8) to 4,
                Coordinate(8, 9) to 3,
                Coordinate(9, 1) to 1,
                Coordinate(9, 2) to 4,
                Coordinate(9, 3) to 6,
                Coordinate(9, 4) to 3,
                Coordinate(9, 5) to 9,
                Coordinate(9, 6) to 2,
                Coordinate(9, 7) to 7,
                Coordinate(9, 8) to 8,
                Coordinate(9, 9) to 5
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
