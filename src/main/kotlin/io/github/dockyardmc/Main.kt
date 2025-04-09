package io.github.dockyardmc

import io.github.dockyardmc.advancement.AdvancementManager
import io.github.dockyardmc.advancement.advancement
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLoadedEvent
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items

fun main() {
    val server = DockyardServer {
        withIp("localhost")
        withPort(25565)
        withUpdateChecker(false)
        withImplementations {
            spark = false
        }
    }

    val root = AdvancementManager.addAdvancement(
        advancement("dockyard:tab1/root") {
            withTitle("<blue>Dockyard")
            withDescription("On github!!!\nDockyardMC/Dockyard")
            withIcon(Items.LAPIS_BLOCK)
            withBackground(Blocks.CHERRY_LEAVES)
            withPosition(0f, 0f)

            requirements += listOf("bleh", "blah", "obtained")
        })

    AdvancementManager.addAdvancement(
        advancement("dockyard:tab1/crash") {
            withParent(root.id)

            withTitle("<red>Dont click!")
            withDescription("This adv has no requirements, so it will crash adv info!")
            withIcon(Items.BARRIER)
            withPosition(1f, 0f)
        })


    Events.on<PlayerJoinEvent> { event ->
        val player = event.player
        player.permissions.add("dockyard.admin")
        player.permissions.add("dockyard.*")
        player.gameMode.value = GameMode.CREATIVE

    }
    Events.on<PlayerLoadedEvent> { event ->
        event.player.advancementTracker.grantCriterion(root.id, "obtained")
    }


    server.start()
}
