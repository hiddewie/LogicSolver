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
        val solver = SudokuSolver()
        val output = solver.solve(input)

        val expected = SudokuOutput(valueMap)
        assertEquals(true, output.isLeft())
        assertEquals(expected, output.left())
    }

    @Test
    fun solveSudoku() {
        val input = Sudoku.readFromString("""
 . . . . 4 5 . . 9
 9 5 . . . 7 3 2 .
 . 8 2 . 6 . 1 . .
 6 7 . 4 . 8 9 . 2
 . 1 5 6 . 9 4 . .
 . . . . . . . . 8
 . 3 . . 7 . . . 1
 . . 7 8 . . . 4 .
 . 4 6 3 . 2 7 . 5
""")

        val solver = SudokuSolver()
        val output = solver.solve(input)

        val expected = """
3 6 1 2 4 5 8 7 9
9 5 4 1 8 7 3 2 6
7 8 2 9 6 3 1 5 4
6 7 3 4 5 8 9 1 2
8 1 5 6 2 9 4 3 7
4 2 9 7 3 1 5 6 8
2 3 8 5 7 4 6 9 1
5 9 7 8 1 6 2 4 3
1 4 6 3 9 2 7 8 5
"""

        assertEquals(true, output.isLeft())
        assertEquals(expected.trim(), output.left().toString().trim())
    }

    @Test
    fun solveSudokuX() {
        val input = SudokuX.readFromString("""
 . 3 . . 8 . . . .
 9 . 6 5 3 7 . . .
 2 . . . 9 . . . 5
 . . 3 . . . 1 . 8
 . . 9 8 . 6 3 . .
 8 . 5 . . . 6 . .
 1 . . . 6 . . . 4
 . . . 1 5 8 7 . 2
 . . . . 2 . . 1 .
""")

        val solver = SudokuSolver()
        val output = solver.solve(input)

        val expected = """
5 3 1 2 8 4 9 7 6
9 4 6 5 3 7 2 8 1
2 8 7 6 9 1 4 3 5
6 7 3 9 4 5 1 2 8
4 2 9 8 1 6 3 5 7
8 1 5 3 7 2 6 4 9
1 5 2 7 6 3 8 9 4
3 9 4 1 5 8 7 6 2
7 6 8 4 2 9 5 1 3
"""

        assertEquals(true, output.isLeft())
        assertEquals(expected.trim(), output.left().toString().trim())
    }

    @Test
    fun solveSudokuHyper() {
        val input = SudokuHyper.readFromString("""
 7 . 3 . . 8 5 . .
 . . . . . 5 1 . 9
 5 . . . . . . 7 .
 . . 4 . . . . 3 8
 . . 6 . 5 . 7 . .
 8 3 . . . . 2 . .
 . 1 . . . . . . 4
 6 . 9 2 . . . . .
 . . 5 4 . . 9 . 2
""")

        val solver = SudokuSolver()
        val output = solver.solve(input)

        val expected = """
7 9 3 1 4 8 5 2 6
4 6 2 3 7 5 1 8 9
5 8 1 9 6 2 4 7 3
1 5 4 7 2 9 6 3 8
9 2 6 8 5 3 7 4 1
8 3 7 6 1 4 2 9 5
2 1 8 5 9 7 3 6 4
6 4 9 2 3 1 8 5 7
3 7 5 4 8 6 9 1 2
"""

        assertEquals(true, output.isLeft())
        assertEquals(expected.trim(), output.left().toString().trim())
    }

    @Test
    fun failToSolveUnsolveableSudoku() {
        val valueMap = mapOf(
                Coordinate(1, 1) to 1
        )

        val input = Sudoku(valueMap)
        val solver = SudokuSolver()
        val output = solver.solve(input)

        val expected = listOf(
                LogicSolveError("No more conclusions, cannot solve")
        )
        assertEquals(true, output.isRight())
        assertEquals(expected, output.right())
    }
}
