package io.github.dockyardmc.advancement

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.utils.debug

object AdvancementManager {
    val trackers = mutableListOf<PlayerAdvancementTracker>()
    val advancements = mutableMapOf<String, Advancement>()

    fun createAdvancementTracker(player: Player): PlayerAdvancementTracker {
        val tracker = PlayerAdvancementTracker(player)

        synchronized(advancements) {
            advancements.forEach { id, adv ->
                tracker.progress[id] = AdvancementProgress.fromAdvancement(adv)
            }
        }

        synchronized(trackers) {
            trackers.add(tracker)
        }

        return tracker
    }

    fun addAdvancement(id: String, adv: Advancement) {
        synchronized(advancements) {
            advancements[id] = adv
            debug("$advancements")
        }

        synchronized(trackers) {
            trackers.forEach {
                it.onNewAdvancement(id, adv)
            }
        }
    }
}