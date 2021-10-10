package net.naimad.client.apperance.particles;

import net.minecraft.client.gui.ScaledResolution;
import net.naimad.client.apperance.particles.impl.Particle;
import net.naimad.client.apperance.particles.utilities.MathUtility;
import org.lwjgl.input.Mouse;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Naimad
 * @since 15/11/2020
 */

public final class ParticleManager {

    private final List<Particle> particles = new LinkedList<>();

    private int radius;

    public void register(final ScaledResolution res) {
        this.radius = 75;

        if (!this.particles.isEmpty())
            this.particles.clear();

        IntStream.range(0, 175).forEach(index -> this.particles.add(this.generate(res)));
    }

    protected Particle generate(final ScaledResolution res) {
        return new Particle((int) MathUtility.getRandomInRange(1, res.getScaledWidth()),
                (int) MathUtility.getRandomInRange(1, res.getScaledHeight()),
                MathUtility.getRandomInRange(1f, 2f), (int) MathUtility.getRandomInRange(50, 255));
    }

    public void publish(final ScaledResolution res, final int mouseX, final int mouseY) {
        this.particles.forEach(particle -> {
            particle.update(res);
//            particle.publish();
        });
        
        this.particles.forEach(particle -> {
            if ((mouseX >= particle.getX() - this.radius)
                    && (mouseY >= particle.getY() - this.radius)
                    && (mouseX <= particle.getX() + this.radius)
                    && (mouseY <= particle.getY() + this.radius)) {

                this.particles
                        .stream()
                        .filter(all -> (all.getX() > particle.getX() && all.getX() - particle.getX() < this.radius
                                && particle.getX() - all.getX() < this.radius)
                                && (all.getY() > particle.getY() && all.getY() - particle.getY() < this.radius
                                || particle.getY() > all.getY() && particle.getY() - all.getY() < this.radius))
                        .forEach(points ->
                                particle.makeLines(points.getX(), points.getY()));
            }
        });

        if (Mouse.isButtonDown(2)) {
            final int container = 100;

            this.particles.forEach(particle -> particle.setAim((int) MathUtility.getRandomInRange(mouseX - container, mouseX + container),
                    (int) MathUtility.getRandomInRange(mouseY - container, mouseY + container)));
        }
    }

    public List<Particle> getParticles() {
        return particles;
    }
}
