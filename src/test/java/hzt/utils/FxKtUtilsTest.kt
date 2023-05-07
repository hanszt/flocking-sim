package hzt.utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class FxKtUtilsTest {

    @Test
    fun testFirstCharUpperCase() {
        assertEquals("", "".firstCharUpperCase())
        assertEquals("A", "a".firstCharUpperCase())
        assertEquals("Hallo", "hAllO".firstCharUpperCase())
    }

    @Test
    fun testReplaceFirstCharUpperCase() {
        assertEquals("", "".replaceFirstChar(Char::uppercase))
        assertEquals("A", "a".replaceFirstChar(Char::uppercase))
        assertEquals("Hallo", "hAllO".lowercase().replaceFirstChar(Char::uppercase))
    }
}
