// Generated by delombok at Sat Jul 14 01:46:55 CEST 2018
/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */
package com.skcraft.launcher.builder

import com.beust.jcommander.Parameter
import java.io.File

class ServerExportOptions {
    @Parameter(names = ["--source"], required = true)
    var sourceDir: File? = null
    @Parameter(names = ["--dest"], required = true)
    var destDir: File? = null

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is ServerExportOptions) return false
        if (if (this.sourceDir == null) other.sourceDir != null else this.sourceDir != other.sourceDir) return false
        return !if (this.destDir == null) other.destDir != null else this.destDir != other.destDir
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        result = result * PRIME + (sourceDir?.hashCode() ?: 43)
        result = result * PRIME + (destDir?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "ServerExportOptions(sourceDir=" + this.sourceDir + ", destDir=" + this.destDir + ")"
    }
}
