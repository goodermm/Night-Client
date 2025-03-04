package net.aspw.client.features.module.impl.player

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.timer.TickTimer
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos

@ModuleInfo(name = "FastBridge", spacedName = "Fast Bridge", description = "", category = ModuleCategory.PLAYER)
class FastBridge : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Ninja", "God"), "Ninja")
    private val speedValue = IntegerValue("Place-Speed", 0, 0, 20)
    private val tickTimer = TickTimer()

    override val tag: String
        get() = modeValue.get()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        tickTimer.update()

        val shouldEagle = mc.theWorld.getBlockState(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)
        ).block === Blocks.air

        if (modeValue.get().equals("ninja", true))
            mc.gameSettings.keyBindSneak.pressed = shouldEagle

        if (tickTimer.hasTimePassed(0 + speedValue.get()) && mc.gameSettings.keyBindUseItem.isKeyDown && shouldEagle || mc.gameSettings.keyBindUseItem.isKeyDown && !mc.thePlayer.onGround) {
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)
            tickTimer.reset()
        }
    }

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && modeValue.get().equals("ninja", true))
            mc.gameSettings.keyBindSneak.pressed = false
    }
}
