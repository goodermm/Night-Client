package net.aspw.client.features.module.impl.other

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.value.BoolValue
import net.aspw.client.visual.font.semi.Fonts
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import java.awt.Color

@ModuleInfo(name = "MurderDetector", spacedName = "Murder Detector", description = "", category = ModuleCategory.OTHER)
class MurderDetector : Module() {
    private val showText = BoolValue("ShowText", true)
    private val chatValue = BoolValue("Chat", true)
    private val notifyValue = BoolValue("Notification", true)

    var murder1: EntityPlayer? = null
    var murder2: EntityPlayer? = null

    private val murderItems = mutableListOf(
        267,  // Items.iron_sword,
        272,  // Items.stone_sword,
        256,  // Items.iron_shovel,
        280,  // Items.stick,
        271,  // Items.wooden_axe,
        268,  // Items.wooden_sword,
        273,  // Items.stone_shovel,
        369,  // Items.blaze_rod,
        277,  // Items.diamond_shovel,
        359,  // Items.shears,
        400,  // Items.pumpkin_pie,
        285,  // Items.golden_pickaxe,
        398,  // Items.carrot_on_a_stick,
        357,  // Items.cookie,
        279,  // Items.diamond_axe,
        283,  // Items.golden_sword,
        276,  // Items.diamond_sword,
        293,  // Items.diamond_hoe,
        421,  // Items.name_tag,
        333,  // Items.boat,
        409,  // Items.prismarine_shard,
        349,  // Items.fish,
        364,  // Items.cooked_beef,
        382,  // Items.speckled_melon,
        351,  // Items.dye,
        340,  // Items.book,
        406,  // Items.quartz,
        396,  // Items.golden_carrot,
        260,  // Items.apple,
        2258, // Items.record_blocks
        76,   // Blocks.redstone_torch,
        32,   // Blocks.deadbush,
        19,   // Blocks.sponge,
        122,  // Blocks.dragon_egg,
        175,  // Blocks.double_plant,
        405,  // Blocks.nether_brick,
        130   // Blocks.ender_chest
    )

    override fun onDisable() {
        murder1 = null
        murder2 = null
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        murder1 = null
        murder2 = null
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) {
            for (player in mc.theWorld.playerEntities) {
                if (mc.thePlayer.ticksExisted % 2 == 0) return
                if (player.heldItem != null && (player.heldItem.displayName.contains(
                        "Knife",
                        ignoreCase = true
                    ) || murderItems.contains(Item.getIdFromItem(player.heldItem.item)))
                ) {
                    if (murder1 == null) {
                        if (Client.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                            Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                        }
                        if (chatValue.get())
                            chat("§e" + player.name + "§r is Murder!")
                        if (notifyValue.get())
                            Client.hud.addNotification(
                                Notification(
                                    player.name + " is Murder!",
                                    Notification.Type.INFO,
                                    6000L
                                )
                            )
                        murder1 = player
                        return
                    }
                    if (murder2 == null && player != murder1) {
                        if (Client.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                            Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                        }
                        if (chatValue.get())
                            chat("§e" + player.name + "§r is Murder!")
                        if (notifyValue.get())
                            Client.hud.addNotification(
                                Notification(
                                    player.name + " is Murder!",
                                    Notification.Type.INFO,
                                    6000L
                                )
                            )
                        murder2 = player
                    }
                }
            }
        }
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val sc = ScaledResolution(mc)
        if (showText.get()) {
            Fonts.minecraftFont.drawString(
                if (murder1 != null) "Murder1: §e" + murder1?.name else "Murder1: §cNone",
                sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth(if (murder1 != null) "Murder1: §e" + murder1?.name else "Murder1: §cNone") / 2F,
                66.5F,
                Color(255, 255, 255).rgb,
                true
            )
            Fonts.minecraftFont.drawString(
                if (murder2 != null) "Murder2: §e" + murder2?.name else "Murder2: §cNone",
                sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth(if (murder2 != null) "Murder2: §e" + murder2?.name else "Murder2: §cNone") / 2F,
                77.5F,
                Color(255, 255, 255).rgb,
                true
            )
        }
    }
}