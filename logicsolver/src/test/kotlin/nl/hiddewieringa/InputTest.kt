package nl.hiddewieringa

import org.junit.Test
import kotlin.test.assertEquals

class InputTest {

    @Test
    fun testRow() {
        val expected = listOf(
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
        val expected = listOf(
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
        val expected = listOf(
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
    fun coordinateTest() {
        assertEquals(1, Coordinate(1, 2).a)
        assertEquals(2, Coordinate(1, 2).b)
    }
}
