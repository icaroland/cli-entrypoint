import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
import java.net.http.HttpRequest
import kotlin.system.exitProcess

// TODO: test the comparator
class SemVerComparator : Comparator<String> {
    override fun compare(first: String?, second: String?): Int {
        
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

fun downloadVersionIfPossible(cliVersion: String) {
    try {
        // if version exists, download the given version online and use it
    } catch (e: Throwable) {
        println("the cliVersion doesn't exist!")
        exitProcess(1)
    }

}

fun getCliVersion(): String {
    try {
        if (!File("deps.json").isFile)
            return getLastVersionInstalledOrDownloadLastVersion()

        val dependencies = Gson().fromJson(File("deps.json").readText(), Map::class.java)

        val cliVersion = dependencies["cliVersion"].toString()

        if (!File("${System.getenv("HOME")}/icaro/cli/$cliVersion").isFile)
            downloadVersionIfPossible(cliVersion)

        return cliVersion
    } catch (e: JsonSyntaxException) {
        println("deps.json (the icaro dependencies file) is malformed!")
        exitProcess(1)
    }
}

fun main(args: Array<String>) {
    val cliPath = "${System.getenv("HOME")}/icaro/cli/${getCliVersion()}.jar"

    val cliProcess = ProcessBuilder(listOf("java", "-jar", cliPath) + args).start()

    val cliOutput = String(cliProcess.inputStream.readAllBytes()) + String(cliProcess.errorStream.readAllBytes())

    println(cliOutput)
}