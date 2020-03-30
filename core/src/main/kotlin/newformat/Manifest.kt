// Generated by delombok at Sat Jul 14 05:49:42 CEST 2018
/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */
package newformat

import kotlinx.serialization.Serializable
import voodoo.Modloader
import voodoo.util.serializer.URLSerializer
import java.net.URL

@Serializable
data class Manifest(

    // what is this even ?
    //var minimumVersion: Int = 0,

    var title: String? = null,

    var name: String? = null,

    var version: String? = null,
    @Serializable(with = URLSerializer::class)
    var baseUrl: URL? = null,

    var librariesLocation: String? = null,

    var objectsLocation: String? = null,

    // no longer required / move to versionManifest ?
    var gameVersion: String? = null,
    var modLoader: Modloader? = null,

    var features: List<Feature> = emptyList(),

    /*
    files to install
     */
    var tasks: List<FileInstall> = emptyList()
) {

    fun updateName(name: String?) {
        if (name != null) {
            this.name = name
        }
    }

    fun updateTitle(title: String?) {
        if (title != null) {
            this.title = title
        }
    }

    fun updateGameVersion(gameVersion: String?) {
        if (gameVersion != null) {
            this.gameVersion = gameVersion
        }
    }

//    @Serializer(forClass = Manifest::class)
//    companion object : KSerializer<Manifest> {
    companion object {
        val MIN_PROTOCOL_VERSION = 2
//
//        override fun serialize(output: Encoder, obj: Manifest) {
//            val elemOutput = output.beginStructure(descriptor)
//            obj.title?.let { title ->
//                elemOutput.encodeStringElement(descriptor, 1, title)
//            }
//            obj.displayName?.let { displayName ->
//                elemOutput.encodeStringElement(descriptor, 2, displayName)
//            }
//            obj.version?.let { version ->
//                elemOutput.encodeStringElement(descriptor, 3, version)
//            }
//            elemOutput.encodeIntElement(descriptor, 0, obj.minimumVersion)
//            obj.baseUrl?.let { baseUrl ->
//                elemOutput.encodeStringElement(descriptor, 4, baseUrl.toString())
//            }
//            obj.librariesLocation?.let { librariesLocation ->
//                elemOutput.encodeStringElement(descriptor, 5, librariesLocation)
//            }
//            obj.objectsLocation?.let { objectsLocation ->
//                elemOutput.encodeStringElement(descriptor, 6, objectsLocation)
//            }
//            obj.gameVersion?.let { gameVersion ->
//                elemOutput.encodeStringElement(descriptor, 7, gameVersion)
//            }
//            obj.launchModifier?.let { launchModifier ->
//                elemOutput.encodeSerializableElement(descriptor, 8, LaunchModifier.serializer(), launchModifier)
//            }
//            obj.features.takeUnless { it.isEmpty() }?.let { features ->
//                elemOutput.encodeSerializableElement(descriptor, 9, Feature.serializer().list, features)
//            }
//            obj.tasks.takeUnless { it.isEmpty() }?.let { tasks ->
//                elemOutput.encodeSerializableElement(descriptor, 10, FileInstall.serializer().list, tasks)
//            }
//            obj.versionManifest?.let { versionManifest ->
//                elemOutput.encodeSerializableElement(
//                    descriptor,
//                    11,
//                    VersionManifest.serializer(),
//                    versionManifest
//                )
//            }
//            elemOutput.endStructure(descriptor)
//        }
    }
}