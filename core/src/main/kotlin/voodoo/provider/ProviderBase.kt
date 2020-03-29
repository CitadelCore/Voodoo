package voodoo.provider

import com.eyeem.watchadoin.Stopwatch
import kotlinx.coroutines.channels.SendChannel
import mu.KLogging
import voodoo.data.DependencyType
import voodoo.data.flat.Entry
import voodoo.data.lock.LockEntry
import java.io.File
import java.time.Instant

/**
 * Created by nikky on 04/01/18.
 * @author Nikky
 */

abstract class ProviderBase(
    open val name: String
) {
    val id: String
        get() = Providers.getId(this)!!

    override fun toString() = "name: $name, id: $id"

    open fun reset() {}

    open suspend fun resolve(entry: Entry, mcVersion: String, addEntry: SendChannel<Pair<Entry, String>>): LockEntry {
        logger.info("[$name] resolve ${entry.id}")
        throw NotImplementedError("unable to resolve")
    }

    companion object : KLogging()

    /**
     * downloads a entry
     *
     * @param entry the entry oyu are working on
     * @param targetFolder provided target rootFolder/location
     * @param cacheDir prepared cache directory
     */
    abstract suspend fun download(
        stopwatch: Stopwatch,
        entry: LockEntry,
        targetFolder: File,
        cacheDir: File
    ): Pair<String?, File>

    abstract suspend fun generateName(entry: LockEntry): String

    open suspend fun getAuthors(entry: LockEntry): List<String> {
        return emptyList()
    }

    open suspend fun getProjectPage(entry: LockEntry): String {
        return ""
    }

    open suspend fun getVersion(entry: LockEntry): String {
        return ""
    }

    open suspend fun getLicense(entry: LockEntry): String {
        return ""
    }

    open suspend fun getThumbnail(entry: LockEntry): String {
        return ""
    }

    open suspend fun getThumbnail(entry: Entry): String {
        return ""
    }

    open suspend fun getReleaseDate(entry: LockEntry): Instant? {
        return null
    }

    open fun reportData(entry: LockEntry): MutableList<Pair<String, String>> {
        return mutableListOf(
            Pair("ID", "${entry.id}")
        ).also { list ->
            list += Pair("Version", "${entry.version()}")
            list += Pair("Provider", "${entry.provider}")
            entry.fileName?.let { fileName ->
                list += Pair("Filename", "$fileName")
            }
            list += Pair("Side", "${entry.side}")
            entry.description?.let {
                list += Pair("Description", "$it")
            }
            list += Pair("Optional", "${entry.optional}")
            entry.dependencies.takeIf { it.isNotEmpty() }?.let { dependencies ->

                dependencies[DependencyType.REQUIRED]?.takeIf { it.isNotEmpty() }?.let { required ->
                    list += Pair("Required Dependencies", required.joinToString(", "))
                }
                dependencies[DependencyType.OPTIONAL]?.takeIf { it.isNotEmpty() }?.let { required ->
                    list += Pair("Optional Dependencies", required.joinToString(", "))
                }
//                dependencies[DependencyType.EmbeddedLibrary]?.takeIf { it.isNotEmpty() }?.let { required ->
//                    list += Triple("dependencies_embedded", "Embedded Dependencies", required.joinToString(", ", "", ""))
//                }
//                dependencies[DependencyType.Include]?.takeIf { it.isNotEmpty() }?.let { required ->
//                    list += Triple("dependencies_include", "Include Dependencies", required.joinToString(", ", "", ""))
//                }
//                dependencies[DependencyType.Tool]?.takeIf { it.isNotEmpty() }?.let { required ->
//                    list += Triple("dependencies_tool", "Tool Dependencies", required.joinToString(", ", "", ""))
//                }
            }
        }
    }

    open fun validate(lockEntry: LockEntry): Boolean {
        if (lockEntry.id.isEmpty()) {
            logger.error("invalid id of $lockEntry")
            return false
        }
        return true
    }
}