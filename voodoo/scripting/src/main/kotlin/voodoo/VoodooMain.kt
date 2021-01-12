package voodoo

/**
 * Created by nikky on 30/12/17.
 * @author Nikky
 */

import com.github.ajalt.clikt.core.subcommands
import kotlinx.coroutines.*
import kotlinx.coroutines.debug.DebugProbes
import mu.KotlinLogging
import voodoo.cli.EvalScriptCommand
import voodoo.cli.VoodooCommand

object VoodooMain {
    val logger = KotlinLogging.logger {}

    @JvmStatic
    fun main(vararg args: String) {

        @OptIn(ExperimentalCoroutinesApi::class)
        DebugProbes.install()

        VoodooCommand().apply {
            subcommands(EvalScriptCommand())
        }.main(args)
    }

//    @JvmStatic
//    fun oldMain(vararg fullArgs: String) {
////        val rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
////        rootLogger.level = Level.INFO // TODO: pass as -Dvoodoo.debug=true ?
//        System.setProperty(DEBUG_PROPERTY_NAME, DEBUG_PROPERTY_VALUE_ON)
//
//        Thread.sleep(1000) // wait for logger to catch up
//
//        logger.debug("using Voodoo: ${GeneratedConstants.FULL_VERSION}")
//        logger.debug("full arguments: ${fullArgs.joinToString(", ", "[", "]") { it }}")
////        logger.debug("system.properties:")
////        System.getProperties().forEach { k, v ->
////            logger.debug { "  $k = $v" }
////        }
//
//        if (fullArgs.isEmpty()) {
//            GradleSetup.main()
//            exitProcess(0)
//        }
//
//        val directories = Directories.get(moduleName = "script")
//        val cacheDir = directories.cacheHome
//
//        val arguments = fullArgs.drop(1)
//        logger.info { "arguments: ${arguments}" }
//        val script = fullArgs.getOrNull(0)?.apply {
//            require(isNotBlank()) { "'$this' configuration script name cannot be blank" }
//            require(endsWith(".voodoo.kts")) { "'$this' configuration script filename must end with .voodoo.kts" }
//        } ?: run {
//            logger.error("configuration script must be the first parameter")
//            exitProcess(1)
//        }
//        val scriptFile = File(script)
//        require(scriptFile.exists()) { "script file does not exists" }
//
//        val id = scriptFile.name.substringBeforeLast(".voodoo.kts").apply {
//            require(isNotBlank()) { "the script file must contain a id in the filename" }
//        }.toLowerCase()
//
//        logger.debug("id: $id")
//
//        if (!SharedFolders.RootDir.defaultInitialized) {
//            SharedFolders.RootDir.value = File(System.getProperty("user.dir")).absoluteFile
//        }
//        val rootDir = SharedFolders.RootDir.get().absoluteFile
//
//        val stopwatch = Stopwatch("main")
//
//        val reportName = stopwatch {
//            val host = "createJvmScriptingHost".watch {
//                createJvmScriptingHost(cacheDir)
//            }
//
//            val libs = rootDir.resolve("libs") // TODO: set from system property
//            val tomeDir = SharedFolders.TomeDir.get()
//            val docDir = SharedFolders.DocDir.get(id)
//
//            logger.info { "fullArgs: ${fullArgs.joinToString()}" }
//            logger.info { "arguments: ${arguments}" }
//            val funcs = mapOf<String, suspend Stopwatch.(Array<String>) -> Unit>(
//                VoodooTask.Build.key to { args ->
//                    // TODO: only compile in this step
//                    val scriptEnv = host.evalScript<MainScriptEnv>(
//                        stopwatch = "evalScript".watch,
//                        libs = libs,
//                        scriptFile = scriptFile,
//                        args = arrayOf()
//                    )
//
//                    val tomeEnv = initTome(
//                        stopwatch = "initTome".watch, libs = libs, host = host, tomeDir = tomeDir, docDir = docDir
//                    )
//                    logger.debug("tomeEnv: $tomeEnv")
//
////                    val nestedPack = scriptEnv.pack
//
//                    // debug
////                    val jsonObj = json.encodeToJsonElement(NestedPack.serializer(), scriptEnv.pack) as JsonObject
////                    rootDir.resolve(id).resolve("$id.nested.pack.json").writeText(
////                        json.encodeToString(JsonObject.serializer(),
////                            JsonObject(mapOf("\$schema" to JsonPrimitive(scriptEnv.pack.`$schema`)) + jsonObj))
////                    )
////                    val schemaFile = rootDir.resolve(id).resolve(scriptEnv.pack.`$schema`)
////                    schemaFile.absoluteFile.parentFile.mkdirs()
////                    schemaFile.writeText(
////                        json.encodeToSchema(NestedPack.serializer())
////                    )
//
//                    // load nestedPack
//                    val nestedPack = json.decodeFromString(NestedPack.serializer(),
//                        rootDir.resolve(id).resolve("$id.nested.pack.json").readText())
//
//
//                    // TODO: pass extra args object
//                    VoodooTask.Build.execute(
//                        stopwatch = "buildTask".watch,
//                        id = id,
//                        nestedPack = nestedPack,
//                        tomeEnv = tomeEnv,
//                        rootFolder = rootDir
//                    )
//                    logger.info("finished")
//                },
//                // TODO: git tag task
//                // TODO: make changelog tasks
//                VoodooTask.Changelog.key to { _ ->
//                    val changelogBuilder = initChangelogBuilder(
//                        stopwatch = "initChangelogBuilder".watch, libs = libs, id = id, tomeDir = tomeDir, host = host
//                    )
//                    val tomeEnv = initTome(
//                        stopwatch = "initTome".watch, libs = libs, host = host, tomeDir = tomeDir, docDir = docDir
//                    )
//
////                    VoodooTask.Changelog.execute(this, id, changelogBuilder, tomeEnv)
//                },
//                VoodooTask.Pack.key to { args ->
//                    // TODO: pass pack method
//                    val arguments = PackArguments(ArgParser(args))
//                    val packer = Pack.packMap[arguments.method.toLowerCase()] ?: run {
//                        Pack.logger.error("no such packing method: ${arguments.method}")
//                        exitProcess(-1)
//                    }
//                    VoodooTask.Pack.execute(this, id, packer)
//                },
//                VoodooTask.Test.key to { args ->
//                    // TODO: pass test method
//                    val arguments = voodoo.test.TestArguments(ArgParser(args))
//
//                    val testMethod = when (arguments.method) {
//                        "mmc" -> TestMethod.MultiMC(clean = arguments.clean)
//                        else -> error("no such method found for ${arguments.method}")
//                    }
//
//                    VoodooTask.Test.execute(this, id, testMethod)
//                },
//                VoodooTask.Version.key to { _ ->
//                    logger.info("voodoo version: " + GeneratedConstants.FULL_VERSION)
//                }
//            )
//
//            fun printCommands(cmd: String?) {
//                if (cmd == null) {
//                    logger.error("no command specified")
//                } else {
//                    logger.error("unknown command '$cmd'")
//                }
//                logger.warn("voodoo ${GeneratedConstants.FULL_VERSION}")
//                logger.warn("commands: ")
//                funcs.keys.forEach { key ->
//                    logger.warn("> $key")
//                }
//            }
//
//            val invocations = arguments.chunkBy(separator = "-")
//            invocations.forEach { argChunk ->
//                val command = argChunk.getOrNull(0) ?: run {
//                    printCommands(null)
//                    return
//                }
//                val remainingArgs = argChunk.drop(1).toTypedArray()
//                logger.info("executing command '$command' with args [${remainingArgs.joinToString()}]")
//
//                val function = funcs[command.toLowerCase()]
//                if (function == null) {
//                    printCommands(command)
//                    return
//                }
//
//                runBlocking(CoroutineName("main")) {
//                    "${command}Watch".watch {
//                        function(remainingArgs)
//                    }
//                }
//            }
//            invocations.joinToString("_") { it.joinToString("-") }
//        }
//        println(stopwatch.toStringPretty())
//        val reportDir = rootDir.resolve("reports").apply { mkdirs() }
//        stopwatch.saveAsSvg(reportDir.resolve("${id}_$reportName.report.svg"))
//        stopwatch.saveAsHtml(reportDir.resolve("${id}_$reportName.report.html"))
//        val traceEventsReport = stopwatch.asTraceEventsReport()
//        val jsonString = Json {
//            prettyPrint = true
//            encodeDefaults = true
//        }
//            .encodeToString(TraceEventsReport.serializer(), traceEventsReport)
//        reportDir.resolve("${id}_$reportName.report.json").writeText(jsonString)
//    }
//
//    private fun initChangelogBuilder(
//        stopwatch: Stopwatch,
//        libs: File,
//        id: String,
//        tomeDir: File,
//        host: BasicJvmScriptingHost,
//    ): ChangelogBuilder = stopwatch {
//        tomeDir.resolve("$id.changelog.kts")
//            .also { file -> logger.debug { "trying to load: $file" } }
//            .takeIf { it.exists() }?.let { idScript ->
//                return@stopwatch host.evalScript<ChangeScript>(
//                    "evalScript_file".watch,
//                    libs = libs,
//                    scriptFile = idScript
//                ).let { script ->
//                    script.getBuilderOrNull() ?: throw NotImplementedError("builder was not assigned in $idScript")
//                }
//            }
//        tomeDir.resolve("default.changelog.kts")
//            .also { file -> logger.debug { "trying to load: $file" } }
//            .takeIf { it.exists() }?.let { defaultScript ->
//                return@stopwatch host.evalScript<ChangeScript>(
//                    "evalScript_default".watch,
//                    libs = libs,
//                    scriptFile = defaultScript
//                ).let { script ->
//                    script.getBuilderOrNull() ?: throw NotImplementedError("builder was not assigned in $defaultScript")
//                }
//            }
//        logger.debug { "falling back to default changelog builder implementation" }
//        return@stopwatch ChangelogBuilder()
//    }
//
//    private fun initTome(
//        stopwatch: Stopwatch,
//        libs: File,
//        tomeDir: File,
//        docDir: File,
//        host: BasicJvmScriptingHost,
//    ): TomeEnv = stopwatch {
//        val tomeEnv = TomeEnv(docDir)
//
//        val tomeScripts = tomeDir.listFiles { file ->
//            logger.debug("tome testing: $file")
//            file.isFile && file.name.endsWith(".tome.kts")
//        }!!
//
//        tomeScripts.forEach { scriptFile ->
//            require(scriptFile.exists()) { "script file does not exists" }
//            val scriptFileName = scriptFile.name
//
//            val id = scriptFileName.substringBeforeLast(".tome.kts").apply {
//                require(isNotBlank()) { "the script file must contain a id in the filename" }
//            }
//
//            val tomeScriptEnv = host.evalScript<TomeScript>(
//                "evalScript_tome".watch,
//                libs = libs,
//                scriptFile = scriptFile,
//                args = *arrayOf(id)
//            )
//
//            val generator = tomeScriptEnv.getGeneratorOrNull()
//                ?: throw NotImplementedError("generator was not assigned in $scriptFile")
//
//            tomeEnv.add(tomeScriptEnv.filename, generator)
//        }
//
//        return@stopwatch tomeEnv
//    }
//
//    private fun Iterable<String>.chunkBy(separator: String = "-"): List<Array<String>> {
//        val result: MutableList<MutableList<String>> = mutableListOf(mutableListOf())
//        this.forEach {
//            if (it == separator)
//                result += mutableListOf<String>()
//            else
//                result.last() += it
//        }
//        return result.map { it.toTypedArray() }
//    }
}