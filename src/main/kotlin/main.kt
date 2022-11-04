import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
import kotlin.system.exitProcess

class SemVerComparator : Comparator<String> {
    private fun getVersionAsList(release: String): List<String> {
        var (major, minor, patch, preReleaseType, preReleaseNumber) = "(\\d).(\\d).?(\\d)?-?(\\w*).?(\\d)?".toRegex()
            .find(release)!!
            .destructured.toList()

        if (patch == "") patch = "0"
        if (preReleaseType == "") preReleaseType = "zeta"
        if (preReleaseNumber == "") preReleaseNumber = "0"

        return listOf(major, minor, patch, preReleaseType, preReleaseNumber)
    }

    override fun compare(o1: String, o2: String): Int {
        for ((versionPiece1, versionPiece2) in getVersionAsList(o1).zip(getVersionAsList(o2))) {
            if (versionPiece1 < versionPiece2)
                return -1

            if (versionPiece1 > versionPiece2)
                return 1
        }
        
        return 0
    }
}

fun getLastVersionInstalledOrDownloadLastVersion(): String {
    val cliDir = File("${System.getenv("HOME")}/icaro/cli")

    if (cliDir.listFiles().isNotEmpty()) {
        return cliDir.listFiles().map { it.name }.maxWith(SemVerComparator())
    } else {
        // get the last version
        val lastVersion = ""

        // download the last version

        return lastVersion
    }
}

fun downloadAndGetVersionIfPossible(cliVersion: String): String {
    try {
        // if version exists, download the given version online and use it
    } catch (e: Throwable) {
        println("the cliVersion doesn't exist!")
        throw e
    }

}

fun getCliVersion(): String {
    try {
        if (!File("deps.json").isFile) return getLastVersionInstalledOrDownloadLastVersion()

        val dependencies = Gson().fromJson(File("deps.json").readText(), Map::class.java)

        val cliVersion = dependencies["cliVersion"].toString()

        if (!File("${System.getenv("HOME")}/icaro/cli/$cliVersion").isFile) return downloadAndGetVersionIfPossible(
            cliVersion
        )

        return cliVersion
    } catch (e: JsonSyntaxException) {
        println("deps.json (the icaro dependencies file) is malformed!")
        throw e
    }
}

fun main(args: Array<String>) {
    try {
        val cliPath = "${System.getenv("HOME")}/icaro/cli/${getCliVersion()}.jar"

        val cliProcess = ProcessBuilder(listOf("java", "-jar", cliPath) + args).start()

        val cliOutput = String(cliProcess.inputStream.readAllBytes()) + String(cliProcess.errorStream.readAllBytes())

        println(cliOutput)
    } catch (e: Throwable) {
        exitProcess(1)
    }
}