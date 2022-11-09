import com.google.gson.GsonBuilder
import org.junit.jupiter.api.Test
import java.io.File

class CliEntrypointTest {
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

        File("$DEPS_FILE_NAME.json").delete()
    }
}