package hzt.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class FxKtUtilsTest {

    @Test
    fun testFirstCharUpperCase() {
        Assertions.assertEquals("", "".firstCharUpperCase())
        Assertions.assertEquals("A", "a".firstCharUpperCase())
        Assertions.assertEquals("Hallo", "hAllO".firstCharUpperCase())
    }

    @Test
    fun testReplaceFirstCharUpperCase() {
        Assertions.assertEquals("", "".replaceFirstChar(Char::uppercase))
        Assertions.assertEquals("A", "a".replaceFirstChar(Char::uppercase))
        Assertions.assertEquals("Hallo", "hAllO".lowercase().replaceFirstChar(Char::uppercase))
    }
}