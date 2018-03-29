package voodoo.core.data.lock

import voodoo.core.data.Feature

/**
 * Created by nikky on 28/03/18.
 * @author Nikky
 * @version 1.0
 */

data class LockPack(
        val name: String = "",
        val title: String = "",
        val mcVersion: String = "",
        val forge: Int = -1,
        val entries: List<LockEntry> = emptyList(),
        val features: List<Feature> = emptyList()
)