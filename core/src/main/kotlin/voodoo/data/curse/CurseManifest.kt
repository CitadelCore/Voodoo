package voodoo.data.curse

data class CurseManifest(
    val name: String,
    val version: String,
    val author: String,
    val minecraft: CurseMinecraft = CurseMinecraft(),
    val manifestType: String,
    val manifestVersion: Int = 1,
    val files: List<CurseFile> = emptyList(),
    val overrides: String = "overrides",
    val projectID: Int = -1
)