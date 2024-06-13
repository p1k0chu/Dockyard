package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.player.ProfileProperty
import io.github.dockyardmc.player.ProfilePropertyMap
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import java.util.UUID

class ClientboundLoginSuccessPacket(uuid: UUID, username: String, properties: MutableList<ProfilePropertyMap>): ClientboundPacket(2, ProtocolState.LOGIN) {
    init {
        data.writeUUID(uuid)
        data.writeUtf(username)
        data.writeVarInt(0)
        data.writeBoolean(false)
//        properties.forEach(data::writeProfileProperties)
    }
}