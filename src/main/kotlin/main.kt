import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
import java.io.FileNotFoundException

fun getInstalledCliReleases(cliFiles: Array<File>): List<Float> {
    return cliFiles.map {
        it.name.removeSuffix(".jar")
            .replace("alpha", "0")
            .replace("beta", "1")
            .replace("rc", "2")
            .replace(".", "")
            .replace("-", ",")
            .toFloat()
    }
}

fun getCliVersion(): String {
    try {
        if (!File("icarodeps.json").isFile)
            throw FileNotFoundException()

        val dependencies = Gson().fromJson(File("icarodeps.json").readText(), Map::class.java)

        return dependencies["cliVersion"].toString()
    } catch (e: JsonSyntaxException) {
        println("the icarodeps.json file is malformed!")
    } catch (e: FileNotFoundException) {
        val cliDir = File("${System.getenv("HOME")}/icaro/cli")

        if (cliDir.listFiles().isNotEmpty()) {
            return getInstalledCliReleases(cliDir.listFiles()).maxOf { it }.toString()
        } else {
            //return last tag
        }
    }
}

fun main(args: Array<String>) {
    val cliPath = "${System.getenv("HOME")}/icaro/cli/${getCliVersion()}.jar"

    if (!File(cliPath).isFile) {

    }

    val cliProcess = ProcessBuilder(listOf("java", "-jar", cliPath) + args).start()

    val cliOutput = String(cliProcess.inputStream.readAllBytes()) + String(cliProcess.errorStream.readAllBytes())

    println(cliOutput)
}