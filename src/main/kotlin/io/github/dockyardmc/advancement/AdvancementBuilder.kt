package io.github.dockyardmc.advancement

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.scroll.Component

/**
 * Build the [Advancement] WITH [AdvancementDisplay]
 */
class AdvancementBuilder() {
    var parentId: String? = null
    val requirements = mutableListOf<List<String>>()

    var title: Component = Component(text = "")
    var description: Component = Component(text = "")
    var icon: ItemStack = Items.PAPER.toItemStack()
    var frame: AdvancementFrame = AdvancementFrame.TASK
    var showToast: Boolean = true
    var isHidden: Boolean = false
    var background: String? = null

    var x: Float = 0f
    var y: Float = 0f

    var pos: Pair<Float, Float>
        get() = x to y
        set(value) {
            x = value.first
            y = value.second
        }

    fun build(): Advancement {
        return Advancement(
            parentId,
            AdvancementDisplay(
                title, description, icon,
                frame, showToast, isHidden,
                background, x, y
            ),
            requirements
        )
    }

    companion object {
        operator fun invoke(builder: AdvancementBuilder.() -> Unit): AdvancementBuilder {
            val new = AdvancementBuilder()
            builder.invoke(new)
            return new
        }
    }
}