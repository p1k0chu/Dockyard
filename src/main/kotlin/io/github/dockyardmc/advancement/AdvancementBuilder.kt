package io.github.dockyardmc.advancement

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.maths.vectors.Vector2f
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.debug

/**
 * Build the [Advancement] WITH [AdvancementDisplay]
 */
class AdvancementBuilder() {
    var parentId: String? = null
    val requirements = mutableListOf<List<String>>()

    var title: String = ""
    var description: String = ""
    var icon: ItemStack = Items.PAPER.toItemStack()
    var frame: AdvancementFrame = AdvancementFrame.TASK
    var showToast: Boolean = true
    var isHidden: Boolean = false
    var background: String? = null

    var x: Float = 0f
    var y: Float = 0f

    fun withParent(id: String?) {
        this.parentId = id
    }

    fun withTitle(title: String) {
        this.title = title
    }

    fun withDescription(description: String) {
        this.description = description
    }

    fun withIcon(icon: ItemStack) {
        this.icon = icon
    }

    fun withIcon(icon: Item) {
        this.icon = icon.toItemStack()
    }

    fun withFrame(frame: AdvancementFrame) {
        this.frame = frame
    }

    fun useToast(showToast: Boolean) {
        this.showToast = showToast
    }

    fun withHidden(isHidden: Boolean) {
        this.isHidden = isHidden
    }

    fun withBackground(background: String?) {
        this.background = background
    }

    fun withBackground(background: Item) {
        val id = background.identifier
        debug("id = $id // BACKGROUND")
        TODO()
    }

    fun withPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
    fun withPosition(vec: Vector2f) {
        this.x = vec.x
        this.y = vec.y
    }

    fun withRequirement(req: String) {
        this.requirements += listOf(req)
    }

    fun withRequirementsAnyOf(requirements: List<String>) {
        this.requirements += requirements
    }

    fun build(): Advancement {
        return Advancement(
            parentId,
            AdvancementDisplay(
                title.toComponent(), description.toComponent(), icon,
                frame, showToast, isHidden,
                background, x, y
            ),
            requirements
        )
    }

    companion object {
        @Deprecated(level = DeprecationLevel.WARNING, message = "DONT")
        operator fun invoke(builder: AdvancementBuilder.() -> Unit): AdvancementBuilder {
            val new = AdvancementBuilder()
            builder.invoke(new)
            return new
        }
    }
}

fun advancement(builder: AdvancementBuilder.() -> Unit): Advancement {
    val adv = AdvancementBuilder()
    builder.invoke(adv)
    return adv.build()
}