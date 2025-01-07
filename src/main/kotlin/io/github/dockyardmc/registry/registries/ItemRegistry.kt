package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.reversed
import io.github.dockyardmc.extentions.toByteBuf
import io.github.dockyardmc.item.ItemComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.readComponent
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

@OptIn(ExperimentalSerializationApi::class)
object ItemRegistry : DataDrivenRegistry {

    override val identifier: String = "minecraft:item"

    var items: MutableMap<String, Item> = mutableMapOf()
    var protocolIdToItem: MutableMap<Int, Item> = mutableMapOf()

    override fun getMaxProtocolId(): Int {
        return protocolIdToItem.keys.last()
    }

    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<Item>>(stream)
        val protocolIdCounter = AtomicInteger()

        list.forEach {
            items[it.identifier] = it
            protocolIdToItem[protocolIdCounter.getAndIncrement()] = it
        }

        //TODO Figure default components out for future release, keeping this not implement for the time being so this update can move on

        val componentsBinary = ClassLoader.getSystemResource("registry/components.bin").openStream()
        val byteArray = componentsBinary.readAllBytes()
        componentsBinary.close()

        val buffer = byteArray.toByteBuf()
        val size = buffer.readVarInt()

        for (i in 0 until size) {
            val itemIdentifier = buffer.readString()
            val mapSize = buffer.readVarInt()

            val defaultComponents = mutableListOf<ItemComponent>()

            for (i1 in 0 until mapSize) {
                val componentId = buffer.readVarInt()
                val length = buffer.readVarInt()
                val component = buffer.readBytes(length)
                val readComponent = component.readComponent(componentId)
                defaultComponents.add(readComponent)
            }

            ItemRegistry[itemIdentifier].defaultComponents = defaultComponents
        }
    }

    override fun get(identifier: String): Item {
        return items[identifier] ?: throw RegistryException(identifier, items.size)
    }

    override fun getOrNull(identifier: String): Item? {
        return items[identifier]
    }

    override fun getByProtocolId(id: Int): Item {
        return protocolIdToItem[id] ?: throw RegistryException(id, items.size)
    }

    override fun getMap(): Map<String, Item> {
        return items
    }
}

@Serializable
data class Item(
    val identifier: String,
    val displayName: String,
    val maxStack: Int,
    val consumeSound: String,
    val canFitInsideContainers: Boolean,
    val isEnchantable: Boolean,
    val isStackable: Boolean,
    val isDamageable: Boolean,
    val isBlock: Boolean,
    @Transient
    var defaultComponents: List<ItemComponent>? = null
) : RegistryEntry {

    override fun getNbt(): NBTCompound? = null

    override fun getProtocolId(): Int {
        return ItemRegistry.protocolIdToItem.reversed()[this]
            ?: throw IllegalStateException("This item is not in the registry (how did you get hold of this object??)")
    }

    fun toItemStack(amount: Int = 1): ItemStack {
        return ItemStack(this, amount)
    }
}