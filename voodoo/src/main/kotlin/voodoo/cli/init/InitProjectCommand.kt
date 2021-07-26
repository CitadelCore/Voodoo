package voodoo.cli.init

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.subcommands
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.KotlinLogging
import mu.withLoggingContext
import voodoo.cli.CLIContext
import voodoo.util.maven.MavenUtil
import voodoo.voodoo.GeneratedConstants
import java.io.StringWriter
import java.util.*

class InitProjectCommand : CliktCommand(
    name = "project",
    help = "intializes project in the working directory",
    invokeWithoutSubcommand = true
) {
    private val logger = KotlinLogging.logger {}
    val cliContext by requireObject<CLIContext>()

    override fun run(): Unit = withLoggingContext("command" to commandName) {
        val rootDir = cliContext.rootDir

        runBlocking(MDCContext()) {
            // download wrapper -> wrapper/wrapper.jar
            val wrapperFile = rootDir.resolve("wrapper/new.jar")
            wrapperFile.absoluteFile.parentFile.mkdirs()
            wrapperFile.delete()

            MavenUtil.downloadArtifact(
                GeneratedConstants.MAVEN_URL,
                GeneratedConstants.MAVEN_GROUP,
                "wrapper",
                GeneratedConstants.FULL_VERSION,
                outputFile = wrapperFile,
                outputDir = wrapperFile.absoluteFile.parentFile,
                classifier = GeneratedConstants.MAVEN_SHADOW_CLASSIFIER
            )

            // create wrapper.properties
            val propertiesFile = rootDir.resolve("wrapper/wrapper.properties")
            propertiesFile.absoluteFile.parentFile.mkdirs()
            val properties = Properties().also { prop ->
                val groupPath = GeneratedConstants.MAVEN_GROUP.replace('.','/')
                val version = if(GeneratedConstants.FULL_VERSION.endsWith("-local")) {
                    MavenUtil.getReleaseVersionFromMavenMetadata(GeneratedConstants.MAVEN_URL, GeneratedConstants.MAVEN_GROUP, "voodoo")
                } else GeneratedConstants.FULL_VERSION
                prop["distributionUrl"] = "${GeneratedConstants.MAVEN_URL}/$groupPath/voodoo/$version/voodoo-$version-${GeneratedConstants.MAVEN_SHADOW_CLASSIFIER}.jar"
                prop["distributionPath"] = "wrapper/bin"
            }

            StringWriter().use {
                properties.store(it, "generated by: voodoo generateWrapper")
                propertiesFile.writeText(it.toString())
            }

            // generate shellscripts

            rootDir.resolve("voodoo.bat").let { batFile ->
                if(batFile.exists()) {
                    logger.error { "$batFile already exists, not overwriting" }
                    return@let
                }

                batFile.writeText("""
                    java -jar wrapper\wrapper.jar %*
                    IF EXIST wrapper\new.jar MOVE /Y wrapper\new.jar wrapper\wrapper.jar
                """.trimIndent())
                logger.info { "generated $batFile" }
            }

            rootDir.resolve("voodoo").let { bashFile ->
                if(bashFile.exists()) {
                    logger.error { "$bashFile already exists, not overwriting" }
                    return@let
                }

                bashFile.writeText("""
                    #!/usr/bin/env bash

                    DIR="${'$'}( cd "${'$'}( dirname "${'$'}{BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
                    java -jar "${'$'}DIR/wrapper/wrapper.jar" ${'$'}@
                    
                    if test -f "${'$'}DIR/wrapper/new.jar"; then
                        mv -f "${'$'}DIR/wrapper/new.jar" "${'$'}DIR/wrapper/wrapper.jar"
                    fi
                """.trimIndent())
                bashFile.setExecutable(true)
                logger.info { "generated $bashFile" }
            }

            rootDir.resolve("completion.bash").let { shellFile ->
                if(shellFile.exists()) {
                    logger.error { "$shellFile already exists, not overwriting" }
                    return@let
                }

                shellFile.writeText("""
                    #!/usr/bin/env bash
                    
                    source <(./voodoo --generate-completion=bash)
                """.trimIndent())
                shellFile.setExecutable(true)
                logger.info { "generated $shellFile" }
            }
            rootDir.resolve("completion.zsh").let { shellFile ->
                if(shellFile.exists()) {
                    logger.error { "$shellFile already exists, not overwriting" }
                    return@let
                }

                shellFile.writeText("""
                    #!/usr/bin/env zsh
                    
                    source <(./voodoo --generate-completion=zsh)
                """.trimIndent())
                shellFile.setExecutable(true)
                logger.info { "generated $shellFile" }
            }
            rootDir.resolve("completion.fish").let { shellFile ->
                if(shellFile.exists()) {
                    logger.error { "$shellFile already exists, not overwriting" }
                    return@let
                }

                shellFile.writeText("""
                    #!/usr/bin/env fish
                    
                    ./voodoo --generate-completion=fish | source
                """.trimIndent())
                shellFile.setExecutable(true)
                logger.info { "generated $shellFile" }
            }

            //TODO: move to setup command
            rootDir.resolve(".gitignore").let { gitignoreFile ->
                if(gitignoreFile.exists()) {
                    logger.error { "$gitignoreFile already exists, not overwriting" }
                    return@let
                }

                gitignoreFile.writeText("""
                    /.completions/
                    /_upload/
                    /reports/
                    /logs/
                    /docs/
                    /schema/
                    /wrapper/bin/
                """.trimIndent())
                logger.info { "generated $gitignoreFile" }
            }
        }
    }
}