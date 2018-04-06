package voodoo.data.nested

import voodoo.data.UserFiles
import voodoo.data.flat.ModPack

/**
 * Created by nikky on 28/03/18.
 * @author Nikky
 * @version 1.0
 */
data class NestedPack(
        var name: String,
        var title: String = "",
        var version: String = "1.0",
        var forge: String = "recommended",
        var mcVersion: String = "",
        var userFiles: UserFiles = UserFiles(),
        var root: NestedEntry = NestedEntry(),
        var versionCache: String? = null,
        var featureCache: String? = null,
        var localDir: String = "local",
        var minecraftDir: String = name
) {
    fun flatten(): ModPack {
        return ModPack(
                name = name,
                title = title,
                version = version,
                forge = forge,
                mcVersion = mcVersion,
                userFiles = userFiles,
                entries = root.flatten(),
                versionCache = versionCache,
                featureCache = featureCache,
                localDir = localDir,
                minecraftDir = minecraftDir
        )
    }
}