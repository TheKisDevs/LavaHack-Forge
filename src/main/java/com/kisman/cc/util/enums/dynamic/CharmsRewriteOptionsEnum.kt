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
        val type : CharmsRewriteTypes
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
            CharmsRewriteTypes.Depth
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
            CharmsRewriteTypes.Lighting
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
            CharmsRewriteTypes.Culling
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
            CharmsRewriteTypes.Blend
        ),
        Translucent(
            task.task {
                enableBlendProfile(Profile.TRANSPARENT_MODEL)
                return@task null
            },
            voidTask,
            voidTask,
            voidTask,
            CharmsRewriteTypes.Translucent
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
            CharmsRewriteTypes.Texture2D
        )
    }
}