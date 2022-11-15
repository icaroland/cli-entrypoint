import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.FileNotFoundException

class CliVersionToUseTest {
    @AfterEach
    fun deleteDepsFile() {
        File("$DEPS_FILE_NAME.json").delete()
    }

    @Test
    fun shouldReturnTheCliVersionWrittenOnDependenciesFile() {
        val cliVersion = "0.1"

        val depsInJson = GsonBuilder().setPrettyPrinting().create().toJson(
            mapOf(
                CLI_VERSION_DEPS_ATTRIBUTE_NAME to cliVersion,
            )
        )

        File("$DEPS_FILE_NAME.json").writeText(depsInJson)

        assert(cliVersionToUse() == cliVersion)
    }

    @Test
    fun shouldThrowBecauseBadJsonWasWritten() {
        File("$DEPS_FILE_NAME.json").writeText("")

        assertThrows<JsonSyntaxException> { cliVersionToUse() }
    }
}

class LastVersionInstalledTest {
    @AfterEach
    fun deleteFiles() {
        File(CLI_CORE_DIR_PATH).deleteRecursively()
    }

    @Test
    fun shouldThrowBecauseNoCliWereFoundInstalled() {
        assertThrows<FileNotFoundException> { lastVersionInstalled() }
    }

    @Test
    fun shouldThrowBecauseBadCliReleaseWereFoundInstalled() {
        File(CLI_CORE_DIR_PATH).mkdirs()
        File("$CLI_CORE_DIR_PATH/0.1.1.0.jar").createNewFile()

        assertThrows<IllegalArgumentException> { lastVersionInstalled() }
    }
}