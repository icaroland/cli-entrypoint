import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals

class VersionAsListTest {
    @ParameterizedTest
    @ValueSource(strings = ["1", "0.1.1.0", "0.1.", "0.1.0-1.alpha", "0.1.0-alpha.1.1", "0.1.0-RC.1.1", "0.1.0-beta.0", "0.1.0"])
    fun shouldThrowIfVersionIsNotIcaroCompliant(version: String) {
        assertThrows<IllegalArgumentException> { versionAsList(version) }
    }

    @Test
    fun shouldReturnTheVersionAsListIfVersionIsIcaroCompliant() {
        assertEquals(
            listOf(
                "0.1", "0.1.1", "0.1-alpha.1", "0.1.2-alpha.1", "0.1.2-beta.1", "0.1.2-rc.1"
            ).map { versionAsList(it) }, listOf(
                listOf("0", "1", "0", "z", "0"),
                listOf("0", "1", "1", "z", "0"),
                listOf("0", "1", "0", "alpha", "1"),
                listOf("0", "1", "2", "alpha", "1"),
                listOf("0", "1", "2", "beta", "1"),
                listOf("0", "1", "2", "rc", "1"),
            )
        )
    }
}

class SemVerComparatorTest {
    @Test
    fun shouldReturnTheLastVersionBetweenTheTwo() {
        val comparator = SemVerComparator()

        listOf(
            listOf("0.1.1", "0.1", 1),
            listOf("0.1", "0.1", 0),
            listOf("0.2", "0.1", 1),
            listOf("0.1", "0.2", -1),
            listOf("0.1", "0.1-alpha.1", 1),
            listOf("0.1", "0.1-beta.1", 1),
            listOf("0.1", "0.1-rc.1", 1),
            listOf("0.1.1", "0.1.2", -1),
            listOf("0.1", "0.2-alpha.1", -1),
            listOf("0.2-alpha.1", "0.2-beta.1", -1),
            listOf("0.2-beta.1", "0.2-rc.1", -1),
            listOf("0.2-alpha.1", "0.2-alpha.2", -1),
            listOf("0.2-rc.1", "0.2-rc.2", -1),
            listOf("0.2-beta.1", "0.2.1-beta.1", -1),
            listOf("0.2", "0.2.1-alpha.1", -1),
            listOf("0.1.3", "0.1.2-rc.4", 1),
        ).forEach { assertEquals(it[2], comparator.compare(it[0] as String, it[1] as String)) }
    }
}