package io.github.dockyardmc.advancement

import kotlinx.datetime.Clock.System

class AdvancementProgress(
    val progress: MutableMap<String, Long?>
) {

    fun grantAdvancement() {
        progress.keys.forEach { key ->
            progress.put(key, System.now().epochSeconds)
        }
    }

    /**
     * @param timestamp epoch seconds, if null then use current time
     */
    fun grantCriteria(name: String, timestamp: Long? = null) {
        progress[name] = timestamp ?: System.now().epochSeconds
    }

    companion object {
        fun fromAdvancement(advancement: Advancement): AdvancementProgress {
            val progress = AdvancementProgress(mutableMapOf())

            advancement.requirements.flatten().forEach { key ->
                progress.progress.put(key, null)
            }

            return progress
        }
    }
}