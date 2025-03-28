package io.github.dockyardmc.advancement

import io.github.dockyardmc.apis.advancement.AdvancementEntry
import io.github.dockyardmc.player.Player

object AdvancementManager {
    private val innerTrackers = mutableListOf<PlayerAdvancementTracker>()
    private val innerAdvancements = mutableMapOf<String, Advancement>()

    val trackers get() = synchronized(innerTrackers) { innerTrackers.toList() }
    val advancements get() = synchronized(innerAdvancements) { innerAdvancements.toMap() }

    fun createAdvancementTracker(player: Player): PlayerAdvancementTracker {
        val tracker = PlayerAdvancementTracker(player)

        synchronized(innerTrackers) {
            innerTrackers.add(tracker)
        }

        synchronized(innerAdvancements) {
            innerAdvancements.forEach(tracker::onAdvancementAdded)
        }

        return tracker
    }

    fun removeAdvancementTracker(tracker: PlayerAdvancementTracker) {
        synchronized(innerTrackers) {
            innerTrackers.remove(tracker)
        }
    }

    fun addAdvancement(id: String, adv: Advancement): AdvancementEntry {
        synchronized(innerAdvancements) {
            innerAdvancements[id] = adv
        }

        synchronized(innerTrackers) {
            innerTrackers.forEach {
                it.onAdvancementAdded(id, adv)
            }
        }

        return AdvancementEntry(id, adv)
    }

    fun removeAdvancement(id: String) {
        synchronized(innerAdvancements) {
            innerAdvancements.remove(id)
        }

        synchronized(innerTrackers) {
            innerTrackers.forEach { it.onAdvancementRemoved(id) }
        }
    }

    fun addAdvancement(adv: AdvancementEntry) = addAdvancement(adv.id, adv.advancement)
    fun removeAdvancement(adv: AdvancementEntry) = removeAdvancement(adv.id)
}