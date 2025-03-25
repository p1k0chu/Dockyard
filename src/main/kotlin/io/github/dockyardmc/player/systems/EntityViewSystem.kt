package io.github.dockyardmc.player.systems

import cz.lukynka.prettylog.log
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.player.Player
import java.util.concurrent.locks.ReentrantLock

class EntityViewSystem(val player: Player): TickablePlayerSystem {

    var visibleEntities: MutableList<Entity> = mutableListOf()
    val lock = ReentrantLock()

    fun clear() {
        synchronized(visibleEntities) {
            visibleEntities.toList().forEach { entity ->
                entity.removeViewer(player)
            }
            visibleEntities.clear()
        }
    }

    override fun tick() {
        if(lock.isLocked) return
        val entities = player.world.entities.toList().filter { it.autoViewable && it != player }

        val add = entities.filter { it.location.distance(player.location) <= it.viewDistanceBlocks && !visibleEntities.contains(it) }
        val remove = entities.filter { it.location.distance(player.location) > it.viewDistanceBlocks && visibleEntities.contains(it) }

        add.forEach { entity ->
            entity.addViewer(player)
            log("Added $entity to viewers")
        }
        remove.forEach { entity ->
            entity.removeViewer(player)
            log("Removed $entity to viewers")
        }
    }
}
