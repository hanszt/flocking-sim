package hzt.utils

import hzt.utils.FxKtUtils.firstLetterUpperCase
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class FxKtUtilsTest {

    @Test
    fun testFirstLetterUpperCase() {
        assertEquals("", "".firstLetterUpperCase())
        assertEquals("A", "a".firstLetterUpperCase())
        assertEquals("Hallo", "hAllO".firstLetterUpperCase())
    }
}
