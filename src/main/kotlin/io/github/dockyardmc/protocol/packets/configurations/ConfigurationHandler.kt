package io.github.dockyardmc.protocol.packets.configurations

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.FeatureFlags
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.PacketHandler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.login.ClientboundChangeDifficultyPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.utils.Resources
import io.github.dockyardmc.world.Difficulty
import io.github.dockyardmc.world.WorldManager
import io.netty.channel.ChannelHandlerContext

class ConfigurationHandler(val processor: PacketProcessor): PacketHandler(processor) {

    fun handlePluginMessage(packet: ServerboundPluginMessagePacket, connection: ChannelHandlerContext) {
        val event = PluginMessageReceivedEvent(packet.channel, packet.data)
        Events.dispatch(event)
        processor.player.brand = event.data

        // Send server brand
        val serverBrandEvent = ServerBrandEvent("§bDockyardMC §8| §7Custom Kotlin Server Implementation")
        Events.dispatch(serverBrandEvent)
        connection.sendPacket(ClientboundPluginMessagePacket("minecraft:brand", serverBrandEvent.brand))

        // Send feature flags
        val featureFlagsEvent = ServerFeatureFlagsEvent(FeatureFlags.enabledFeatureFlags)
        Events.dispatch(featureFlagsEvent)
        connection.sendPacket(ClientboundFeatureFlagsPacket(featureFlagsEvent.featureFlags))

        val registryDataPacket = ClientboundRegistryDataPacket(Resources.registry)
        connection.sendPacket(registryDataPacket)

        val finishConfigurationPacket = ClientboundFinishConfigurationPacket()
        connection.sendPacket(finishConfigurationPacket)
    }

    fun handleClientInformation(packet: ServerboundClientInformationPacket, connection: ChannelHandlerContext) {
        val clientConfiguration = ClientConfiguration(
            packet.locale,
            packet.viewDistance,
            packet.chatMode,
            packet.chatColors,
            packet.displayedSkinParts,
            packet.mainHandSide,
            packet.enableTextFiltering,
            packet.allowServerListing,
        )

        val event = PlayerClientConfigurationEvent(clientConfiguration, processor.player)
        Events.dispatch(event)
        processor.player.clientConfiguration = event.configuration
    }

    fun handleConfigurationFinishAcknowledge(packet: ServerboundFinishConfigurationAcknowledgePacket, connection: ChannelHandlerContext) {
        val player = processor.player
        processor.state = ProtocolState.PLAY
        processor.player.releaseMessagesQueue()

        val world = WorldManager.worlds[0]
        processor.player.world = world

        player.gameMode.value = GameMode.CREATIVE

        val playPacket = ClientboundPlayPacket(
            player.entityId,
            false,
            WorldManager.worlds.map { it.name }.toMutableList(),
            20,
            16,
            16,
            false,
            true,
            false,
            "overworld",
            world.name,
            world.seed,
            player.gameMode.value,
            GameMode.SURVIVAL,
            false,
            true,
            0
        )

        connection.sendPacket(playPacket)

        val difficultyPacket = ClientboundChangeDifficultyPacket(Difficulty.PEACEFUL, false)
        connection.sendPacket(difficultyPacket)

        val chunkCenterChunkPacket = ClientboundSetCenterChunkPacket(0, 0)
        connection.sendPacket(chunkCenterChunkPacket)

        val gameEventPacket = ClientboundPlayerGameEventPacket(GameEvent.START_WAITING_FOR_CHUNKS, 1f)
        connection.sendPacket(gameEventPacket)

        processor.player.world.chunks.forEach {
            connection.sendPacket(it.packet)
        }

        processor.player.location = world.defaultSpawnLocation

        connection.sendPacket(ClientboundRespawnPacket())
        connection.sendPacket(ClientboundPlayerSynchronizePositionPacket(world.defaultSpawnLocation))
        processor.player.isFullyInitialized = true

        //TODO Send command completion packets
//        connection.sendPacket(ClientboundCommandsPacket(mutableListOf(testCommand)))


        Events.dispatch(PlayerJoinEvent(processor.player))

        // Make player visibile to all other players by default
        PlayerManager.players.forEach { loopPlayer ->
            if(loopPlayer.username == player.username) return@forEach
            player.addViewer(loopPlayer)
            loopPlayer.addViewer(player)
        }

        //TODO add multi-world support and send this stuff when joining world
//        val worldBorder = player.world!!.worldBorder
//        val worldBorderPacket = ClientboundInitializeWorldBorderPacket(worldBorder.diameter, worldBorder.diameter, 0, worldBorder.warningBlocks, worldBorder.warningTime)
//        connection.sendPacket(worldBorderPacket)

//        connection.sendPacket(ClientboundPlayerAbilitiesPacket(isFlying = true, allowFlying = true))


        val tickingStatePacket = ClientboundSetTickingStatePacket(DockyardServer.tickRate, false)
        connection.sendPacket(tickingStatePacket)

        SkinManager.updateSkinOf(player)
    }
}