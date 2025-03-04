package net.aspw.client.features.module.impl.player

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.Render3DEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.render.ColorUtils.rainbow
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.util.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import org.lwjgl.opengl.GL11
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "Blink", description = "", category = ModuleCategory.PLAYER)
class Blink : Module() {
    private val pulseValue = BoolValue("Pulse", true)
    private val packets = LinkedBlockingQueue<Packet<*>>()
    private val positions = LinkedList<DoubleArray>()
    private val c0FValue = BoolValue("C0FCancel", false)
    private val pulseDelayValue = IntegerValue("PulseDelay", 400, 10, 5000, "ms")
    private val pulseTimer = MSTimer()
    private var fakePlayer: EntityOtherPlayerMP? = null
    private var disableLogger = false
    override fun onEnable() {
        if (mc.thePlayer == null) return
        if (!pulseValue.get()) {
            fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
            fakePlayer!!.clonePlayer(mc.thePlayer, true)
            fakePlayer!!.copyLocationAndAnglesFrom(mc.thePlayer)
            fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
            mc.theWorld.addEntityToWorld(-1337, fakePlayer)
        }
        synchronized(positions) {
            positions.add(
                doubleArrayOf(
                    mc.thePlayer.posX,
                    mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight() / 2,
                    mc.thePlayer.posZ
                )
            )
            positions.add(doubleArrayOf(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY, mc.thePlayer.posZ))
        }
        pulseTimer.reset()
    }

    override fun onDisable() {
        if (mc.thePlayer == null) return
        blink()
        if (fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
            fakePlayer = null
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (mc.thePlayer == null || disableLogger) return
        if (packet is C03PacketPlayer) // Cancel all movement stuff
            event.cancelEvent()
        if (packet is C04PacketPlayerPosition || packet is C06PacketPlayerPosLook ||
            packet is C08PacketPlayerBlockPlacement ||
            packet is C0APacketAnimation ||
            packet is C0BPacketEntityAction || packet is C02PacketUseEntity || c0FValue.get() && packet is C0FPacketConfirmTransaction
        ) {
            event.cancelEvent()
            packets.add(packet)
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        synchronized(positions) {
            positions.add(
                doubleArrayOf(
                    mc.thePlayer.posX,
                    mc.thePlayer.entityBoundingBox.minY,
                    mc.thePlayer.posZ
                )
            )
        }
        if (pulseValue.get() && pulseTimer.hasTimePassed(pulseDelayValue.get().toLong())) {
            blink()
            pulseTimer.reset()
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        val color = rainbow()
        synchronized(positions) {
            GL11.glPushMatrix()
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            mc.entityRenderer.disableLightmap()
            GL11.glBegin(GL11.GL_LINE_STRIP)
            RenderUtils.glColor(color)
            val renderPosX = mc.renderManager.viewerPosX
            val renderPosY = mc.renderManager.viewerPosY
            val renderPosZ = mc.renderManager.viewerPosZ
            for (pos in positions) GL11.glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ)
            GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
            GL11.glEnd()
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glPopMatrix()
        }
    }

    override val tag: String
        get() = packets.size.toString()

    private fun blink() {
        try {
            disableLogger = true
            while (!packets.isEmpty()) {
                mc.netHandler.networkManager.sendPacket(packets.take())
            }
            disableLogger = false
        } catch (e: Exception) {
            e.printStackTrace()
            disableLogger = false
        }
        synchronized(positions) { positions.clear() }
    }
}