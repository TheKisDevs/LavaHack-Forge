package com.kisman.cc.util.entity

import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Thanks to FFP
 *
 * @author _kisman_
 * @since 12:47 of 04.06.2022
 */
@SideOnly(Side.CLIENT)
class EntityVoid(
    val world : World,
    val id : Int
) : Entity(world) {
    init { setEntityId(id) }
    override fun entityInit() {}
    override fun readEntityFromNBT(p0: NBTTagCompound) {}
    override fun writeEntityToNBT(p0: NBTTagCompound) {}
}