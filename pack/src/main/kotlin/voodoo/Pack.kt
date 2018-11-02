package voodoo

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import mu.KLogging
import voodoo.data.lock.LockPack
import voodoo.pack.CursePack
import voodoo.pack.MMCFatPack
import voodoo.pack.MMCPack
import voodoo.pack.MMCStaticPack
import voodoo.pack.SKPack
import voodoo.pack.ServerPack
import voodoo.util.json
import java.io.File
import kotlin.system.exitProcess

/**
 * Created by nikky on 28/03/18.
 * @author Nikky
 */

object Pack : KLogging() {
    private val packMap = mapOf(
        "sk" to SKPack,
        "mmc" to MMCPack,
        "mmc-static" to MMCStaticPack,
        "mmc-fat" to MMCFatPack,
        "server" to ServerPack,
        "curse" to CursePack
    )

    suspend fun main(vararg args: String) {
        val arguments = Arguments(ArgParser(args))

        arguments.run {
            logger.info("loading $modpackLockFile")
            val modpack: LockPack = json.parse(LockPack.serializer(), modpackLockFile.readText())
            val rootFolder = modpackLockFile.absoluteFile.parentFile
            modpack.loadEntries(rootFolder)

            val packer = packMap[methode.toLowerCase()] ?: run {
                logger.error("no such packing methode: $methode")
                exitProcess(-1)
            }

            packer.pack(
                modpack = modpack,
                target = targetFolder,
                clean = true
            )
            logger.info("finished packaging")
        }
    }

    suspend fun pack(modpack: LockPack, vararg args: String) {
        logger.info("parsing arguments")
        val arguments = ArgumentsForDSL(ArgParser(args))

        arguments.run {
            logger.info("loading entries")
            modpack.loadEntries()

            val packer = packMap[methode.toLowerCase()] ?: run {
                logger.error("no such packing methode: $methode")
                exitProcess(-1)
            }

            packer.pack(
                modpack = modpack,
                target = target,
                clean = true
            )
            logger.info("finished packaging")
        }
    }

    private class Arguments(parser: ArgParser) {
        val methode by parser.positional(
            "METHODE",
            help = "format to package into"
        ) { this.toLowerCase() }
            .default("")

        val modpackLockFile by parser.positional(
            "FILE",
            help = "input pack .lock.hjson"
        ) { File(this) }

        val targetFolder by parser.storing(
            "--output", "-o",
            help = "output rootFolder"
        ).default<String?>(null)
    }

    private class ArgumentsForDSL(parser: ArgParser) {
        val methode by parser.positional(
            "METHODE",
            help = "format to package into"
        ) { this.toLowerCase() }
            .default("")
        val target by parser.storing(
            "--output", "-o",
            help = "output rootFolder"
        ).default<String?>(null)
    }
}