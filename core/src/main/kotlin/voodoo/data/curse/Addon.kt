package voodoo.data.curse

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Addon(
    val id: ProjectID,
    val name: String,
    val authors: List<Author> = emptyList(),
    val attachments: List<Attachment>? = emptyList(),
    val webSiteURL: String,
    val gameId: Int,
    val summary: String,
    val defaultFileId: Int,
    val commentCount: Int,
    val downloadCount: Float,
    val rating: Int,
    val installCount: Int,
    val latestFiles: List<AddonFile>,
    val categories: List<Category> = emptyList(),
    val primaryAuthorName: String?,
    val externalUrl: String?,
    val status: ProjectStatus,
    val stage: ProjectStage,
    val donationUrl: String?,
    val primaryCategoryName: String?,
    val primaryCategoryAvatarUrl: String?,
    val likes: Int,
    val categorySection: CategorySection,
    val packageType: PackageType,
    val avatarUrl: String? = "",
    val slug: String,
    val clientUrl: String,
    val gameVersionLatestFiles: List<GameVersionLatestFile>,
//        val featured: Int,
    val popularityScore: Float,
    val gamePopularityRank: Int,
    val fullDescription: String,
    val gameName: String,
    val portalName: String,
    val sectionName: String, // Section,
    val dateModified: Date,
    val dateCreated: Date,
    val dateReleased: Date,
    val available: Boolean,
    val categoryList: String
)