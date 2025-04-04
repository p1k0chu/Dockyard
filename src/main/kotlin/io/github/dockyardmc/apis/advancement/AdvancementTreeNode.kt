package io.github.dockyardmc.apis.advancement

import io.github.dockyardmc.advancement.Advancement
import io.github.dockyardmc.advancement.AdvancementBuilder
import io.github.dockyardmc.maths.vectors.Vector2f

/**
 * To add children set their parent to `this`, like:
 * ```kotlin
 * val parent = AdvancementTreeNode(...)
 * val child = AdvancementTreeNode(...)
 * child.parent = parent
 * ```
 */
open class AdvancementTreeNode(
    var advancement: AdvancementBuilder,
) {
    private val children: MutableList<AdvancementTreeNode> = mutableListOf()

    var parent: AdvancementTreeNode? = null
        set(newParent) {
            field?.children?.remove(this)

            newParent?.children?.add(this)
            field = newParent
        }

    fun addChild(node: AdvancementTreeNode) {
        node.parent = this
    }

    /**
     * Moves this advancement and its children on the [vec]tor
     */
    fun move(vec: Vector2f) {
        advancement.x += vec.x
        advancement.y += vec.y

        children.forEach { it.move(vec) }
    }

    /**
     * Places this advancement at ([x], [y]) and moves its children
     */
    fun place(x: Float, y: Float) {
        val vecDiff = Vector2f(x - advancement.x, y - advancement.y)

        advancement.x = x
        advancement.y = y

        children.forEach { it.move(vecDiff) }
    }

    /** Default impl places children first in a vertical line,
     * then parent in the middle.
     *
     * @param startDrawingAt where the current advancement should be drawn.
     * if draws its children this is Y level where you start
     * @param distanceX horizontal distance between advancements and their children
     * @param distanceY vertical distance between children
     *
     * @return same as [startDrawingAt], returned value is passed to the next advancement
     */
    open fun buildTree(startDrawingAt: Vector2f = Vector2f(0f,0f), distanceX: Float = 1.5f, distanceY: Float = 1.5f): Vector2f {
        if (children.isEmpty()) {
            advancement.x = startDrawingAt.x
            advancement.y = startDrawingAt.y

            return startDrawingAt + Vector2f(0f, distanceY)
        }

        var drawAt = startDrawingAt.copy()
        drawAt.x += distanceX

        children.forEach {
            val nextDrawAt = it.buildTree(drawAt)
            drawAt = nextDrawAt
        }
        drawAt.x -= distanceX

        advancement.x = drawAt.x
        advancement.y = (startDrawingAt.y + drawAt.y - distanceY) / 2f

        return drawAt
    }

    /**
     * Returns you a list of all advancements as [Advancement]
     */
    fun getAdvancements(): MutableList<Advancement> {
        if(this.advancement.parentId == null) {
            this.advancement.parentId = parent?.advancement?.id
        }

        val result = mutableListOf<Advancement>()

        result.add(
            this.advancement.build()
        )

        children.forEach {
            result.addAll(it.getAdvancements())
        }

        return result
    }
}

class AdvancementTreeNodeBuilder(val id: String) {
    val advancementBuilder = AdvancementBuilder(id)
    var parent: AdvancementTreeNode? = null

    fun withAdvancement(builder: AdvancementBuilder.() -> Unit) {
        builder.invoke(advancementBuilder)
    }

    fun withParent(parent: AdvancementTreeNode) {
        this.parent = parent
    }

    fun build(): AdvancementTreeNode {
        val node = AdvancementTreeNode(advancementBuilder)
        node.parent = parent
        return node
    }
}

fun node(id: String, builder: AdvancementTreeNodeBuilder.() -> Unit): AdvancementTreeNode {
    val nodeBuilder = AdvancementTreeNodeBuilder(id)
    builder.invoke(nodeBuilder)
    return nodeBuilder.build()
}