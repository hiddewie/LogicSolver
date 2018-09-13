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
        val expected = setOf(
                OneOf.left<Value, NotAllowed>(Value(Coordinate(1, 9), 9))
        )
        assertEquals(expected, GroupStrategy().missingValue(data))
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
        }.toSet()
        assertEquals(expected, GroupStrategy().filledValueNotAllowedInGroup(data))
    }

    @Test
    fun singleValueAllowed() {
        val data = listOf(
                SudokuSolveData(Coordinate(1, 1), null, (1..8).map { it })
        )
        val expected = setOf(
                OneOf.left<Value, NotAllowed>(Value(Coordinate(1, 1), 9))
        )
        assertEquals(expected, GroupStrategy().singleValueAllowed(data))
    }

    @Test
    fun filledValueNotAllowedRest() {
        val data = listOf(
                SudokuSolveData(Coordinate(1, 1), 1, listOf())
        )
        val expected = (2..9).map {
            OneOf.right<Value, NotAllowed>(NotAllowed(Coordinate(1, 1), it))
        }.toSet()
        assertEquals(expected, GroupStrategy().filledValueRestNotAllowed(data))
    }

    @Test
    fun overlapStrategyTest() {
        // No overlap
        assertEquals(setOf(), SudokuSolver().overlappingConclusionsForGroups(listOf(), listOf(), mutableMapOf()))

        val group1 = row(2).map {
            SudokuSolveData(it, null, listOf())
        }
        val group2 = column(2).map {
            SudokuSolveData(it, null, listOf())
        }
        // One overlap
        assertEquals(setOf(), SudokuSolver().overlappingConclusionsForGroups(group1, group2, mutableMapOf()))

        val group3 = listOf(
                SudokuSolveData(Coordinate(1,1), null, listOf(1)),
                SudokuSolveData(Coordinate(1,2), null, listOf(1)),
                SudokuSolveData(Coordinate(1,3), null, listOf(1)),
                SudokuSolveData(Coordinate(2,1), null, listOf()),
                SudokuSolveData(Coordinate(2,2), null, listOf()),
                SudokuSolveData(Coordinate(2,3), null, listOf()),
                SudokuSolveData(Coordinate(3,1), null, listOf(1)),
                SudokuSolveData(Coordinate(3,2), null, listOf(1)),
                SudokuSolveData(Coordinate(3,3), null, listOf(1))
        )
        val data = (block(1) + row(2)).toSet().map {
            it to SudokuSolveData(it, null, if (it.a != 2) listOf(1) else listOf())
        }.toMap(mutableMapOf())

        val expected = (4..9).map {
            OneOf.right<Value, NotAllowed>(NotAllowed(Coordinate(2, it), 1))
        }.toSet()

        // Three overlap (no conclusion)
        assertEquals(setOf(), SudokuSolver().overlappingConclusionsForGroups(group1, group3, data))
        // Three overlap other way around (conclusions)
        assertEquals(expected, SudokuSolver().overlappingConclusionsForGroups(group3, group1, data))
    }
}
