package com.kisman.cc.features.module.render.esp

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.render.Rendering
import com.kisman.cc.util.render.objects.world.TextOnBoundingBox
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@ModuleInfo(
    name = "ItemESPRewrite",
    display = "Items",
    desc = "Highlights items better",
    submodule = true
)
class ItemESPRewrite : Module() {
    private val limit = register(Setting("Limit", this, 0.0, 0.0, 200.0, true))
    private val itemName = register(Setting("ItemName", this, false))
    private val nameRenderDistance = register(Setting("NameRenderDistance", this, 8.0, 1.0, 30.0, false))
    private val render = register(Setting("Render", this, "Box", listOf("Box", "Glow")))
    private val renderDistance = register(Setting("RenderDistance", this, 8.0, 1.0, 100.0, false))
    private val renderMode = register(Setting("Render Mode", this, Rendering.Mode.BOX_OUTLINE))
    private val outlineWidth = register(Setting("OutlineWidth", this, 2.0, 1.0, 5.0, false))
    private val color = register(Setting("Color", this, "Color", Colour(255, 255, 255, 120)))
    private val color1 = register(Setting("SecondColor", this, "SecondColor", Colour(255, 255, 255, 120)))
    private val textColor = register(Setting("TextColor", this, "TextColor", Colour(255, 255, 255, 255)))
    private val correct = register(Setting("Correct", this, false))
    private val interpolate = register(Setting("Interpolate", this, false))

    private val glowingEntities = HashSet<Entity>(64)

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent){
        if(mc.world == null || mc.player == null){
            return
        }

        for((count, entity) in mc.world.loadedEntityList.withIndex()){
            if(entity !is EntityItem){
                continue
            }

            if(count > limit.valInt){
                break
            }

            //squared val != non-squared val
            if(entity.getDistance(mc.player) > renderDistance.valDouble){
                continue
            }

            if(render.valString.equals("Glow")){

                entity.glowing = true
                glowingEntities.add(entity)

            } else {

                if(glowingEntities.contains(entity)) {
                    glowingEntities.remove(entity)
                }

                var aabb = entity.boundingBox

                if(interpolate.valBoolean){
                    val x = entity.lastTickPosX
                    val y = entity.lastTickPosY
                    val z = entity.lastTickPosZ
                    val w = entity.width / 2.0;
                    val h = entity.height
                    val amount = EntityUtil.getInterpolatedAmount(entity, event.partialTicks.toDouble())
                    val xA = amount.x
                    val yA = amount.y
                    val zA = amount.z
                    aabb = AxisAlignedBB(x - w + xA, y + yA, z - w + zA, x + w + xA, y + h + yA, z + w + zA)
                }

                aabb = Rendering.correct(aabb)

                if(correct.valBoolean){
                    aabb = aabb.grow(0.08)
                    aabb = aabb.offset(0.0, 0.24, 0.0)
                }

                Rendering.draw(aabb, outlineWidth.valFloat, color.colour, color1.colour, renderMode.valEnum as Rendering.Mode)
            }

            if(itemName.valBoolean){
                if(entity.getDistance(mc.player) > nameRenderDistance.valDouble){
                    continue
                }
                //item.name like "item.item.expBottle"
                TextOnBoundingBox(entity.item.displayName, entity.boundingBox, textColor.colour).draw(event.partialTicks)
            }
        }
    }

    override fun onDisable(){
        super.onDisable()
        for(entity in glowingEntities){
            entity.glowing = false
        }
    }
}