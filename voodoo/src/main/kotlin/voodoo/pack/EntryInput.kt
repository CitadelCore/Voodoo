package voodoo.pack

import com.github.ricky12awesome.jss.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import voodoo.data.Side
import voodoo.data.components.*
import voodoo.data.curse.FileID
import voodoo.data.curse.FileType
import voodoo.data.curse.PackageType
import voodoo.data.curse.ProjectID
import voodoo.data.flat.Entry

@Serializable
sealed class EntryInput(
//    @JsonSchema.Definition("entry.applyOverrides")
    @JsonSchema.StringEnum(["replace_with_overrides"])
    val applyOverrides: List<String> = listOf(),
    var name: String? = null,
    var folder: String? = null,
    var description: String? = null,
    var optional: Optional? = null,
    var side: Side = Side.BOTH,
    var websiteUrl: String = "",
//    var dependencies: MutableMap<String, DependencyType> = mutableMapOf(),
    var packageType: PackageType = PackageType.MOD,
    var transient: Boolean = false, // this entry got added as dependency for something else, only setthis if you know what you are doing
    var version: String = "", // TODO: use regex only ?
    var fileName: String? = null,
    var fileNameRegex: String = ".*(?<!-sources\\.jar)(?<!-api\\.jar)(?<!-deobf\\.jar)(?<!-lib\\.jar)(?<!-slim\\.jar)$",
    var validMcVersions: Set<String> = setOf(),
    var invalidMcVersions: Set<String> = setOf(),
    val enabled: Boolean = true
) {
    protected fun applyCommonOverride(tag: EntryOverride) {
        tag.folder?.let {
            folder = it
        }
        tag.description?.let {
            description = it
        }
        tag.optional?.let { optionalOverride ->
            val opt = this.optional ?: Optional()
            opt.applyOverride(optionalOverride)
            optional = opt
        }
        tag.side?.let {
            side = it
        }
        tag.websiteUrl?.let {
            websiteUrl = it
        }
        tag.packageType?.let {
            packageType = it
        }
        tag.version?.let {
            version = it
        }
        tag.fileName?.let {
            fileName = it
        }
        tag.fileNameRegex?.let {
            fileNameRegex = it
        }
        tag.validMcVersions?.let {
            validMcVersions += it
        }
        tag.invalidMcVersions?.let {
            invalidMcVersions += it
        }
    }

    protected fun toCommonComponent(id: String): CommonComponent {
        return CommonComponent(
            id = id,
            name = name,
            folder = folder,
            description = description,
            optionalData = optional?.toOptionalData(),
            side = side,
            websiteUrl = websiteUrl,
            dependencies = mutableMapOf(),
            packageType = packageType,
            transient = transient,
            version = version,
            fileName = fileName,
            fileNameRegex = fileNameRegex,
            validMcVersions = validMcVersions,
            invalidMcVersions = invalidMcVersions
        )
    }

    abstract fun applyTag(tag: EntryOverride): EntryInput
    abstract fun toEntry(id: String): Entry

    @Serializable
    @SerialName("curse")
    data class Curse(
        @JsonSchema.StringEnum(["replace_with_curseforge_projects"])
        val projectName: String? = null,
        @SerialName("curseProperties")
        val curse: CurseComponent = CurseComponent(),
    ) : EntryInput() {
        override fun applyTag(tag: EntryOverride): Curse {
            return when(tag) {
                is EntryOverride.Curse -> copy(
                    curse = curse.copy(
                        useOriginalUrl = tag.useOriginalUrl ?: curse.useOriginalUrl,
                        skipFingerprintCheck = tag.skipFingerprintCheck ?: curse.skipFingerprintCheck,
                    )
                ).apply {
                    applyCommonOverride(tag)
                }
                is EntryOverride.Common -> this.apply {
                    applyCommonOverride(tag)
                }
                else -> this
            }
        }

        override fun toEntry(id: String): Entry = Entry.Curse (
            common = toCommonComponent(id),
            curse = curse.copy()
        )
    }

    @Serializable
    @SerialName("direct")
    data class Direct(
        @SerialName("directProperties")
        val direct: DirectComponent = DirectComponent(),
    ) : EntryInput() {
        override fun applyTag(tag: EntryOverride): Direct {
            return when(tag) {
                is EntryOverride.Direct -> copy(
                    direct = direct.copy(
                        url = tag.url ?: direct.url,
                        useOriginalUrl = tag.useOriginalUrl ?: direct.useOriginalUrl
                    )
                ).apply {
                    applyCommonOverride(tag)
                }
                is EntryOverride.Common -> this.apply {
                    applyCommonOverride(tag)
                }
                else -> this
            }
        }

        override fun toEntry(id: String): Entry  =Entry.Direct(
            common = toCommonComponent(id),
            direct = direct.copy()
        )
    }

    @Serializable
    @SerialName("jenkins")
    data class Jenkins(
        @SerialName("jenkinsProperties")
        val jenkins: JenkinsComponent = JenkinsComponent(),
    ) : EntryInput() {
        override fun applyTag(tag: EntryOverride): Jenkins {
            return when(tag) {
                is EntryOverride.Jenkins -> copy(
                    jenkins = jenkins.copy(
                        jenkinsUrl = tag.jenkinsUrl ?: jenkins.jenkinsUrl,
                        job = tag.job ?: jenkins.job,
                        buildNumber = tag.buildNumber ?: jenkins.buildNumber
                    )
                ).apply {
                    applyCommonOverride(tag)
                }
                is EntryOverride.Common -> this.apply {
                    applyCommonOverride(tag)
                }
                else -> this
            }
        }
        override fun toEntry(id: String): Entry = Entry.Jenkins(
            common = toCommonComponent(id),
            jenkins = jenkins.copy()
        )
    }

    @Serializable
    @SerialName("local")
    data class Local(
        @SerialName("localProperties")
        val local: LocalComponent = LocalComponent(),
    ) : EntryInput() {
        override fun applyTag(tag: EntryOverride): Local {
            return when(tag) {
                is EntryOverride.Local -> copy(
                    local = local.copy(
                        fileSrc = tag.fileSrc ?: local.fileSrc
                    )
                ).apply {
                    applyCommonOverride(tag)
                }
                is EntryOverride.Common -> this.apply {
                    applyCommonOverride(tag)
                }
                else -> this
            }
        }
        override fun toEntry(id: String): Entry = Entry.Local(
            common = toCommonComponent(id),
            local = local.copy()
        )
    }

    @Serializable
    @SerialName("noop")
    class Noop() : EntryInput() {
        override fun applyTag(tag: EntryOverride): Noop {
            return when(tag) {
                is EntryOverride.Common -> this.apply {
                    applyCommonOverride(tag)
                }
                else -> this
            }
        }
        override fun toEntry(id: String): Entry = Entry.Noop(
            common = toCommonComponent(id)
        )
    }
}