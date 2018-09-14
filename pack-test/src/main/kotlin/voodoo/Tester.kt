package voodoo

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.serialization.json.JSON
import mu.KLogging
import voodoo.data.lock.LockPack
import voodoo.tester.MultiMCTester
import voodoo.util.ExceptionHelper
import java.io.File
import kotlin.system.exitProcess

/**
 * Created by nikky on 28/03/18.
 * @author Nikky
 */

object Tester : KLogging() {
    @JvmStatic
    fun main(vararg args: String) = mainBody {
        val arguments = Arguments(ArgParser(args))

        runBlocking(context = ExceptionHelper.context){
            arguments.run {

                logger.info("loading $modpackLockFile")
                val modpack: LockPack = JSON.unquoted.parse(modpackLockFile.readText())
                val rootFolder = modpackLockFile.absoluteFile.parentFile
                modpack.loadEntries(rootFolder)

                val tester = when (methode) {
                    "mmc" -> MultiMCTester

                    else -> {
                        logger.error("no such packing methode: $methode")
                        exitProcess(-1)
                    }
                }

                tester.execute(modpack = modpack, clean = clean)
            }
        }

    }

    private class Arguments(parser: ArgParser) {
        val methode by parser.positional(
            "METHODE",
            help = "testing provider to use"
        ) { this.toLowerCase() }
            .default("")

        val modpackLockFile by parser.positional(
            "FILE",
            help = "input pack .lock.json"
        ) { File(this) }

        val clean by parser.flagging(
            "--clean", "-c",
            help = "clean output rootFolder before packaging"
        )
            .default(true)
    }
}