// Generated by delombok at Sat Jul 14 04:26:21 CEST 2018
/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */
package com.skcraft.launcher.model.minecraft

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.Date
import java.util.LinkedHashSet

@JsonIgnoreProperties(ignoreUnknown = true)
class VersionManifest(
        var id: String? = null,
        var time: Date? = null,
        var releaseTime: Date? = null,
        var assets: String? = null,
        var type: String? = null,
        var processArguments: String? = null,
        var minecraftArguments: String? = null,
        var mainClass: String? = null,
        var minimumLauncherVersion: Int = 0,
        var libraries: LinkedHashSet<Library>? = null
) {

    val assetsIndex: String?
        @JsonIgnore
        get() = if (assets != null) assets else "legacy"

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is VersionManifest) return false
        if (if (this.id == null) other.id != null else this.id != other.id) return false
        if (if (this.time == null) other.time != null else this.time != other.time) return false
        if (if (this.releaseTime == null) other.releaseTime != null else this.releaseTime != other.releaseTime) return false
        if (if (this.assets == null) other.assets != null else this.assets != other.assets) return false
        if (if (this.type == null) other.type != null else this.type != other.type) return false
        if (if (this.processArguments == null) other.processArguments != null else this.processArguments != other.processArguments) return false
        if (if (this.minecraftArguments == null) other.minecraftArguments != null else this.minecraftArguments != other.minecraftArguments) return false
        if (if (this.mainClass == null) other.mainClass != null else this.mainClass != other.mainClass) return false
        if (this.minimumLauncherVersion != other.minimumLauncherVersion) return false
        return !if (this.libraries == null) other.libraries != null else this.libraries != other.libraries
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        result = result * PRIME + (id?.hashCode() ?: 43)
        result = result * PRIME + (time?.hashCode() ?: 43)
        result = result * PRIME + (releaseTime?.hashCode() ?: 43)
        result = result * PRIME + (assets?.hashCode() ?: 43)
        result = result * PRIME + (type?.hashCode() ?: 43)
        result = result * PRIME + (processArguments?.hashCode() ?: 43)
        result = result * PRIME + (minecraftArguments?.hashCode() ?: 43)
        result = result * PRIME + (mainClass?.hashCode() ?: 43)
        result = result * PRIME + this.minimumLauncherVersion
        result = result * PRIME + (libraries?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "VersionManifest(id=" + this.id + ", time=" + this.time + ", releaseTime=" + this.releaseTime + ", assets=" + this.assets + ", type=" + this.type + ", processArguments=" + this.processArguments + ", minecraftArguments=" + this.minecraftArguments + ", mainClass=" + this.mainClass + ", minimumLauncherVersion=" + this.minimumLauncherVersion + ", libraries=" + this.libraries + ")"
    }
}
