package nl.hiddewieringa.logicsolver

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InputTest {

    @Test
    fun testRow() {
        val expected = setOf(
                Coordinate(1, 1),
                Coordinate(1, 2),
                Coordinate(1, 3),
                Coordinate(1, 4),
                Coordinate(1, 5),
                Coordinate(1, 6),
                Coordinate(1, 7),
                Coordinate(1, 8),
                Coordinate(1, 9)
        )
        assertEquals(expected, row(1))
    }

    @Test
    fun testCol() {
        val expected = setOf(
                Coordinate(1, 1),
                Coordinate(2, 1),
                Coordinate(3, 1),
                Coordinate(4, 1),
                Coordinate(5, 1),
                Coordinate(6, 1),
                Coordinate(7, 1),
                Coordinate(8, 1),
                Coordinate(9, 1)
        )
        assertEquals(expected, column(1))
    }

    @Test
    fun testBlock() {
        val expected = setOf(
                Coordinate(1, 1),
                Coordinate(1, 2),
                Coordinate(1, 3),
                Coordinate(2, 1),
                Coordinate(2, 2),
                Coordinate(2, 3),
                Coordinate(3, 1),
                Coordinate(3, 2),
                Coordinate(3, 3)
        )
        assertEquals(expected, block(1))
    }

    @Test
    fun testHyperBlock() {
        val expected = setOf(
                Coordinate(6, 6),
                Coordinate(6, 7),
                Coordinate(6, 8),
                Coordinate(7, 6),
                Coordinate(7, 7),
                Coordinate(7, 8),
                Coordinate(8, 6),
                Coordinate(8, 7),
                Coordinate(8, 8)
        )
        assertEquals(expected, hyperBlock(4))
    }

    @Test
    fun coordinateTest() {
        assertEquals(1, Coordinate(1, 2).a)
        assertEquals(2, Coordinate(1, 2).b)
    }

    @Test
    fun testDiagonal() {
        val expected = setOf(
                Coordinate(1, 1),
                Coordinate(2, 2),
                Coordinate(3, 3),
                Coordinate(4, 4),
                Coordinate(5, 5),
                Coordinate(6, 6),
                Coordinate(7, 7),
                Coordinate(8, 8),
                Coordinate(9, 9)
        )
        assertEquals(expected, diagonalBT())

        val expected2 = setOf(
                Coordinate(1, 9),
                Coordinate(2, 8),
                Coordinate(3, 7),
                Coordinate(4, 6),
                Coordinate(5, 5),
                Coordinate(6, 4),
                Coordinate(7, 3),
                Coordinate(8, 2),
                Coordinate(9, 1)
        )
        assertEquals(expected2, diagonalTB())
    }

    @Test
    fun testTranslate() {
        val data = setOf(Coordinate(10, 11))
        val expectedX = setOf(Coordinate(20, 11))
        val expectedY = setOf(Coordinate(10, 21))
        val expectedXY = setOf(Coordinate(20, 21))
        assertEquals(expectedX, data.translateX(10))
        assertEquals(expectedY, data.translateY(10))
        assertEquals(expectedXY, data.translate(10, 10))
    }

    @Test
    fun testCoordinateCompare() {
        assertTrue { Coordinate(1, 1) == Coordinate(1, 1) }
        assertTrue { Coordinate(2, 1) > Coordinate(1, 1) }
        assertTrue { Coordinate(2, 1) > Coordinate(1, 2) }
    }

    @Test
    fun testValidSudokuValue() {
        assertTrue { validSudokuValue("1") }
        assertTrue { validSudokuValue("9") }
        assertFalse { validSudokuValue("0") }
        assertFalse { validSudokuValue("10") }
        assertFalse { validSudokuValue("jknsd") }
        assertFalse { validSudokuValue("x") }
        assertFalse { validSudokuValue("") }
    }
}
