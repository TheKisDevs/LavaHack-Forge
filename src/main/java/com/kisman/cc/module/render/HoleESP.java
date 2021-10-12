package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import kisman.pasta.salhack.util.Hole;
import kisman.pasta.salhack.util.Hole.HoleTypes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static kisman.pasta.salhack.util.render.ESPUtil.*;

public class HoleESP extends Module {
    private Setting radius = new Setting("Radius", this, 8, 0, 32, true);
    private Setting ignoreOwnHole = new Setting("IgnoreOwnHole", this, false);

    private Setting render = new Setting("RenderMode", this, "FlatOutline", new ArrayList<>(Arrays.asList("None", "FlatOutline", "Flat", "Outline", "Full")));

    private Setting obby = new Setting("_ObsidianHoles", this, "ObsidianHoles");

    private Setting obbyHoles = new Setting("ObsidianHoles", this, true);
    private Setting obbyColor = new Setting("ObbyColor", this, "ObbyColor", new float[] {0, 1, 0, 1}, false);


    private Setting bedrock = new Setting("_BedrockHoles", this, "BedrockHoles");

    private Setting bedrockHoles = new Setting("BedrockHoles", this, true);
    private Setting bedrockColor = new Setting("BedrockColor", this, "BedrockColor", new float[] {0, 1, 0, 1}, false);


    private List<Hole> holes = new ArrayList<>();

    public HoleESP() {
        super("HoleESP", "HoleESP", Category.RENDER);

        setmgr.rSetting(radius);
        setmgr.rSetting(ignoreOwnHole);
        setmgr.rSetting(render);

        setmgr.rSetting(obby);
        setmgr.rSetting(obbyHoles);
        setmgr.rSetting(obbyColor);

        setmgr.rSetting(bedrock);
        setmgr.rSetting(bedrockHoles);
        setmgr.rSetting(bedrockColor);
    }

    public void onEnable() {
        holes.clear();
    }

    public void onDisable() {
        holes.clear();
    }

    public void update() {
        holes.clear();

        if(mc.player == null && mc.world == null) return;

        final Vec3i playerPos = new Vec3i(mc.player.posX, mc.player.posY, mc.player.posZ);

        for(int x = playerPos.getX() - (int) radius.getValDouble(); x < playerPos.getX() + (int) radius.getValDouble(); x++) {
            for(int z = playerPos.getZ() - (int) radius.getValDouble(); z < playerPos.getZ() + (int) radius.getValDouble(); z++) {
                for(int y = playerPos.getY() - (int) radius.getValDouble(); x < playerPos.getY() + (int) radius.getValDouble(); y++) {
                    if(!render.getValString().equalsIgnoreCase("None")) {
                        final BlockPos blockPos = new BlockPos(x, y, z);

                        if(ignoreOwnHole.getValBoolean() && mc.player.getDistanceSq(blockPos) <= 1) {
                            continue;
                        }

                        final IBlockState blockState = mc.world.getBlockState(blockPos);

                        HoleTypes type = isBlockValid(blockState, blockPos);

                        if(type != HoleTypes.None) {
                            final IBlockState downBlockState = mc.world.getBlockState(blockPos.down());

                            if(downBlockState == Blocks.AIR) {
                                final BlockPos downPos = blockPos.down();

                                type = isBlockValid(downBlockState, blockPos);

                                if(type != HoleTypes.None) {
                                    holes.add(new Hole(downPos.getX(), downPos.getY(), downPos.getZ(), downPos, type, true));
                                }
                            } else {
                                holes.add(new Hole(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos, type));
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(!render.getValString().equalsIgnoreCase("None")) {
            new ArrayList<Hole>(holes).forEach(hole -> {
                final AxisAlignedBB bb = new AxisAlignedBB(
                        hole.getX() - mc.renderManager.viewerPosX,
                        hole.getY() - mc.renderManager.viewerPosY,
                        hole.getZ() - mc.renderManager.viewerPosZ,
                        hole.getX() + 1 - mc.renderManager.viewerPosX,
                        hole.getY() + (hole.isTall() ? 2 : 1) - mc.renderManager.viewerPosY,
                        hole.getZ() + 1 - mc.renderManager.viewerPosZ
                );

                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
                GL11.glLineWidth(1.5f);

                switch (hole.getHoleType())
                {
                    case Bedrock:
                        Render(render.getValString(), bb, new Colour(bedrockColor.getR(), bedrockColor.getG(), bedrockColor.getB(), bedrockColor.getA()));
                        break;
                    case Obsidian:
                        Render(render.getValString(), bb, new Colour(obbyColor.getR(), obbyColor.getG(), obbyColor.getB(), obbyColor.getA()));
                        break;
                    default:
                        break;
                }

                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            });
        }
    }
}
