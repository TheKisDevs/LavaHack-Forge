package kisman.pasta.salhack.util.render;

import java.awt.Color;

import com.kisman.cc.util.Colour;
import com.kisman.cc.util.EntityUtil;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import kisman.pasta.salhack.util.Hole;
import kisman.pasta.salhack.util.HoleTypes;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class ESPUtil {
    public static void ColorToGL(final Color color) {
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public static void RenderOutline(ICamera camera, RenderWorldLastEvent event)
    {
        GL11.glPushMatrix();
        Minecraft.getMinecraft().world.loadedEntityList.forEach(entity ->
        {
            if (entity != null && !entity.isDead && entity != Minecraft.getMinecraft().player)
            {
                double d3 = Minecraft.getMinecraft().player.lastTickPosX + (Minecraft.getMinecraft().player.posX - Minecraft.getMinecraft().player.lastTickPosX) * (double)event.getPartialTicks();
                double d4 = Minecraft.getMinecraft().player.lastTickPosY + (Minecraft.getMinecraft().player.posY - Minecraft.getMinecraft().player.lastTickPosY) * (double)event.getPartialTicks();
                double d5 = Minecraft.getMinecraft().player.lastTickPosZ + (Minecraft.getMinecraft().player.posZ - Minecraft.getMinecraft().player.lastTickPosZ) * (double)event.getPartialTicks();
                
                camera.setPosition(d3,  d4,  d5);
                
                if (camera.isBoundingBoxInFrustum(entity.getEntityBoundingBox()))
                {
                    RenderOutline(entity, 1, 1, 1, 255);
                    Minecraft.getMinecraft().getRenderManager().renderEntityStatic(entity, event.getPartialTicks(), false);
                }
            }
        });
        GL11.glPopMatrix();
    }
    
    public static void RenderShader(RenderWorldLastEvent event)
    {
        
    }
    
    public static void RenderOutline(final Entity entity, final double n, final double n2, final double n3, final int n4)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)n, (float)n2 + entity.height + 0.5f, (float)n3);
        final float n5 = 1.0f;
        final float n6 = 0.0f;
        GL11.glNormal3f(n6, n5, n6);
        final float n7 = Minecraft.getMinecraft().getRenderManager().playerViewY;
        final float n8 = 1.0f;
        final float n9 = 0.0f;
        GL11.glRotatef(n7, n9, n8, n9);
        GL11.glScalef(-0.017f, -0.017f, 0.017f);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        int n10 = 0;
        if (entity.isSneaking()) {
            n10 = 4;
        }
        GL11.glDisable(3553);
        GL11.glPushMatrix();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glEnable(2848);
        GlStateManager.disableLighting();
        RenderBoundingBox(0.0, n10 + 19, 0.0, n10 + 21, -16777216);
        RenderBoundingBox(0.0, n10 + 21, 0.0, n10 + 46, -16777216);
        RenderBoundingBox(0.0, n10 + 21, 0.0, n10 + 25, n4);
        RenderBoundingBox(0.0, n10 + 25, 0.0, n10 + 48, n4);
        RenderBoundingBox(0.0, n10 + 19, 0.0, n10 + 21, -16777216);
        RenderBoundingBox(0.0, n10 + 21, 0.0, n10 + 46, -16777216);
        RenderBoundingBox(0.0, n10 + 21, 0.0, n10 + 25, n4);
        RenderBoundingBox(0.0, n10 + 25, 0.0, n10 + 48, n4);
        RenderBoundingBox(0.0, n10 + 140, 0.0, n10 + 142, -16777216);
        RenderBoundingBox(0.0, n10 + 115, 0.0, n10 + 140, -16777216);
        RenderBoundingBox(0.0, n10 + 136, 0.0, n10 + 140, n4);
        RenderBoundingBox(0.0, n10 + 113, 0.0, n10 + 140, n4);
        RenderBoundingBox(0.0, n10 + 140, 0.0, n10 + 142, -16777216);
        RenderBoundingBox(0.0, n10 + 115, 0.0, n10 + 140, -16777216);
        RenderBoundingBox(0.0, n10 + 136, 0.0, n10 + 140, n4);
        RenderBoundingBox(0.0, n10 + 113, 0.0, n10 + 140, n4);
        GlStateManager.enableLighting();
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        final float n11 = 1.0f;
        final int n12 = 1;
        GL11.glColor4f((float)n12, (float)n12, n11, (float)n12);
        GL11.glPopMatrix();
    }
    
    public static void RenderBoundingBox(final double n, final double n2, final double n3, final double n4, final int n5)
    {
        final float n6 = (n5 >> 24 & 0xFF) / 255.0f;
        final float n7 = (n5 >> 16 & 0xFF) / 255.0f;
        final float n8 = (n5 >> 8 & 0xFF) / 255.0f;
        final float n9 = (n5 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(n7, n8, n9, n6);
        GL11.glBegin(7);
        GL11.glVertex2d(n, n4);
        GL11.glVertex2d(n3, n4);
        GL11.glVertex2d(n3, n2);
        GL11.glVertex2d(n, n2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    public static boolean IsVoidHole(BlockPos blockPos, IBlockState blockState)
    {
        if (blockPos.getY() > 4 || blockPos.getY() <= 0)
            return false;

        BlockPos l_Pos = blockPos;

        for (int l_I = blockPos.getY(); l_I >= 0; --l_I)
        {
            if (Minecraft.getMinecraft().world.getBlockState(l_Pos).getBlock() != Blocks.AIR)
                return false;

            l_Pos = l_Pos.down();
        }

        return true;
    }

    public static Hole.HoleTypes isBlockValid(IBlockState blockState, BlockPos blockPos)
    {
        if (blockState.getBlock() != Blocks.AIR)
            return Hole.HoleTypes.None;

        if (Minecraft.getMinecraft().world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR)
            return Hole.HoleTypes.None;

        if (Minecraft.getMinecraft().world.getBlockState(blockPos.up(2)).getBlock() != Blocks.AIR) // ensure the area is
                                                                             // tall enough for
                                                                             // the player
            return Hole.HoleTypes.None;

        if (Minecraft.getMinecraft().world.getBlockState(blockPos.down()).getBlock() == Blocks.AIR)
            return Hole.HoleTypes.None;

        final BlockPos[] touchingBlocks = new BlockPos[]
        { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west() };

        boolean l_Bedrock = true;
        boolean l_Obsidian = true;

        int validHorizontalBlocks = 0;
        for (BlockPos touching : touchingBlocks)
        {
            final IBlockState touchingState = Minecraft.getMinecraft().world.getBlockState(touching);
            if ((touchingState.getBlock() != Blocks.AIR) && touchingState.isFullBlock())
            {
                validHorizontalBlocks++;

                if (touchingState.getBlock() != Blocks.BEDROCK && l_Bedrock)
                    l_Bedrock = false;

                if (!l_Bedrock)
                {
                    if (touchingState.getBlock() != Blocks.OBSIDIAN && touchingState.getBlock() != Blocks.BEDROCK)
                        l_Obsidian = false;
                }
            }
        }

        if (validHorizontalBlocks < 4)
            return Hole.HoleTypes.None;

        if (l_Bedrock)
            return Hole.HoleTypes.Bedrock;
        if (l_Obsidian)
            return Hole.HoleTypes.Obsidian;

        return Hole.HoleTypes.Normal;
    }

    public enum HoleModes
    {
        None,
        FlatOutline,
        Flat,
        Outline,
        Full,
    }
    
    public static void Render(String holeMode, final AxisAlignedBB bb, Colour color) {
        switch (holeMode) {
            case "Flat":
                RenderGlobal.renderFilledBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.maxZ, color.getR(), color.getG(), color.getB(), color.getA());
                break;
            case "FlatOutline":
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.maxZ, color.getR(), color.getG(), color.getB(), color.getA());
                break;
            case "Full":
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color.getR(), color.getG(), color.getB(), color.getA());
                RenderGlobal.renderFilledBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color.getR(), color.getG(), color.getB(), color.getA());
                break;
            case "Outline":
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color.getR(), color.getG(), color.getB(), color.getA());
                break;
            default:
                break;
        }
    }
}
