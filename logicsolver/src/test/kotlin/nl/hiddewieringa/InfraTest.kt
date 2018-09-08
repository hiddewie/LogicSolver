package nl.hiddewieringa

import org.junit.Test
import kotlin.test.assertEquals

class InfraTest {

    @Test
    fun oneOf() {
        assertEquals(true, OneOf.left<Int, Int>(1).isLeft())
        assertEquals(true, OneOf.right<Int, Int>(1).isRight())
    }

    @Test
    fun map() {
        assertEquals(2, OneOf.left<Int, Int>(1).mapLeft { 2 }.left())
        assertEquals(1, OneOf.left<Int, Int>(1).mapRight { 2 }.left())

        assertEquals(1, OneOf.right<Int, Int>(1).mapLeft { 2 }.right())
        assertEquals(2, OneOf.right<Int, Int>(1).mapRight { 2 }.right())
    }
}
