package com.kisman.cc.features.module.render.crystalmodifier;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Quaternion;

import java.util.Arrays;

import static com.kisman.cc.features.module.render.CrystalModifier.CUBELET_SCALE;
import static com.kisman.cc.features.module.render.CrystalModifier.rotatingSide;

public class RubiksCrystalUtil {
    // Rotation of individual cubelets
    public static Quaternion[] cubeletStatus = {
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion(),
            new Quaternion()
    };

    // Get ID of cublet by position
    public static int[][][] cubletLookup = {
            // x
            {
                    //y
                    {17,9,0},
                    {20,16,3},
                    {23,15,6}
            },
            {
                    //y
                    {18,10,1},
                    {21,-1,4},
                    {24,14,7}
            },
            {
                    //y
                    {19,11,2},
                    {22,12,5},
                    {25,13,8}
            }
    };

    // Get cublet IDs of a side
    public static int[][] cubeSides = {
            // front
            {0,1,2,3,4,5,6,7,8},
            // back
            {19,18,17,22,21,20,25,24,23},
            // top
            {0,1,2,9,10,11,17,18,19},
            // bottom
            {23,24,25,15,14,13,6,7,8},
            // left
            {17,9,0,20,16,3,23,15,6},
            // right
            {2,11,19,5,12,22,8,13,25}
    };

    // Transformations of cube sides
    public static int[][] cubeSideTransforms = {
            {0,0,1},
            {0,0,-1},
            {0,1,0},
            {0,-1,0},
            {-1,0,0},
            {1,0,0}
    };

    // extra method from different class
    public static double easeInOutCubic (double t) {
        return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
    }

    public static void drawCubeletStatic(ModelRenderer cube, float scale, int x, int y, int z){
        int cubletId = cubletLookup[x+1][y+1][z+1];
        if (Arrays.stream(cubeSides[rotatingSide]).anyMatch(i -> i == cubletId)) return;
        drawCubelet(cube, scale, x, y, z, cubletId);
    }

    public static void drawCubeletRotating(ModelRenderer cube, float scale, int x, int y, int z){
        int cubletId = cubletLookup[x+1][y+1][z+1];
        if (Arrays.stream(cubeSides[rotatingSide]).noneMatch(i -> i == cubletId)) return;
        int[] trans = cubeSideTransforms[rotatingSide];
        drawCubelet(cube, scale, x - trans[0], y - trans[1], z - trans[2], cubletId);
    }

    public static void applyCubeletRotation(int x, int y, int z, int rX, int rY, int rZ){
        int cubletId = cubletLookup[x+1][y+1][z+1];
        if (Arrays.stream(cubeSides[rotatingSide]).noneMatch(i -> i == cubletId)) return;
        float RotationAngle = (float) Math.toRadians(90);
        float xx = (float) (rX * Math.sin(RotationAngle / 2));
        float yy = (float) (rY * Math.sin(RotationAngle / 2));
        float zz = (float) (rZ * Math.sin(RotationAngle / 2));
        float ww = (float) Math.cos(RotationAngle / 2);
        cubeletStatus[cubletId] = Quaternion.mul(new Quaternion(xx,yy,zz,ww), cubeletStatus[cubletId], null);
    }

    public static void drawCubelet(ModelRenderer cube, float scale, int x, int y, int z, int cubletId){
        GlStateManager.pushMatrix();
        GlStateManager.translate(x*CUBELET_SCALE, y*CUBELET_SCALE, z*CUBELET_SCALE);
        GlStateManager.rotate(cubeletStatus[cubletId]);
        cube.render(scale);
        GlStateManager.popMatrix();
    }
}
