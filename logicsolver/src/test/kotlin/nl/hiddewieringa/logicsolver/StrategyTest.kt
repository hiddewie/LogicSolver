package nl.hiddewieringa.logicsolver

import org.junit.Test
import kotlin.test.assertEquals

class StrategyTest {

    @Test
    fun missingValue() {
        val data = (1..8).map {
            SudokuSolveData(Coordinate(1, it), it, listOf())
        } + setOf(
                SudokuSolveData(Coordinate(1, 9), null, listOf())
        )
        val expected = setOf(
                OneOf.left<Value, NotAllowed>(Value(Coordinate(1, 9), 9))
        )
        assertEquals(expected, MissingValueGroupStrategy()(Pair(data.toSet(), (1..9).toSet())))
    }

    @Test
    fun missingNotAllowed() {
        val data = setOf(
                SudokuSolveData(Coordinate(1, 1), 1, listOf())
        ) + (2..9).map {
            SudokuSolveData(Coordinate(1, it), null, listOf())
        }
        val expected = (2..9).map {
            OneOf.right<Value, NotAllowed>(NotAllowed(Coordinate(1, it), 1))
        }.toSet()
        assertEquals(expected, FilledValueNotAllowedInGroupStrategy()(Pair(data, (1..9).toSet())))
    }

    @Test
    fun singleValueAllowed() {
        val data = setOf(
                SudokuSolveData(Coordinate(1, 1), null, (1..8).map { it })
        )
        val expected = setOf(
                OneOf.left<Value, NotAllowed>(Value(Coordinate(1, 1), 9))
        )
        assertEquals(expected, SingleValueAllowedStrategy()(Pair(data, (1..9).toSet())))
    }

    @Test
    fun filledValueNotAllowedRest() {
        val data = setOf(
                SudokuSolveData(Coordinate(1, 1), 1, listOf())
        )
        val expected = (2..9).map {
            OneOf.right<Value, NotAllowed>(NotAllowed(Coordinate(1, 1), it))
        }.toSet()
        assertEquals(expected, FilledValueRestNotAllowedStrategy()(Pair(data, (1..9).toSet())))
    }

    @Test
    fun overlapStrategyTest() {
        // No overlap
        assertEquals(setOf(), OverlappingGroupsStrategy().overlappingConclusionsForGroups(setOf(), setOf(), mapOf(), setOf()))

        val group1 = row(sudokuRange, 2).map {
            SudokuSolveData(it, null, listOf())
        }.toSet()
        val group2 = column(sudokuRange, 2).map {
            SudokuSolveData(it, null, listOf())
        }.toSet()

        // One overlap
        assertEquals(setOf(), OverlappingGroupsStrategy().overlappingConclusionsForGroups(group1, group2, mapOf(), (1..9).toSet()))

        val group3 = setOf(
                SudokuSolveData(Coordinate(1, 1), null, listOf(1)),
                SudokuSolveData(Coordinate(1, 2), null, listOf(1)),
                SudokuSolveData(Coordinate(1, 3), null, listOf(1)),
                SudokuSolveData(Coordinate(2, 1), null, listOf()),
                SudokuSolveData(Coordinate(2, 2), null, listOf()),
                SudokuSolveData(Coordinate(2, 3), null, listOf()),
                SudokuSolveData(Coordinate(3, 1), null, listOf(1)),
                SudokuSolveData(Coordinate(3, 2), null, listOf(1)),
                SudokuSolveData(Coordinate(3, 3), null, listOf(1))
        )
        val data = (block(1, 3, 3) + row(sudokuRange, 2)).toSet().map {
            it to SudokuSolveData(it, null, if (it.a != 2) listOf(1) else listOf())
        }.toMap(mutableMapOf())

        val expected = (4..9).map {
            OneOf.right<Value, NotAllowed>(NotAllowed(Coordinate(2, it), 1))
        }.toSet()

        // Three overlap (no conclusion)
        assertEquals(setOf(), OverlappingGroupsStrategy().overlappingConclusionsForGroups(group1, group3, data, (1..9).toSet()))
        // Three overlap other way around (conclusions)
        assertEquals(expected, OverlappingGroupsStrategy().overlappingConclusionsForGroups(group3, group1, data, (1..9).toSet()))
    }

    @Test
    fun twoNumbersTakeTwoPlacesStrategyTest() {
        val data = setOf(
                SudokuSolveData(Coordinate(1, 1), null, (3..9).toList()),
                SudokuSolveData(Coordinate(2, 1), null, (4..9).toList())
        ) + (3..9).map {
            SudokuSolveData(Coordinate(it, 1), null, listOf(1, 2))
        }

        val expected = setOf<Conclusion>(
                OneOf.right(NotAllowed(Coordinate(2, 1), 3))
        )
        assertEquals(expected, TwoNumbersTakeTwoPlacesStrategy()(Pair(data, (1..9).toSet())))
    }

    @Test
    fun twoNumbersOnlyInTwoPlaces() {
        val data = setOf(
                SudokuSolveData(Coordinate(1, 1), null, (3..9).toList()),
                SudokuSolveData(Coordinate(2, 1), null, (3..9).toList())
        ) + (3..9).map {
            SudokuSolveData(Coordinate(it, 1), null, listOf())
        }

        val expected = (3..9).flatMap {
            listOf(
                    OneOf.right<Value, NotAllowed>(NotAllowed(Coordinate(it, 1), 1)),
                    OneOf.right<Value, NotAllowed>(NotAllowed(Coordinate(it, 1), 2))
            )
        }.toSet()
        assertEquals(expected, TwoNumbersOnlyInTwoPlacesStrategy()(Pair(data, (1..9).toSet())))
    }
}
