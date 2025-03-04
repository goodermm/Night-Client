package net.aspw.client.visual.hud.element.elements

import com.google.gson.JsonElement
import net.aspw.client.util.misc.MiscUtils
import net.aspw.client.util.misc.RandomUtils
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.value.TextValue
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.Element
import net.aspw.client.visual.hud.element.ElementInfo
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.util.*
import javax.imageio.ImageIO

/**
 * CustomHUD image element
 *
 * Draw custom image
 */
@ElementInfo(name = "Image")
class Image : Element() {

    companion object {

        /**
         * Create default element
         */
        fun default(): Image {
            val image = Image()

            image.x = 0.0
            image.y = 0.0

            return image
        }

    }

    private val image: TextValue = object : TextValue("Image", "") {

        override fun fromJson(element: JsonElement) {
            super.fromJson(element)

            if (get().isEmpty())
                return

            setImage(get())
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (get().isEmpty())
                return

            setImage(get())
        }

    }

    private val resourceLocation = ResourceLocation(RandomUtils.randomNumber(128))
    private var width = 64
    private var height = 64

    /**
     * Draw element
     */
    override fun drawElement(): Border { //fix image
        GlStateManager.disableAlpha()
        RenderUtils.drawImage(resourceLocation, 0, 0, width / 2, height / 2)
        GlStateManager.enableAlpha()

        return Border(0F, 0F, width / 2F, height / 2F)
    }

    override fun createElement(): Boolean {
        val file = MiscUtils.openFileChooser() ?: return false

        if (!file.exists()) {
            MiscUtils.showErrorPopup("Error", "The file does not exist.")
            return false
        }

        if (file.isDirectory) {
            MiscUtils.showErrorPopup("Error", "The file is a directory.")
            return false
        }

        setImage(file)
        return true
    }

    private fun setImage(image: String): Image {
        try {
            this.image.changeValue(image)

            val byteArrayInputStream = ByteArrayInputStream(Base64.getDecoder().decode(image))
            val bufferedImage = ImageIO.read(byteArrayInputStream)
            byteArrayInputStream.close()

            width = bufferedImage.width
            height = bufferedImage.height

            mc.textureManager.loadTexture(resourceLocation, DynamicTexture(bufferedImage))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    fun setImage(image: File): Image {
        try {
            setImage(Base64.getEncoder().encodeToString(Files.readAllBytes(image.toPath())))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return this
    }
}