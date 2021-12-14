package i.gishreloaded.gishcode.utils.visual;

import net.minecraft.client.renderer.GlStateManager;

import java.awt.Color;

public class ColorUtils {
	public static Color rainbow() {
		long offset = 999999999999L;
		float fade = 1.0f;
        float hue = (float) (System.nanoTime() + offset) / 1.0E10f % 1.0f;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16);
        Color c = new Color((int) color);
        return new Color((float) c.getRed() / 255.0f * fade, (float) c.getGreen() / 255.0f * fade, (float) c.getBlue() / 255.0f * fade, (float) c.getAlpha() / 255.0f);
    }
	
	public static int color(int r, int g, int b, int a) {
		return new Color(r, g, b, a).getRGB();
	}
	
	public static int color(float r, float g, float b, float a) {
		return new Color(r, g, b, a).getRGB();
	}
	
	public static int getColor(int a, int r, int g, int b) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int getColor(int r, int g, int b) {
        return 255 << 24 | r << 16 | g << 8 | b;
    }

    public static int astolfoColors(int yOffset, int yTotal) {
        float hue;
        float speed = 2900.0f;
        for (hue = (float) (System.currentTimeMillis() % (long) ((int)speed)) + (float) ((yTotal - yOffset) * 9); hue > speed; hue -= speed) {
        }
        if ((double) (hue /= speed) > 0.5) {
            hue = 0.5f - (hue - 0.5f);
        }
        return Color.HSBtoRGB(hue += 0.5f, 0.5f, 1.0f);
    }

    public static int getRed(int color) {
        return new Color(color).getRed();
    }

    public static int getGreen(int color) {
        return new Color(color).getGreen();
    }

    public static int getBlue(int color) {
        return new Color(color).getBlue();
    }

    public static int getAlpha(int color) {
        return new Color(color).getAlpha();
    }

    public static int rainbow(int delay, long index) {
        double rainbowState = Math.ceil(System.currentTimeMillis() + index + (long)delay) / 15.0;
        return Color.getHSBColor((float)((rainbowState %= 360.0) / 360.0), 0.4f, 1.0f).getRGB();
    }

    public static Color rainbow(final int delay, final float s, final float b) {
        return Color.getHSBColor((System.currentTimeMillis() + delay) % 11520L / 11520.0f, s, b);
    }

    public static int getColor(int brightness) {
        return ColorUtils.getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(int brightness, int alpha) {
        return ColorUtils.getColor(brightness, brightness, brightness, alpha);
    }

    public static void glColor(final int hex, final int alpha) {
        final float red = (hex >> 16 & 0xFF) / 255F;
        final float green = (hex >> 8 & 0xFF) / 255F;
        final float blue = (hex & 0xFF) / 255F;

        GlStateManager.color(red, green, blue, alpha / 255F);
    }
}
