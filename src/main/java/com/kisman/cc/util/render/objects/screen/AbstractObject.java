package com.kisman.cc.util.render.objects.screen;

import java.util.ArrayList;

import com.kisman.cc.util.render.ColorUtils;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

public class AbstractObject {
    public ArrayList<double[]> vectors;
    public Color color;
    public boolean line;
    public float width;

    public AbstractObject(ArrayList<double[]> vectors, Color color, boolean line, float width) {
        this.vectors = vectors;
        this.color = color;
        this.line = line;
        this.width = width;
    }

    public void render() {
        ColorUtils.glColor(color);
        if(line) {
            glLineWidth(width);
            glBegin(GL_POINTS);
        } else glBegin(GL_POLYGON);

        setupVectors();

        glEnd();
    }

    private void setupVectors() {
        for(double[] vector : vectors) glVertex2d(vector[0], vector[1]);
    }
}