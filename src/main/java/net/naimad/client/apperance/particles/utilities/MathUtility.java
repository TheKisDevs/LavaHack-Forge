package net.naimad.client.apperance.particles.utilities;

/**
 * @author Naimad
 * @since 15/11/2020
 */

public final class MathUtility {

    public static float getRandomInRange(final float min, final float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }
}
