package com.kisman.cc.gui.particle;

import java.util.*;
import java.util.List;

import com.kisman.cc.features.module.client.Config;

import com.kisman.cc.util.Colour;
import org.lwjgl.opengl.*;
import net.minecraft.client.*;
import org.lwjgl.input.*;

import static org.lwjgl.opengl.GL11.*;

public class ParticleSystem {
    private static final float SPEED = 0.2f;
    private final List<Particle> particleList;

    public ParticleSystem(final int initAmount) {
        this.particleList = new ArrayList<>();
        this.addParticles(initAmount);
    }

    public ParticleSystem() {
        this(StaticParticles.startPointsCount);
    }

    public void addParticles(final int n) {
        for (int i = 0; i < n; ++i) this.particleList.add(Particle.generateParticle());
    }

    public static double distance(final float x, final float y, final float x1, final float y1) {
        return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
    }

    public void tick(final int delta) {
        for (final Particle particle : this.particleList) particle.tick(delta, 0.1f);
    }

    //Draws a line
    private void drawLine(final float f, final float f2, final float f3, final float f4, Colour color, float width) {
        GL11.glColor4f(color.r1, color.g1, color.b1, color.a1);
        GL11.glLineWidth(width);
        GL11.glBegin(1);
        GL11.glVertex2f(f, f2);
        GL11.glVertex2f(f3, f4);
        GL11.glEnd();
    }

    //Draws a Gradient line
    private void drawGradientLine(final float f, final float f2, final float f3, final float f4, Colour startcolor, Colour endcolor, float width) {
        GL11.glPushMatrix();
        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);

        GL11.glColor4f(startcolor.r1, startcolor.g1, startcolor.b1, startcolor.a1);
        GL11.glLineWidth(width);
        GL11.glBegin(1);

        GL11.glVertex2f(f, f2);

        GL11.glColor4f(endcolor.r1, endcolor.g1, endcolor.b1, endcolor.a1);

        GL11.glVertex2f(f3, f4);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        GL11.glEnd();
        glPopMatrix();
    }

    //Draws a Three Gradient line
    private void drawThreeGradientLine(final float f, final float f2, final float f3, final float f4, Colour startcolor,  Colour midcolor, Colour endcolor, float width) {
        GL11.glPushMatrix();
        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);

        GL11.glColor4f(startcolor.r1, startcolor.g1, startcolor.b1, startcolor.a1);
        GL11.glLineWidth(width);
        GL11.glBegin(1);

        GL11.glVertex2f(f, f2);

        //

        GL11.glColor4f(midcolor.r1, midcolor.g1, midcolor.b1, midcolor.a1);

        float y;

        if(f2>=f4)
        {
            y = f4 + ((f2-f4)/2);
        }else{
            y = f2 + ((f4-f2)/2);
        }

        float x;

        if(f>=f3)
        {
            x = f3 + ((f-f3)/2);
        }else{
            x = f + ((f3-f)/2);
        }

        GL11.glVertex2f(x, y);
        GL11.glEnd();
        //////////////////////////

        GL11.glBegin(1);

        GL11.glColor4f(midcolor.r1, midcolor.g1, midcolor.b1, midcolor.a1);

        GL11.glVertex2f(x, y);

        //

        GL11.glColor4f(endcolor.r1, endcolor.g1, endcolor.b1, endcolor.a1);

        GL11.glVertex2f(f3, f4);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        GL11.glEnd();
        glPopMatrix();
    }

    //Draws a Gradient line glowing effect
    private void drawGradientLineGlowing(final float f, final float f2, final float f3, final float f4, Colour startcolor,  Colour endcolor, float width) {
        GL11.glPushMatrix();
        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);

        GL11.glColor4f(startcolor.r1, startcolor.g1, startcolor.b1, 151 * 55 / 255);
        GL11.glLineWidth(width+4);
        GL11.glBegin(1);

        GL11.glVertex2f(f, f2);

        GL11.glColor4f(endcolor.r1, endcolor.g1, endcolor.b1, 151 * 55 / 255);

        GL11.glVertex2f(f3, f4);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        GL11.glEnd();
        glPopMatrix();
    }

    public void render() {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        if (Minecraft.getMinecraft().currentScreen == null) return;
        for (final Particle particle : this.particleList) {
            if(Config.instance.particlesGradientMode.getValString().equals(Config.ParticlesGradientMode.Syns.name())) particle.color.glColor();
            else GL11.glColor4f(StaticParticles.color.r1, StaticParticles.color.g1, StaticParticles.color.b1, particle.getAlpha());
            if(StaticParticles.points) {
                GL11.glPointSize(particle.getSize() * StaticParticles.pointSizeModifier);
                GL11.glBegin(0);
                GL11.glVertex2f(particle.getX(), particle.getY());
                GL11.glEnd();
            }
            final int Width = Mouse.getEventX() * Minecraft.getMinecraft().currentScreen.width / Minecraft.getMinecraft().displayWidth;
            final int Height = Minecraft.getMinecraft().currentScreen.height - Mouse.getEventY() * Minecraft.getMinecraft().currentScreen.height / Minecraft.getMinecraft().displayHeight - 1;
            float nearestDistance = 0.0f;
            Particle nearestParticle = null;
            final int dist = 100;
            for (final Particle particle2 : this.particleList) {
                final float distance = particle.getDistanceTo(particle2);
                if (distance <= dist && (distance((float)Width, (float)Height, particle.getX(), particle.getY()) <= dist || distance((float)Width, (float)Height, particle2.getX(), particle2.getY()) <= dist)) {
                    if (nearestDistance > 0.0f && distance > nearestDistance) continue;
                    nearestDistance = distance;
                    nearestParticle = particle2;
                }
            }
            if (nearestParticle == null) continue;
//            final float alpha = Math.min(1.0f, Math.min(1.0f, 1.0f - nearestDistance / dist));

            if(StaticParticles.lines) {
                //Checks if two gradient particles mode is enabled
                if (StaticParticles.IsTwoGParticlesEnabled) {
                    //Checks if rendering gradient mode is default
                    if (StaticParticles.mode.equals(StaticParticles.modeDEfType)) {
                        this.drawGradientLine(particle.getX(), particle.getY(), nearestParticle.getX(), nearestParticle.getY(), StaticParticles.startColor, StaticParticles.endColor, StaticParticles.particleWidth);
                        //this.drawGradientLineGlowing(particle.getX(), particle.getY(), nearestParticle.getX(), nearestParticle.getY(), StaticParticles.startColor, StaticParticles.endColor, StaticParticles.particleWidth);
                    } else if (StaticParticles.mode.equals(StaticParticles.modeTGfType)) {
                        this.drawThreeGradientLine(particle.getX(), particle.getY(), nearestParticle.getX(), nearestParticle.getY(), StaticParticles.startColor, StaticParticles.middleColor, StaticParticles.endColor, StaticParticles.particleWidth);
                    } else {
                        this.drawLine(particle.getX(), particle.getY(), nearestParticle.getX(), nearestParticle.getY(), StaticParticles.color, StaticParticles.particleWidth);
                    }
                } else
                    this.drawLine(particle.getX(), particle.getY(), nearestParticle.getX(), nearestParticle.getY(), StaticParticles.color, StaticParticles.particleWidth);
            }
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(2884);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public void onUpdate()
    {
        StaticParticles.onUpdate();
    }

}

