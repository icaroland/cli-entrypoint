import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterEach
import java.io.File

open class AutoRemover {
    @AfterEach
    fun deleteGeneratedContent() {
        FileUtils.cleanDirectory(File("."))
    }
}