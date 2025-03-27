package io.github.dockyardmc.advancement

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUpdateAdvancementsPacket
import kotlin.collections.set

class PlayerAdvancementTracker(val player: Player) {

    val progress = mutableMapOf<String, AdvancementProgress>()

    fun update() {
        player.sendPacket(ClientboundUpdateAdvancementsPacket(
            true,
            AdvancementManager.advancements.toMap(),
            listOf(),
            progress.mapValues { it.value.progress }
        ))
    }

    fun onNewAdvancement(advId: String, adv: Advancement){
        progress[advId] = AdvancementProgress.fromAdvancement(adv)
    }
}