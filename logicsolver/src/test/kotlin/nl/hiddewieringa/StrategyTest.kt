package nl.hiddewieringa

import org.junit.Test
import kotlin.test.assertEquals

class StrategyTest {

    @Test
    fun missingValue() {
        val data = (1..8).map {
            SudokuSolveData(Coordinate(1, it), it, listOf())
        } + listOf(
                SudokuSolveData(Coordinate(1, 9), null, listOf())
        )
        val expected = listOf(
                OneOf.left<Value, NotAllowed>(Value(Coordinate(1, 9), 9))
        )
        assertEquals(expected, GroupStrategy(data).missingValue(data))
    }

    @Test
    fun missingNotAllowed() {
        val data = listOf(
                SudokuSolveData(Coordinate(1, 1), 1, listOf())
        ) + (2..9).map {
            SudokuSolveData(Coordinate(1, it), null, listOf())
        }
        val expected = (2..9).map {
            OneOf.right<Value, NotAllowed>(NotAllowed(Coordinate(1, it), 1))
        }
        assertEquals(expected, GroupStrategy(data).missingNotAllowed(data))
    }

    @Test
    fun singleValueAllowed() {
        val data = listOf(
                SudokuSolveData(Coordinate(1, 1), null, (1..8).map { it })
        )
        val expected = listOf(
                OneOf.left<Value, NotAllowed>(Value(Coordinate(1, 1), 9))
        )
        assertEquals(expected, GroupStrategy(data).singleValueAllowed(data))
    }
}
