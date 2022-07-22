package com.kisman.cc.util.enums.dynamic

import com.kisman.cc.util.enums.CharmsRewriteTypes
import org.cubic.dynamictask.AbstractTask
import net.minecraft.client.renderer.GlStateManager.*

/**
 * @author _kisman_
 * @since 23:17 of 13.07.2022
 */
class CharmsRewriteOptionsEnum {
    companion object {
        private val task : AbstractTask.DelegateAbstractTask<Void> = AbstractTask.types(
            Void::class.java,
            Void::class.java
        )

        private val voidTask = task.task { return@task null }
    }

    enum class CharmsRewriteOptions(
        val beginIfTrue : AbstractTask<Void>,
        val beginIfFalse : AbstractTask<Void>,
        val afterIfTrue : AbstractTask<Void>,
        val afterIfFalse : AbstractTask<Void>,
        val typeW : CharmsRewriteTypes,
        val typeM : CharmsRewriteTypes
    ) {
        Depth(
            task.task {
                enableDepth()
                return@task null
            },
            task.task {
                disableDepth()
                return@task null
            },
            task.task {
                disableDepth()
                return@task null
            },
            task.task {
                enableDepth()
                return@task null
            },
            CharmsRewriteTypes.WireDepth,
            CharmsRewriteTypes.ModelDepth
        ),
        Lighting(
            task.task {
                enableLighting()
                return@task null
            },
            task.task {
                disableLighting()
                return@task null
            },
            task.task {
                disableLighting()
                return@task null
            },
            task.task {
                enableLighting()
                return@task null
            },
            CharmsRewriteTypes.WireLighting,
            CharmsRewriteTypes.ModelLighting
        ),
        Culling(
            task.task {
                enableCull()
                return@task null
            },
            task.task {
                disableCull()
                return@task null
            },
            task.task {
                disableCull()
                return@task null
            },
            task.task {
                enableCull()
                return@task null
            },
            CharmsRewriteTypes.WireCulling,
            CharmsRewriteTypes.ModelCulling
        ),
        Blend(
            task.task {
                enableBlend()
                return@task null
            },
            task.task {
                disableBlend()
                return@task null
            },
            task.task {
                disableBlend()
                return@task null
            },
            task.task {
                enableBlend()
                return@task null
            },
            CharmsRewriteTypes.WireBlend,
            CharmsRewriteTypes.ModelBlend
        ),
        Translucent(
            task.task {
                enableBlendProfile(Profile.TRANSPARENT_MODEL)
                return@task null
            },
            voidTask,
            voidTask,
            voidTask,
            CharmsRewriteTypes.WireTranslucent,
            CharmsRewriteTypes.ModelTranslucent
        ),
        Texture2D(
            task.task {
                enableTexture2D()
                return@task null
            },
            task.task {
                disableTexture2D()
                return@task null
            },
            task.task {
                disableTexture2D()
                return@task null
            },
            task.task {
                enableTexture2D()
                return@task null
            },
            CharmsRewriteTypes.WireTexture2D,
            CharmsRewriteTypes.ModelTexture2D
        )
    }
}