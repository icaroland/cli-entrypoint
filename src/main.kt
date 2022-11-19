import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
import java.io.FileNotFoundException

fun main(args: Array<String>) {
    try {
        val cliPath = "$CLI_CORE_DIR_PATH/${cliVersionToUse()}.jar"

        ProcessBuilder(listOf("java", "-jar", cliPath) + args).inheritIO().start()
    } catch (e: Throwable) {
        println(e.message)
    }
}

@Throws(JsonSyntaxException::class)
fun cliVersionToUse(): String {
    if (!File("$DEPS_FILE_NAME.json").isFile) return lastVersionInstalled()

    try {
        val dependencies = Gson().fromJson(File("$DEPS_FILE_NAME.json").readText(), Map::class.java)

        return dependencies[CLI_VERSION_DEPS_ATTRIBUTE_NAME].toString()
    } catch (e: Throwable) {
        throw JsonSyntaxException("deps.json file doesn't contain valid JSON!")
    }
}

@Throws(FileNotFoundException::class, IllegalArgumentException::class)
fun lastVersionInstalled(): String {
    if (File(CLI_CORE_DIR_PATH).listFiles()?.isEmpty() != false)
        throw FileNotFoundException("no cli versions are installed!")

    val cliVersions: List<String> = File(CLI_CORE_DIR_PATH).listFiles()!!.map { it.name.removeSuffix(".jar") }

    if (cliVersions.size == 1 && !versionFormatRegex.matches(cliVersions[0]))
        throw IllegalArgumentException(CORRUPTED_VERSION_MSG)

    return cliVersions.maxWith(SemVerComparator())
}