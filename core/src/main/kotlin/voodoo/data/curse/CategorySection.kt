package voodoo.data.curse

import kotlinx.serialization.Serializable

@Serializable
data class CategorySection(
    val id: Int,
    val gameId: Int,
    val name: String,
    @Serializable(with = PackageType.Companion::class)
    val packageType: PackageType,
    val path: String,
    val initialInclusionPattern: String? = ".",
    val extraIncludePattern: String? = ""
)