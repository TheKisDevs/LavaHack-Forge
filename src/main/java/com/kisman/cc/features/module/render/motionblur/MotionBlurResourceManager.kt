package com.kisman.cc.features.module.render.motionblur

import net.minecraft.client.resources.FallbackResourceManager
import net.minecraft.client.resources.IResource
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.data.MetadataSerializer
import net.minecraft.util.ResourceLocation

/**
 * @author _kisman_
 * @since 16:44 of 23.07.2022
 */
class MotionBlurResourceManager(
    frmMetadataSerializerIn: MetadataSerializer
) : FallbackResourceManager(
    frmMetadataSerializerIn
), IResourceManager {
    override fun getResourceDomains(): MutableSet<String>? {
        return null
    }

    override fun getResource(location: ResourceLocation): IResource {
        return MotionBlurResource()
    }

    override fun getAllResources(location: ResourceLocation): MutableList<IResource>? {
        return null
    }
}
