package dev.priyankvasa.sample.androidApp

import dev.priyankvasa.sample.androidApp.appBuild.BuildVersion
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BuildVersionTest {
    @Test
    fun givenTwoVersionsAreEqual_whenCompared_thenResultIsCorrect() {
        val a = BuildVersion("4.57.0-DEVELOPMENT")
        val b = BuildVersion("4.57.0-235324")
        assertEquals(a, b)
    }

    @Test
    fun givenMinorVersionOfOneIsLessThanAnother_whenCompared_thenResultIsCorrect() {
        val a = BuildVersion("4.56.0-DEVELOPMENT")
        val b = BuildVersion("4.57.0-235324")
        assertTrue(a < b)
        assertTrue(b > a)
    }

    @Test
    fun givenMajorVersionOfOneIsLessThanAnother_whenCompared_thenResultIsCorrect() {
        val a = BuildVersion("4.56.0-34534")
        val b = BuildVersion("5.56.0-GGSDA")
        assertTrue(a < b)
        assertTrue(b > a)
    }

    @Test
    fun givenPatchVersionOfOneIsLessThanAnother_whenCompared_thenResultIsCorrect() {
        val a = BuildVersion("4.56.0")
        val b = BuildVersion("4.56.1")
        assertTrue(a < b)
        assertTrue(b > a)
    }

    @Test
    fun givenVersionCodeOfOneIsLessThanAnother_whenCompared_thenResultIsCorrect() {
        val a = BuildVersion("4.56.0-3464")
        val b = BuildVersion("4.56.0-3465")
        assertTrue(a < b)
        assertTrue(b > a)
    }

    @Test
    fun givenAInvalidVersion_whenVersionIsConstructed_thenExceptionIsThrown() {
        assertFailsWith(IllegalArgumentException::class) {
            BuildVersion("4.56a.1")
        }
        assertFailsWith(IllegalArgumentException::class) {
            BuildVersion("4a.56.1")
        }
    }

    @Test
    fun givenAnInvalidVersionCode_whenVersionIsConstructed_thenVersionCodeIsSetToInvalid() {
        assertEquals(BuildVersion.INVALID_VERSION_CODE, BuildVersion("4.56.1-df2354").code)
    }
}
