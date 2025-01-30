package io.github.dockyardmc

import io.github.dockyardmc.apis.Hologram
import io.github.dockyardmc.apis.hologram
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.commands.simpleSuggestion
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.ItemRarity
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.server.ServerMetrics
import io.github.dockyardmc.utils.DebugSidebar
import io.github.dockyardmc.world.WorldManager

// This is just testing/development environment.
// To properly use dockyard, visit https://dockyardmc.github.io/Wiki/wiki/quick-start.html

lateinit var holo: Hologram

fun main(args: Array<String>) {

    if (args.contains("event-documentation")) {
        EventsDocumentationGenerator()
        return
    }

    val server = DockyardServer {
        withIp("0.0.0.0")
        withMaxPlayers(50)
        withPort(25565)
        useMojangAuth(true)
        useDebugMode(true)
        withNetworkCompressionThreshold(256)
    }


    Events.on<PlayerJoinEvent> {
        val player = it.player

        DockyardServer.broadcastMessage("<yellow>${player} joined the game.")
        player.gameMode.value = GameMode.CREATIVE
        player.permissions.add("dockyard.all")

        DebugSidebar.sidebar.viewers.add(player)

        player.addPotionEffect(PotionEffects.NIGHT_VISION, -1, 0, false)

        val item = ItemStack(Items.SWEET_BERRIES, 10).withConsumable(0.1f).withFood(2, 0f, true).withRarity(ItemRarity.EPIC)
        player.give(item)
    }

    Events.on<PlayerLeaveEvent> {
        DockyardServer.broadcastMessage("<yellow>${it.player} left the game.")
    }

    Commands.add("/holo") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()
            if (!::holo.isInitialized) {
                holo = hologram(player.location) {
                    withPlayerLine { player -> "<yellow><bold>$player's position:" }
                    withPlayerLine { player -> "//position update" }
                    withStaticLine(" ")
                    withStaticLine("// mem update below")
                }
            } else {
                holo.teleport(player.location)
            }
        }
    }

    Events.on<PlayerMoveEvent> { event ->
        if (::holo.isInitialized) holo.setPlayerLine(1) { player -> "<lime>${player.location.blockX} ${player.location.blockY} ${player.location.blockZ}" }
    }

    Events.on<ServerTickEvent> {
        if (::holo.isInitialized) holo.setStaticLine(3, "<gray>Server memory: <aqua>${ServerMetrics.memoryUsageTruncated}")
    }

    Commands.add("/reset") {
        addArgument("player", PlayerArgument(), simpleSuggestion(""))
        execute {
            val platformSize = 30

            val world = WorldManager.mainWorld

            world.batchBlockUpdate {
                for (x in 0 until platformSize) {
                    for (z in 0 until platformSize) {
                        setBlock(x, 0, z, Blocks.STONE)
                        for (y in 1 until 20) {
                            setBlock(x, y, z, Blocks.AIR)
                        }
                    }
                }
            }
        }
    }
    server.start()
}