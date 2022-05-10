package com.kisman.cc.util.render.cubic;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class BoundingBox {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public double minX;

    public double minY;

    public double minZ;

    public double maxX;

    public double maxY;

    public double maxZ;

    boolean corrected = false;

    public BoundingBox(AxisAlignedBB aabb){
        minX = aabb.minX;
        minY = aabb.minY;
        minZ = aabb.minZ;
        maxX = aabb.maxX;
        maxY = aabb.maxY;
        maxZ = aabb.maxZ;
    }

    public BoundingBox(BlockPos pos){
        this(new AxisAlignedBB(pos));
    }


    public BoundingBox grow(double amount){
        minX -= amount;
        minY -= amount;
        minZ -= amount;
        maxX += amount;
        maxY += amount;
        maxZ += amount;
        return this;
    }

    public BoundingBox growX(double amount){
        minX -= amount;
        maxX += amount;
        return this;
    }

    public BoundingBox growY(double amount){
        minY -= amount;
        maxY += amount;
        return this;
    }

    public BoundingBox growZ(double amount){
        minZ -= amount;
        maxZ -= amount;
        return this;
    }

    public BoundingBox centerTo(double x, double y, double z){
        double difX = (maxX - minX) * 0.5;
        double difY = (maxY - minY) * 0.5;
        double difZ = (maxZ - minZ) * 0.5;
        minX = x - difX;
        minY = y - difY;
        minZ = z - difZ;
        maxX = x + difX;
        maxY = y + difY;
        maxZ = z + difZ;
        return this;
    }

    /*
    public BoundingBox correct(){
        minX = minX - mc.renderManager.viewerPosX;
        minY = minY - mc.renderManager.viewerPosY;
        minZ = minZ - mc.renderManager.viewerPosZ;
        maxX = maxX - mc.renderManager.viewerPosX;
        maxY = maxY - mc.renderManager.viewerPosY;
        maxZ = maxZ - mc.renderManager.viewerPosZ;
        corrected = true;
        return this;
    }
     */

    public AxisAlignedBB toAABB(){
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
