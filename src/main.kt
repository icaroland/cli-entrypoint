import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
import java.io.FileNotFoundException
import kotlin.system.exitProcess

class SemVerComparator : Comparator<String> {
    private fun versionAsList(release: String): List<String> {
        if (!Regex("\\d.\\d(.\\d)?(-\\w*.\\d)?").matches(release)) throw IllegalArgumentException()

        var (major, minor, patch, preReleaseType, preReleaseNumber) = Regex("(\\d).(\\d).?(\\d)?-?(\\w*).?(\\d)?")
            .find(release)!!.destructured.toList()

        if (patch == "") patch = "0"
        if (preReleaseType == "") preReleaseType = "zeta"
        if (preReleaseNumber == "") preReleaseNumber = "0"

        return listOf(major, minor, patch, preReleaseType, preReleaseNumber)
    }

    override fun compare(o1: String, o2: String): Int {
        for ((currentNumberO1, currentNumberO2) in versionAsList(o1).zip(versionAsList(o2))) {
            if (currentNumberO1 < currentNumberO2) return -1

            if (currentNumberO1 > currentNumberO2) return 1
        }

        return 0
    }
}

fun lastVersionInstalled(): String {
    if (File("$ICARO_HOME/cli/core").listFiles()?.isEmpty() == true)
        throw FileNotFoundException()

    return File("$ICARO_HOME/cli/core").listFiles()!!.map { it.name }.maxWith(SemVerComparator())
}

fun cliVersionToUse(): String {
    if (!File("$DEPS_FILE_NAME.json").isFile) return lastVersionInstalled()

    val dependencies = Gson().fromJson(File("$DEPS_FILE_NAME.json").readText(), Map::class.java)

    return dependencies[CLI_VERSION_DEPS_ATTRIBUTE_NAME].toString()
}

fun main(args: Array<String>) {
    try {
        val cliPath = "$ICARO_HOME/cli/core/${cliVersionToUse()}.jar"

        val cliProcess = ProcessBuilder(listOf("java", "-jar", cliPath) + args).start()

        val cliOutput = String(cliProcess.inputStream.readAllBytes()) + String(cliProcess.errorStream.readAllBytes())

        println(cliOutput)
    } catch (e: JsonSyntaxException) {
        println("deps.json file doesn't contain valid JSON!")
    } catch (e: IllegalArgumentException) {
        println("at least one of your cli version installed is corrupted. (Re)Install Icaro!")
    } catch (e: FileNotFoundException) {
        println("no cli versions are installed!")
    } catch (e: Throwable) {
        exitProcess(1)
    }
}