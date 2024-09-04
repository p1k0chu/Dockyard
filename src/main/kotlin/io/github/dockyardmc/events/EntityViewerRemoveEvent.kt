package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.player.Player

@EventDocumentation("when viewer is removed from entity viewer list", true)
class EntityViewerRemoveEvent(var entity: Entity, var viewer: Player): CancellableEvent()