package fairies.pixels.curlyLabAndroid.presentation.hairTyping

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class ThicknessTypesTest {

    @Test
    fun `getResultNameByDbCode returns capitalized result name`() {
        val result = ThicknessTypes.getResultNameByDbCode("THIN")

        assertEquals("Тонкие", result)
    }

    @Test
    fun `getResultNameByDbCode returns null for unknown dbCode`() {
        val result = ThicknessTypes.getResultNameByDbCode("UNKNOWN")

        assertNull(result)
    }

    @Test
    fun `getDbCodeByResultName returns capitalized dbCode`() {
        val result = ThicknessTypes.getDbCodeByResultName("тонкие")

        assertEquals("THIN", result)
    }

    @Test
    fun `getDbCodeByResultName returns null for unknown resultName`() {
        val result = ThicknessTypes.getDbCodeByResultName("unknown")

        assertNull(result)
    }
}