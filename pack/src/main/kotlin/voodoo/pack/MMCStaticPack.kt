package voodoo.pack

import com.eyeem.watchadoin.Stopwatch
import voodoo.data.lock.LockPack
import voodoo.mmc.MMCUtil
import voodoo.util.blankOr
import voodoo.util.maven.MavenUtil
import voodoo.util.packToZip
import voodoo.util.unixPath
import java.io.File
import java.net.URI
import kotlin.system.exitProcess

object MMCStaticPack : AbstractPack() {
    override val label = "MultiMC Static Pack"

    override fun File.getOutputFolder(id: String): File = resolve("mulltimc-sk-static")

    override suspend fun pack(
        stopwatch: Stopwatch,
        modpack: LockPack,
        output: File,
        uploadBaseDir: File,
        clean: Boolean
    ) = stopwatch {
        val cacheDir = directories.cacheHome.resolve("mmc")
        val instanceDir = cacheDir.resolve(modpack.id)
        instanceDir.deleteRecursively()

        val preLaunchCommand =
            "\"\$INST_JAVA\" -jar \"\$INST_DIR/mmc-installer.jar\" --id \"\$INST_ID\" --inst \"\$INST_DIR\" --mc \"\$INST_MC_DIR\""
        val minecraftDir = MMCUtil.installEmptyPack(
            modpack.title.blankOr,
            modpack.id,
            icon = modpack.iconFile,
            instanceDir = instanceDir,
            preLaunchCommand = preLaunchCommand
        )

        logger.info("tmp dir: $instanceDir")

        val skPackUrl = modpack.packOptions.multimcOptions.skPackUrl
            ?: run {
                modpack.packOptions.baseUrl?.let { baseUrl ->
                    val skOutput = with(SKPack) { uploadBaseDir.getOutputFolder(modpack.id) }
                    val skPackFile = skOutput.resolve("${modpack.id}.json")
                    val relativePath = skPackFile.relativeTo(uploadBaseDir).unixPath
                    URI(baseUrl).resolve(relativePath).toASCIIString()
                }
            }
        if (skPackUrl == null) {
            MMCSelfupdatingPack.logger.error("skPackUrl in multimc options is not set")
            exitProcess(3)
        }
        val urlFile = instanceDir.resolve("voodoo.url.txt")
        urlFile.writeText(skPackUrl)

        val multimcInstaller = instanceDir.resolve("mmc-installer.jar")
        val installer = MavenUtil.downloadArtifact(
            "downloadArtifact".watch,
            mavenUrl = PackConstants.MAVEN_URL,
            group = PackConstants.MAVEN_GROUP,
            artifactId = "multimc-installer",
            version = PackConstants.FULL_VERSION,
            classifier = PackConstants.MAVEN_SHADOW_CLASSIFIER,
            outputDir = directories.cacheHome
        )
        installer.copyTo(multimcInstaller)

        val packignore = instanceDir.resolve(".packignore")
        packignore.writeText(
            """.minecraft
                  |mmc-pack.json
                """.trimMargin()
        )

        output.mkdirs()
        val instanceZip = output.resolve(modpack.id + ".zip")

        instanceZip.delete()
        packToZip(instanceDir.toPath(), instanceZip.toPath())
        logger.info("created mmc pack $instanceZip")
    }
}