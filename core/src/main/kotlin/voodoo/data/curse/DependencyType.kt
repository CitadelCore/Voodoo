package voodoo.data.curse;

import com.fasterxml.jackson.annotation.JsonCreator
import mu.KLogging

enum class DependencyType {
    REQUIRED,
    OPTIONAL,
    EMBEDDED;

    companion object : KLogging() {
        @JsonCreator
        @JvmStatic
        fun fromString(key: String?): DependencyType? {
            return if (key == null)
                null
            else {
                val index = key.toIntOrNull() ?: return valueOf(key.toUpperCase())
                return values()[index - 1]
            }
        }
    }
}