// Generated by delombok at Sat Jul 14 04:26:21 CEST 2018
/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */
package com.skcraft.launcher.model.modpack

import java.util.ArrayList
import java.util.Arrays

class RequireAny(
        private var features: MutableList<Feature> = ArrayList()
) : Condition {
    constructor(vararg feature: Feature) : this(feature.toMutableList())

    override fun matches(): Boolean {
        for (feature in features) {
            if (feature.isSelected) {
                return true
            }
        }
        return false
    }

    fun setFeatures(features: MutableList<Feature>) {
        this.features = features
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is RequireAny) return false
        if (!other.canEqual(this as Any)) return false
        return this.features == other.features
    }

    protected fun canEqual(other: Any): Boolean {
        return other is RequireAny
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        result = result * PRIME + features.hashCode()
        return result
    }

    override fun toString(): String {
        return "RequireAny(features=" + this.features + ")"
    }
}
