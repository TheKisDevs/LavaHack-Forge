package com.kisman.cc.util.render.image

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import java.awt.Image
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

/**
 * @author _kisman_
 * @since 20:05 of 17.05.2022
 */
class ImageUtil {
    companion object {
        fun cacheBufferedImage(image : BufferedImage, format : String, name : String) : DynamicTexture {
            val texture = DynamicTexture(image)
            val location = Minecraft.getMinecraft().textureManager.getDynamicTextureLocation(name, texture)
            Minecraft.getMinecraft().textureManager.loadTexture(location, texture)
            return texture
        }

        fun createFlipped(image : BufferedImage) : BufferedImage {
            val at = AffineTransform()
            at.concatenate(AffineTransform.getScaleInstance(-1.0, 1.0))
            at.concatenate(AffineTransform.getTranslateInstance(-1.0, (-image.height).toDouble()))
            return createTransformed(image, at)
        }

        fun createTransformed(image: BufferedImage, at : AffineTransform) : BufferedImage {
            val newImage = BufferedImage(image.width, image.height, TYPE_INT_ARGB)
            val g = newImage.createGraphics()
            g.transform(at)
            g.drawImage(image, 0, 0, null)
            g.dispose()
            return newImage
        }

        @Throws(IOException::class)
        fun bufferedImageFromFile(file : File) : BufferedImage {
            val image = ImageIO.read(file)
            val bimage = BufferedImage(image.getWidth(null), image.getHeight(null), TYPE_INT_ARGB)
            val g = bimage.createGraphics()
            g.drawImage(image, 0, 0, null)
            g.dispose()
            return bimage
        }
    }
}