package io.github.dockyardmc.advancement

import io.github.dockyardmc.player.Player

object AdvancementManager {
    private val innerTrackers = mutableListOf<PlayerAdvancementTracker>()
    private val innerAdvancements = mutableMapOf<String, Advancement>()

    val trackers get() = synchronized(innerTrackers) { innerTrackers.toList() }
    val advancements get() = synchronized(innerAdvancements) { innerAdvancements.toMap() }

    fun createAdvancementTracker(player: Player): PlayerAdvancementTracker {
        val tracker = PlayerAdvancementTracker(player)

        synchronized(innerAdvancements) {
            innerAdvancements.forEach(tracker::onAdvancementAdded)
        }

        synchronized(innerTrackers) {
            innerTrackers.add(tracker)
        }

        return tracker
    }

    fun removeAdvancementTracker(tracker: PlayerAdvancementTracker) {
        synchronized(innerTrackers) {
            innerTrackers.remove(tracker)
        }
    }

    fun addAdvancement(id: String, adv: Advancement) {
        synchronized(innerAdvancements) {
            innerAdvancements[id] = adv
        }

        synchronized(innerTrackers) {
            innerTrackers.forEach {
                it.onAdvancementAdded(id, adv)
            }
        }
    }

    fun removeAdvancement(id: String) {
        synchronized(innerAdvancements) {
            innerAdvancements.remove(id)
        }

        synchronized(innerTrackers) {
            innerTrackers.forEach { it.onAdvancementRemoved(id) }
        }
    }
}