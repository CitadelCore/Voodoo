package voodoo.data.curse;

import com.fasterxml.jackson.annotation.JsonCreator

enum class ProjectStatus {
    NORMAL,
    HIDDEN,
    DELETED;

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromString(key: String?): ProjectStatus? {
            return if (key == null)
                null
            else {
                val index = key.toIntOrNull() ?: return valueOf(key.toUpperCase())
                return values()[index - 1]
            }
        }
    }
}