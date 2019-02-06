package voodoo.changelog

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.internal.LinkedHashMapSerializer
import kotlinx.serialization.serializer
import mu.KLogging
import voodoo.data.lock.LockEntry
import voodoo.data.lock.LockPack
import voodoo.data.meta.MetaInfo
import voodoo.forge.ForgeUtil
import voodoo.markdownTable
import voodoo.provider.Providers
import voodoo.util.blankOr
import voodoo.util.json
import java.io.File

val LockPack.forgeVersion: String
    get() = runBlocking {
        ForgeUtil.forgeVersionOf(forge)?.forgeVersion ?: "missing"
    }
val LockPack.authorsString: String
    get() = authors.joinToString(", ")
val LockPack.iconHtml: String
    get() = "<img src=\"${icon.relativeTo(rootDir).path}\" alt=\"icon\" style=\"max-height: 128px;\"/>"

data class PackDiff(
    val newPack: LockPack,
    val oldPack: LockPack?,
    val newEntries: Map<String, LockEntry>,
    val oldEntries: Map<String, LockEntry>?,
    val newRootDir: File,
    val oldRootDir: File
) : KLogging() {
    fun writeChangelog(newMeta: File, oldMeta: File, docDir: File, generator: ChangelogBuilder) {
        val newPackMetaInfo = writePackMetaInformation(newMeta, newPack)
        val oldPackMetaInfo = readPackMetaInformation(oldMeta)

        val newEntryMetaInfo = writeEntryMetaInformation(newMeta, newPack)
        val oldEntryMetaInfo = readEntryMetaInformation(oldMeta)

        docDir.mkdirs()
        val changelogFile = newMeta.resolve(Companion.Filename.changelog)

        val currentChangelogText = buildString {
            // TODO:  write changelog

            with(generator) {
                this@buildString.writeChangelog(
                    newPack,
                    oldPack,
                    newPackMetaInfo,
                    oldPackMetaInfo,
                    newEntryMetaInfo,
                    oldEntryMetaInfo
                )
            }
        }

        logger.info("writing changelog to $changelogFile")

        // write changelog into changelogFile
        changelogFile.writeText(currentChangelogText)

        oldMeta.mkdirs()
        val currentCompleteChangelogFile = newMeta.resolve(Companion.Filename.completeChangelog)
        val oldCompleteChangelogFile = oldMeta.resolve(Companion.Filename.completeChangelog)

        val oldCompleteChanglogText = oldCompleteChangelogFile.takeIf { it.exists() }?.readText() ?: ""

        currentCompleteChangelogFile.writeText(oldCompleteChanglogText + currentChangelogText)

        // copy files to documentation
        changelogFile.copyTo(docDir.resolve(Companion.Filename.changelog), overwrite = true)
        currentCompleteChangelogFile.copyTo(docDir.resolve(Companion.Filename.completeChangelog), overwrite = true)
    }

    companion object: KLogging() {
        private object Filename {
            const val changelog = "changelog.md"
            const val completeChangelog = "complete_changelog.md"
            const val packMeta = "pack.meta.hjson"
            const val entryMeta = "entry.meta.hjson"
        }

        private val packMetaSerializer: LinkedHashMapSerializer<String, MetaInfo> =
            LinkedHashMapSerializer(String.serializer(), MetaInfo.serializer())
        private val entryMetaSerializer: LinkedHashMapSerializer<String, Map<String, MetaInfo>> =
            LinkedHashMapSerializer(String.serializer(), packMetaSerializer)

        fun writePackMetaInformation(newMeta: File, pack: LockPack): Map<String, MetaInfo> {
            val reportMap = pack.report().associateTo(linkedMapOf()) { it.first to MetaInfo(it.second, it.third) }

            val reportFile = newMeta.resolve(Companion.Filename.packMeta)
            val json = json.stringify(packMetaSerializer, reportMap)

            newMeta.mkdirs()
            reportFile.writeText(json)

            return reportMap
        }

        fun readPackMetaInformation(oldMeta: File): Map<String, MetaInfo> {
            val reportFile = oldMeta.resolve(Companion.Filename.packMeta)
            if (!reportFile.exists()) return mapOf()
            return json.parse(packMetaSerializer, reportFile.readText())
        }

        fun writeEntryMetaInformation(newMeta: File, pack: LockPack): Map<String, Map<String, MetaInfo>> {
            val reportMap = pack.entrySet.sortedBy { it.displayName.toLowerCase() }.associate { entry ->
                val provider = Providers[entry.provider]

                entry.id.toLowerCase() to provider.reportData(entry)
            }.mapValuesTo(linkedMapOf()) { (id, triples) ->
                triples.associate { it.first to MetaInfo(it.second, it.third) }
            }
            val reportFile = newMeta.resolve(Companion.Filename.entryMeta)
            val json = json.stringify(entryMetaSerializer, reportMap)

            newMeta.mkdirs()
            reportFile.writeText(json)

            return reportMap
        }

        fun readEntryMetaInformation(oldMeta: File): Map<String, Map<String, MetaInfo>> {
            val reportFile = oldMeta.resolve(Companion.Filename.entryMeta)
            if (!reportFile.exists()) return mapOf()
            return json.parse(entryMetaSerializer, reportFile.readText())
        }

        private fun changeTable(
            propHeader: String = "Property",
            oldheader: String = "old value",
            newheader: String = "new value",
            newMetaInfo: Map<String, MetaInfo>,
            oldMetaInfo: Map<String, MetaInfo>
        ): String? {
            val content = mutableListOf<List<String>>()
            newMetaInfo.forEach { key, newInfo ->
                val oldInfo = oldMetaInfo[key]
                if (oldInfo != null) {
                    // value was changed
                    if (oldInfo.value != newInfo.value) {
                        content += listOf(newInfo.name, oldInfo.value, newInfo.value)
                    }
                } else {
                    // value was added
                    content += listOf(newInfo.name, "", newInfo.value)
                }
            }

            oldMetaInfo.filter { (key, meta) -> !newMetaInfo.containsKey(key) }
                .forEach { key, oldInfo ->
                    // value was removed
                    content += listOf(oldInfo.name, oldInfo.value, "")
                }

            if (content.isEmpty())  {
                logger.debug("empty table because there was no differences btween")
                logger.debug { "old: $oldMetaInfo" }
                logger.debug { "new: $newMetaInfo" }
                return null
            }

            return markdownTable(
                headers = listOf(propHeader, oldheader, newheader),
                content = content.toList()
            )
        }
    }
}