package io.github.dockyard.tests.advancements

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.advancement.Advancement
import io.github.dockyardmc.advancement.advancement
import io.github.dockyardmc.registry.Items
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AdvancementTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testFlags() {
        val adv = advancement("some_id") {
            withTitle("Get wood")
            withDescription("Get OAK wood")
            withIcon(Items.OAK_LOG)

            useToast(true)
            withHidden(false)
            withBackground(null)
        }

        assertEquals(Advancement.SHOW_TOAST, adv.getFlags())

    }

    @Test
    fun `addAll should add viewers to all descendants`() {
        // Define the root advancement
        val i = advancement("some_id") {
            withTitle("Root Advancement")
            withDescription("This is the root advancement.")
        }

        // Define child advancements
        val j = advancement("some_id2") {
            withTitle("Child Advancement 1")
            withDescription("This is a child advancement of Root Advancement.")
            withParent(i)
        }
        val k = advancement("some_id3") {
            withTitle("Child Advancement 2")
            withDescription("This is another child advancement of Root Advancement.")
            withParent(i)
        }

        // Define grandchild advancements
        val l = advancement("some_id4") {
            withTitle("Grandchild Advancement 1")
            withDescription("This is a grandchild advancement of Child Advancement 1.")
            withParent(j)
        }
        val m = advancement("some_id5") {
            withTitle("Grandchild Advancement 2")
            withDescription("This is a grandchild advancement of Child Advancement 2.")
            withParent(k)
        }

        // Get the player
        val player = PlayerTestUtil.getOrCreateFakePlayer()

        // Add all children to the root
        i.addAll(player)

        // Verify that the player is added as a viewer to each child and grandchild
        assert(i.viewers.contains(player))
        assert(j.viewers.contains(player))
        assert(k.viewers.contains(player))
        assert(l.viewers.contains(player))
        assert(m.viewers.contains(player))

        // Remove the child advancements
        i.removeViewer(player)

        // Verify that the player is removed from each child and grandchild
        i.viewers.isEmpty()
        j.viewers.isEmpty()
        k.viewers.isEmpty()
        l.viewers.isEmpty()
        m.viewers.isEmpty()
    }
}