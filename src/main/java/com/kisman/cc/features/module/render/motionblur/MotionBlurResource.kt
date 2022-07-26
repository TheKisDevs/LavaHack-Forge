package com.kisman.cc.features.module.render.motionblur

import com.kisman.cc.Kisman
import com.kisman.cc.features.module.render.MOTION_BLUR_SHADER
import com.kisman.cc.features.module.render.MotionBlur
import net.minecraft.client.resources.IResource
import net.minecraft.client.resources.data.IMetadataSection
import net.minecraft.util.ResourceLocation
import org.apache.commons.io.IOUtils
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

/**
 * @author _kisman_
 * @since 16:34 of 23.07.2022
 */
class MotionBlurResource : IResource {
    override fun close() {
        TODO("Not yet implemented")
    }

    override fun getResourceLocation(): ResourceLocation? {
        return null
    }

    override fun getInputStream(): InputStream {
        val amount = (Kisman.instance.moduleManager.getModule("MotionBlur") as MotionBlur).amount.valInt
        return IOUtils.toInputStream(
            String.format(
                Locale.ENGLISH,
                MOTION_BLUR_SHADER,
                amount,
                amount,
                amount
            ),
            Charset.defaultCharset()
        )
    }

    override fun hasMetadata(): Boolean {
        return false
    }

    override fun <T : IMetadataSection?> getMetadata(p0: String): T? {
        return null
    }

    override fun getResourcePackName(): String? {
        return null
    }

}