package nl.hiddewieringa.logicsolver

import org.junit.Test
import kotlin.test.assertEquals

class SolverTest {

    @Test
    fun solveDataToString() {
        assertEquals("SSD(Coordinate(a=1, b=2), null, [])", SudokuSolveData(Coordinate(1, 2), null, listOf()).toString())
        assertEquals("SSD(Coordinate(a=1, b=2), 1, [2])", SudokuSolveData(Coordinate(1, 2), 1, listOf(2)).toString())
    }
}
