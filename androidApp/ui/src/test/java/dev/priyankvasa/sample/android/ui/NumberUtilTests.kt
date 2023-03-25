package dev.priyankvasa.sample.android.ui

import dev.priyankvasa.sample.android.ui.util.toSuffixedShortString
import org.junit.Test
import kotlin.test.assertEquals

class NumberUtilTests {
    @Test
    fun testLongToSuffixedShortString() {
        val numbers = longArrayOf(
            0,
            5,
            999,
            1120,
            -5821,
            10500,
            -101800,
            2000000,
            -7810000,
            92150000,
            123200000,
            9999999,
            999999999999999999L,
            1230000000000000L,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
        )
        val expected = arrayOf(
            "0",
            "5",
            "999",
            "1.12K",
            "-5.82K",
            "10.50K",
            "-101.80K",
            "2M",
            "-7.81M",
            "92.15M",
            "123.20M",
            "9.99M",
            "999.99P",
            "1.23P",
            "-9.22E",
            "9.22E",
        )

        for (i in numbers.indices) {
            val n = numbers[i]
            val formatted: String = n.toSuffixedShortString()
            println("$n => $formatted")
            assertEquals(
                expected[i],
                formatted,
                message = "Expected: ${expected[i]} but found: $formatted",
            )
        }
    }
}
