package fairies.pixels.curlyLabAndroid.presentation.hairTyping

import junit.framework.TestCase.assertEquals
import org.junit.Test

class ColoredTypesTest {

    @Test
    fun `colored enum should contain correct values`() {
        assertEquals("c", ColoredTypes.COLORED.code)
        assertEquals("окрашенные", ColoredTypes.COLORED.result)
    }

    @Test
    fun `not colored enum should contain correct values`() {
        assertEquals("n", ColoredTypes.NOT_COLORED.code)
        assertEquals("неокрашенные", ColoredTypes.NOT_COLORED.result)
    }

    @Test
    fun `colored types should contain two values`() {
        assertEquals(2, ColoredTypes.entries.size)
    }
}