package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket


class ClientboundOpenContainerPacket(type: InventoryType, name: String) : ClientboundPacket() {

    init {
        buffer.writeVarInt(1)
        buffer.writeVarInt(type.ordinal)
        buffer.writeTextComponent(name)
    }
}

enum class InventoryType {
    GENERIC_9X1,
    GENERIC_9X2,
    GENERIC_9X3,
    GENERIC_9X4,
    GENERIC_9X5,
    GENERIC_9X6,
    GENERIC_3X3,
    CRAFTER_3X3,
    ANVIL,
    BEACON,
    BLAST_FURNACE,
    BREWING_STAND,
    CRAFTING_TABLE,
    ENCHANTMENT_TABLE,
    FURNACE,
    GRINDSTONE,
    HOPPER,
    LECTERN,
    LOOM, //of fate
    VILLAGER,
    SHULKER_BOX,
    SMITHING_TABLE,
    SMOKER,
    CARTOGRAPHY_TABLE,
    STONECUTTER
}