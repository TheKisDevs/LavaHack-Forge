package com.kisman.cc.module.render

import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.Rendering
import com.kisman.cc.util.render.objects.TextOnBoundingBox
import com.kisman.cc.util.render.objects.TextOnEntityObject
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.abs

class ItemESPRewrite : Module("ItemESPRewrite", Category.RENDER) {

    private val limit = register(Setting("Limit", this, 0.0, 0.0, 200.0, true))
    private val itemName = register(Setting("ItemName", this, false))
    private val nameRenderDistance = register(Setting("NameRenderDistance", this, 8.0, 1.0, 30.0, false))
    private val render = register(Setting("Render", this, "Box", listOf("Box", "Glow")))
    private val renderDistance = register(Setting("RenderDistance", this, 8.0, 1.0, 100.0, false))
    private val renderMode = register(Setting("Render", this, Rendering.Mode.BOTH))
    private val outlineWidth = register(Setting("OutlineWidth", this, 2.0, 1.0, 5.0, false))
    private val color = register(Setting("Color", this, "Color", Colour(255, 255, 255, 120)))
    private val color1 = register(Setting("SecondColor", this, "SecondColor", Colour(255, 255, 255, 120)))
    private val textColor = register(Setting("TextColor", this, "TextColor", Colour(255, 255, 255, 255)))
    private val correct = register(Setting("Correct", this, false))

    private val glowingEntities = HashSet<Entity>(64)

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent){
        if(mc.world == null || mc.player == null){
            return
        }

        if(!isToggled){
            return
        }

        var count = 0;

        for(entity in mc.world.loadedEntityList){
            if(entity !is EntityItem){
                continue
            }

            if(count > limit.valInt){
                break
            }

            val item : EntityItem = entity

            if(item.getDistanceSq(mc.player) > renderDistance.valDouble){
                continue
            }

            if(render.valString.equals("Glow")){

                entity.glowing = true
                glowingEntities.add(entity)

                if(itemName.valBoolean){
                    if(item.getDistanceSq(mc.player) > nameRenderDistance.valDouble){
                        continue
                    }
                    val text = TextOnEntityObject(item.name, entity, textColor.colour)
                    text.draw(event.partialTicks)
                }

            } else {

                var aabb = Rendering.correct(entity.boundingBox)

                if(correct.valBoolean){
                    aabb = aabb.grow(0.08)
                    aabb = aabb.offset(0.0, 0.38, 0.0)
                }

                Rendering.draw(aabb, outlineWidth.valFloat, color.colour, color1.colour, renderMode.valEnum as Rendering.Mode)

                if(itemName.valBoolean){
                    if(item.getDistanceSq(mc.player) > nameRenderDistance.valDouble){
                        continue
                    }
                    val text = TextOnBoundingBox(item.name, aabb, textColor.colour)
                    text.draw(event.partialTicks)
                }
            }

            count++;
        }
    }

    override fun onDisable(){
        for(entity in glowingEntities){
            entity.glowing = false
        }
    }

    override fun isBeta() : Boolean {
        return true;
    }
}