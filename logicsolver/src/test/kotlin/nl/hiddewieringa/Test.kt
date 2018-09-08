package nl.hiddewieringa

import org.junit.Assert.assertEquals
import org.junit.Test

class Test {

    @Test
    fun x() {
        assertEquals(1, 1)
    }

    @Test
    fun fail() {
        assertEquals(1, 2)
    }
}
