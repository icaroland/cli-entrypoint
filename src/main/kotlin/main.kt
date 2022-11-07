import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess

class SemVerComparator : Comparator<String> {
    private fun versionAsList(release: String): List<String> {
        var (major, minor, patch, preReleaseType, preReleaseNumber) = "(\\d).(\\d).?(\\d)?-?(\\w*).?(\\d)?".toRegex()
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

fun lastVersionInstalled(): String =
    File("$ICARO_HOME/cli/core").listFiles()!!.map { it.name }.maxWith(SemVerComparator()) 

fun cliVersion(): String {
    if (!File("deps.json").isFile) return lastVersionInstalled()

    val dependencies = Gson().fromJson(File("deps.json").readText(), Map::class.java)

    return dependencies["cliVersion"].toString()
}

fun main(args: Array<String>) {
    try {
        val cliPath = "$ICARO_HOME/cli/core/${cliVersion()}.jar"

        val cliProcess = ProcessBuilder(listOf("java", "-jar", cliPath) + args).start()

        val cliOutput = String(cliProcess.inputStream.readAllBytes()) + String(cliProcess.errorStream.readAllBytes())

        println(cliOutput)
    } catch (e: JsonSyntaxException) {
        println("deps.json file doesn't contain valid JSON!")
        exitProcess(1)
    }
}