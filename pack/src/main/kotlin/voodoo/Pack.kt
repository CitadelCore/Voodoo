package voodoo

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.serialization.json.JSON
import mu.KLogging
import voodoo.data.lock.LockPack
import voodoo.pack.*
import voodoo.util.ExceptionHelper
import java.io.File
import kotlin.system.exitProcess

/**
 * Created by nikky on 28/03/18.
 * @author Nikky
 */

object Pack : KLogging() {
    @JvmStatic
    fun main(vararg args: String) = mainBody {
        val arguments = Arguments(ArgParser(args))

        runBlocking(ExceptionHelper.context) {
            arguments.run {
                logger.info("loading $modpackLockFile")
                val modpack: LockPack = JSON.unquoted.parse(modpackLockFile.readText())
                val rootFolder = modpackLockFile.absoluteFile.parentFile
                modpack.loadEntries(rootFolder)

                val packer = when (methode) {
                    "sk" -> SKPack
                    "mmc" -> MMCPack
                    "mmc-static" -> MMCStaticPack
                    "mmc-fat" -> MMCFatPack
                    "server" -> ServerPack
                    "curse" -> CursePack

                    else -> {
                        logger.error("no such packing methode: $methode")
                        exitProcess(-1)
                    }
                }

                packer.download(coroutineScope = this@runBlocking, modpack = modpack, target = targetArg, clean = true)
            }
        }
    }

    private class Arguments(parser: ArgParser) {
        val methode by parser.positional("METHODE",
                help = "format to package into") { this.toLowerCase() }
                .default("")

        val modpackLockFile by parser.positional("FILE",
                help = "input pack .lock.json") { File(this) }

        val targetArg by parser.storing("--output", "-o",
                help = "output rootFolder")
                .default<String?>(null)

//        val clean by parser.flagging("--clean", "-c",
//                help = "clean output rootFolder before packaging")
//                .default(true)
    }
}