const val DEPS_FILE_NAME = "deps"

val CLI_CORE_DIR_PATH = "${System.getenv("ICARO_HOME")}/cli/core"

const val CLI_VERSION_DEPS_ATTRIBUTE_NAME = "cliVersion"

const val CORRUPTED_VERSION_MSG =
    "at least one of your cli version installed is corrupted. (Re)Install Icaro or contact the maintainers"

val versionFormatRegex = Regex("\\d.\\d(.[1-9])?(-\\w*.[1-9])?")

@Throws(IllegalArgumentException::class)
fun versionAsList(release: String): List<String> {
    if (!versionFormatRegex.matches(release))
        throw IllegalArgumentException(CORRUPTED_VERSION_MSG)

    var (major, minor, patch, preReleaseType, preReleaseNumber) = Regex("(\\d).(\\d).?([1-9])?-?(\\w*).?([1-9])?")
        .find(release)!!.destructured.toList()

    if (preReleaseType !in listOf("alpha", "beta", "rc", ""))
        throw IllegalArgumentException(CORRUPTED_VERSION_MSG)

    if (patch == "") patch = "0"
    if (preReleaseType == "") preReleaseType = "z"
    if (preReleaseNumber == "") preReleaseNumber = "0"

    return listOf(major, minor, patch, preReleaseType, preReleaseNumber)
}

class SemVerComparator : Comparator<String> {
    @Throws(IllegalArgumentException::class)
    override fun compare(o1: String, o2: String): Int {
        for ((currentNumberO1, currentNumberO2) in versionAsList(o1).zip(versionAsList(o2))) {
            if (currentNumberO1 < currentNumberO2) return -1

            if (currentNumberO1 > currentNumberO2) return 1
        }

        return 0
    }
}