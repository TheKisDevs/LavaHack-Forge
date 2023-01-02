package com.kisman.cc.features.subsystem.subsystems

import com.kisman.cc.util.collections.LimitedHashMap
import com.kisman.cc.util.enums.RotationLogic
import com.kisman.cc.util.enums.dynamic.RotationEnum
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 11:53 of 02.01.2023
 */

@JvmField val rotationBlockCache = LimitedHashMap<BlockPos, FloatArray>(1000)
@JvmField val rotationEntityCache = LimitedHashMap<Entity, FloatArray>(1000)

fun rotateEntity(
    entity : Entity,
    rotator : RotationEnum.Rotation,
    silent : Boolean
) : Void = rotator.taskR.doTask(rotator.taskCEntity.doTask(entity.entityId, RotationLogic.Default).also { rotationEntityCache[entity] = it }, silent)

fun rotateEntity(
    entity : Entity,
    rotator : RotationEnum.Rotation
) : Void = rotateEntity(
    entity,
    rotator,
    false
)

fun rotateBlock(
    pos : BlockPos,
    rotator : RotationEnum.Rotation,
    silent : Boolean
) : Void = rotator.taskR.doTask(rotator.taskCBlock.doTask(pos).also { rotationBlockCache[pos] = it }, silent)

fun rotateBlock(
    pos : BlockPos,
    rotator : RotationEnum.Rotation
) : Void = rotateBlock(
    pos,
    rotator,
    false
)