package voodoo.cli

import com.eyeem.watchadoin.Stopwatch
import com.eyeem.watchadoin.saveAsHtml
import com.eyeem.watchadoin.saveAsSvg
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import kotlinx.coroutines.*
import kotlinx.coroutines.slf4j.MDCContext
import mu.KotlinLogging
import mu.withLoggingContext
import voodoo.Pack
import voodoo.data.lock.LockPack
import voodoo.util.SharedFolders
import voodoo.util.VersionComparator
import java.io.File

class PackageCommand(): CliktCommand(
    name = "package",
//    help = ""
) {
    private val logger = KotlinLogging.logger {}
    val cliContext by requireObject<CLIContext>()

    val id by option(
        "--id",
        help = "pack id"
    ).required()
        .validate {
            require(it.isNotBlank()) { "id must not be blank" }
            require(it.matches("""[\w_]+""".toRegex())) { "modpack id must not contain special characters" }
        }

    val packTargets by option(
        "--target", "-t"
    ).choice(Pack.packMap)
        .multiple()


    val uploadDirOption by option("--uploadDir")
        .file(canBeFile = false, canBeDir = true, canBeSymlink = false, mustBeWritable = true)

    override fun run() = withLoggingContext("command" to commandName) {
        runBlocking(MDCContext()) {
            val stopwatch = Stopwatch(commandName)

            val rootDir = cliContext.rootDir
            val baseDir = rootDir.resolve(id)

            stopwatch {

                val uploadDir = uploadDirOption ?: SharedFolders.UploadDir.get(id)

                packTargets.toSet().forEach { packTarget ->
                    withLoggingContext("pack" to packTarget.id) {
                        withContext(MDCContext()) {
                            val lockPacks = LockPack.parseAll(baseFolder = baseDir)
                                .sortedWith(compareBy(VersionComparator, LockPack::version))

                            coroutineScope {
                                lockPacks.forEach { lockpack ->
                                    withLoggingContext("version" to lockpack.version) {
                                        launch(MDCContext() + CoroutineName("package-version-${lockpack.version}")) {
                                            // TODO: pass pack method (enum / object)
                                            Pack.pack("pack-${packTarget.id}".watch, lockpack, uploadDir, packTarget)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val reportDir = File("reports").apply { mkdirs() }
            stopwatch.saveAsSvg(reportDir.resolve("${id}_build.report.svg"))
            stopwatch.saveAsHtml(reportDir.resolve("${id}_build.report.html"))
        }
    }
}