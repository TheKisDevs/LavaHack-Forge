package com.kisman.cc.features.module.combat.autorer

import com.kisman.cc.features.module.combat.AntiBot
import com.kisman.cc.features.module.combat.AutoRer
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.entity.TargetFinder
import com.kisman.cc.util.enums.AutoRerTargetFinderLogic
import com.kisman.cc.util.math.max
import com.kisman.cc.util.world.CrystalUtils
import net.minecraft.entity.player.EntityPlayer
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 14:02 of 18.08.2022
 */
class AutoRerTargetFinder(
    private val logic : Supplier<AutoRerTargetFinderLogic>,
    private val placeRange : Supplier<Float>,
    private val autoRer : AutoRer,
    range : Supplier<Double>,
    delay : Supplier<Long>,
    threadded : Supplier<Boolean>
) : TargetFinder(
    range,
    delay,
    threadded
) {
    override fun getTarget(
        range : Float,
        wallRange : Float
    ) : EntityPlayer? {
        var currentTarget : EntityPlayer? = null
        var minHealth = 50f
        var maxDamage = 0.5f

        for(player in mc.world.playerEntities) {
            if (AntiBot.instance.isToggled && AntiBot.instance.mode.checkValString("Zamorozka") && !EntityUtil.antibotCheck(player)) {
                continue
            }

            if (!isntValid(player, range.toDouble(), wallRange.toDouble())) {
                if (currentTarget == null || (mc.player.getDistanceSq(player) < mc.player.getDistanceSq(currentTarget))) {
                    val damage = getDamageForPlayer(player)

                    if(logic.get() == AutoRerTargetFinderLogic.Damage) {
                        if(damage < maxDamage) {
                            continue
                        }
                    } else if(logic.get() == AutoRerTargetFinderLogic.Health) {
                        if(player.health > minHealth) {
                            continue
                        }
                    }

                    currentTarget = player

                    if(damage > maxDamage) {
                        maxDamage = damage
                    }

                    if(player.health < minHealth) {
                        minHealth = player.health
                    }
                }
            }
        }
        return currentTarget
    }

    private fun getDamageForPlayer(
        player : EntityPlayer
    ) : Float {
        var maxDamage = 0.5f


        for(pos in EntityUtil.getSphere(player.position, placeRange.get(), 1 + placeRange.get().toInt(), false, true, 0)) {
            if(
                !(autoRer.thirdCheck.valBoolean && !autoRer.isPosValid(pos))
                && CrystalUtils.canPlaceCrystal(
                    pos,
                    autoRer.secondCheck.valBoolean,
                    true,
                    autoRer.needToMultiPlace(),
                    autoRer.firePlace.valBoolean
                )
            ) {
                val targetDamage = CrystalUtils.calculateDamage(
                    mc.world,
                    pos.x + 0.5,
                    (pos.y + 1).toDouble(),
                    pos.z + 0.5,
                    AutoRer.currentTarget,
                    autoRer.terrain.valBoolean
                )

                maxDamage = maxDamage.max(CrystalUtils.calculateDamage(
                    mc.world,
                    pos.x + 0.5,
                    (pos.y + 1).toDouble(),
                    pos.z + 0.5,
                    AutoRer.currentTarget,
                    autoRer.terrain.valBoolean
                ))

            }
        }

        return maxDamage
    }
}