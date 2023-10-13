package net.aspw.client.features.module.impl.minigames

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.network.play.client.*

@ModuleInfo(name = "Debugger", description = "", category = ModuleCategory.MINIGAMES)
class Debugger : Module() {
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C0DPacketCloseWindow)
            chat("Close C0D // $event")
        if (packet is C08PacketPlayerBlockPlacement)
            chat("Place C08 // $event")
        if (packet is C0EPacketClickWindow)
            chat("Click C0E // $event")
        if (packet is C14PacketTabComplete)
            chat("Getlist C14 // $event")
        if (packet is C02PacketUseEntity)
            chat("Attack C02 // $event")
        if (packet is C09PacketHeldItemChange)
            chat("NewHeld C09 // $event")
        if (packet is C0APacketAnimation)
            chat("Swing C0A // $event")
    }
}