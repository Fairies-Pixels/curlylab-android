package fairies.pixels.curlyLabAndroid.presentation.hairTyping

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class PorosityTypesTest {

    @Test
    fun `getResultNameByDbCode returns capitalized result name`() {
        val result = PorosityTypes.getResultNameByDbCode("HIGH")

        assertEquals("Пористые", result)
    }

    @Test
    fun `getResultNameByDbCode returns null for unknown dbCode`() {
        val result = PorosityTypes.getResultNameByDbCode("UNKNOWN")

        assertNull(result)
    }

    @Test
    fun `getDbCodeByResultName returns capitalized dbCode`() {
        val result = PorosityTypes.getDbCodeByResultName("пористые")

        assertEquals("HIGH", result)
    }

    @Test
    fun `getDbCodeByResultName returns null for unknown resultName`() {
        val result = PorosityTypes.getDbCodeByResultName("unknown")

        assertNull(result)
    }
}