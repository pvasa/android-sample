package dev.priyankvasa.sample.android.ui.util

import java.text.DecimalFormat
import java.util.NavigableMap
import java.util.TreeMap

fun Number.toNumberFormattedString(): String =
    DecimalFormat("#,###.##").format(this)

private val suffixes: NavigableMap<Long, String> =
    TreeMap<Long, String>().apply {
        put(1_000L, "K")
        put(1_000_000L, "M")
        put(1_000_000_000L, "B")
        put(1_000_000_000_000L, "T")
        put(1_000_000_000_000_000L, "P")
        put(1_000_000_000_000_000_000L, "E")
    }

private fun formatSuffixed(value: Long): String {
    // Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
    if (value == Long.MIN_VALUE) return formatSuffixed(Long.MIN_VALUE + 1)
    if (value < 0) return "-${formatSuffixed(-value)}"
    if (value < 1000) return value.toString() // deal with easy case

    val e = suffixes.floorEntry(value)!!
    val divideBy = e.key
    val suffix = e.value

    val truncated = value / (divideBy / 100) // the number part of the output times 100

    val hasDecimal = truncated / 100.0 != (truncated / 100).toDouble()

    return if (hasDecimal) {
        "${"%.2f".format(truncated / 100.0)}$suffix"
    } else {
        "${(truncated / 100)}$suffix"
    }
}

fun Long.toSuffixedShortString(): String = formatSuffixed(toLong())
