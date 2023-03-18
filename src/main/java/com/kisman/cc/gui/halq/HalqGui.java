package com.kisman.cc.gui.halq;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.KismanGuiScreen;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.ModuleComponent;
import com.kisman.cc.gui.api.Openable;
import com.kisman.cc.gui.api.SettingComponent;
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation;
import com.kisman.cc.gui.halq.components.Description;
import com.kisman.cc.gui.halq.components.Header;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.gui.particle.ParticleSystem;
import com.kisman.cc.gui.selectionbar.SelectionBar;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.UtilityKt;
import com.kisman.cc.util.enums.dynamic.EasingEnum;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author made by _kisman_ for Halq with love <3
 */
public class HalqGui extends KismanGuiScreen {
    public SearchBar searchBar = new SearchBar();


    public static LocateMode stringLocateMode = LocateMode.Left;
    public static EasingEnum.Easing animationEasing = EasingEnum.Easing.Curve;
    public static Colour primaryColor = new Colour(Color.RED);
    public static Colour backgroundColor = new Colour(30, 30, 30, 121);
    public static Colour outlineColor = new Colour(Color.BLACK);
    public static Colour test2Color = new Colour(30, 30, 30, 121);
    public static Colour hoverColor = new Colour(255, 255, 255, 60);
    public static boolean background = true,
            line = true,
            shadow = true,
            hideAnnotations = false,
            test = true,
            shadowRects = false,
            test2 = true,
            //Outline fields:
            componentsOutline = false,
            outlineTest = true,
            outlineTest2 = true,
            outlineHeaders = false,
            shaderState = false,
            animationState = true,
            animateToggleable = true,
            animateHover = false,
            animateSlider = false,
            animationDirection = true,
            animationReverseDirection = false,
            animationAlpha = false,
            animationBothSide = false,
            openIndicator = true,
            searchSettings = false;
    public static int diff = 0,
            textOffsetX = 5,
            gradientFrameDiff = 0,
            animationSpeed = 750,
            layerStepOffset = 5;
    public static double offsetsX = 0,
            offsetsY = 0,
            lineWidth = 1.0,
            scale = 1;
    public static float ticks = 0;

    /**
     * These aren't quite constants anymore.
     * You should IN NO CASE modify these besides
     * in the method where there values get updated
     * from the Gui module, which is the
     * {@link HalqGui#drawScreen(int, int, float)}
     * method. In no other place should these be
     * changed EVER. I just wanted to make this
     * very clear.
     * - Cubic
     */
    public static int height = GuiModule.instance.componentHeight.getValInt(); //13;
    public static int headerOffset = GuiModule.instance.headerOffset.getValInt(); //5;
    public static final int width = 120;

    //frames list
    public final ArrayList<Frame> frames = new ArrayList<>();

    //particles
    public ParticleSystem particleSystem;

    /**
     * {@link com.kisman.cc.gui.mainmenu.gui.KismanMainMenuGui}
     */
    private GuiScreen lastGui = null;

    public static Component currentComponent = null;
    public static Description currentDescription = null;
    public static int mouseX = -1;
    public static int mouseY = -1;

    private static Runnable shaderableThing = () -> {};
    private static Runnable postRenderThing = () -> {};

    public HalqGui(GuiScreen lastGui) {
        this();
        this.lastGui = lastGui;
    }

    public HalqGui setLastGui(GuiScreen gui) {
        this.lastGui = gui;
        return this;
    }

    public HalqGui(GuiScreen lastGui, boolean notFullInit) {
        this(notFullInit);
        setLastGui(lastGui);
    }

    public HalqGui(@SuppressWarnings("unused") boolean notFullInit) {
        this.particleSystem = new ParticleSystem();
    }

    public HalqGui() {
        this(true);
        int offsetX = 5;
        int count = 0;
        for(Category cat : Category.values()) {
            frames.add(new Frame(cat, offsetX, 20, count));
            offsetX += width + 5;
            count++;
        }
    }

    public void init() {
        SelectionBar.Guis.ClickGui.getOpen0().invoke();
        Kisman.instance.selectionBar.setReinit(true);
    }

    @Override
    public void initGui() {
        super.initGui();
        particleSystem = new ParticleSystem();
    }

    protected SelectionBar.Guis gui() {
        return SelectionBar.Guis.ClickGui;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ticks = partialTicks;

        if(Kisman.instance.selectionBar.getSelection() != gui()) {
            Kisman.instance.selectionBar.open();
            return;
        }

        primaryColor = GuiModule.instance.primaryColor.getColour();
        background = GuiModule.instance.background.getValBoolean();
        height = GuiModule.instance.componentHeight.getValInt();
        headerOffset = GuiModule.instance.headerOffset.getValInt();
        shadow = GuiModule.instance.shadow.getValBoolean();
        hideAnnotations = GuiModule.instance.hideAnnotations.getValBoolean();
        test = GuiModule.instance.horizontalLines.getValBoolean();
        shadowRects = GuiModule.instance.shadowRects.getValBoolean();
        line = GuiModule.instance.verticalLines.getValBoolean();
        diff = Config.instance.guiGradientDiff.getValInt();
        offsetsX = GuiModule.instance.offsetsX.getValDouble();
        offsetsY = GuiModule.instance.offsetsY.getValDouble();
        stringLocateMode = (LocateMode) GuiModule.instance.uwu.getValEnum();
        test2 = GuiModule.instance.test2.getValBoolean();
        test2Color = GuiModule.instance.test2Color.getColour();
        textOffsetX = GuiModule.instance.textOffsetX.getValInt();
        outlineColor = GuiModule.instance.outlineColor.getColour();
        outlineTest = GuiModule.instance.outlineTest.getValBoolean();
        outlineTest2 = GuiModule.instance.outlineTest2.getValBoolean();
        outlineHeaders = GuiModule.instance.outlineHeaders.getValBoolean();
        componentsOutline = GuiModule.instance.componentsOutline.getValBoolean();
        lineWidth = GuiModule.instance.lineWidth.getValDouble();
        hoverColor = GuiModule.instance.hoverColor.getColour();
        shaderState = GuiModule.instance.shaderState.getValBoolean();
        gradientFrameDiff = GuiModule.instance.gradientFrameDiff.getValInt();
        animationState = GuiModule.instance.animationState.getValBoolean();
        animationSpeed = GuiModule.instance.animationSpeed.getValInt();
        animationEasing = GuiModule.instance.animationEasing.getValEnum();
        animationReverseDirection = GuiModule.instance.animationReverseDirection.getValBoolean();
        animateToggleable = GuiModule.instance.animateToggleable.getValBoolean();
        animateHover = GuiModule.instance.animateHover.getValBoolean();
        animateSlider = GuiModule.instance.animateSlider.getValBoolean();
        openIndicator = GuiModule.instance.openIndicator.getValBoolean();
        layerStepOffset = GuiModule.instance.layerStepOffset.getValInt();
        scale = GuiModule.instance.scale.getValDouble();
        animationDirection = GuiModule.instance.animationDirection.getValBoolean();
        animationAlpha = GuiModule.instance.animationAlpha.getValBoolean();
        animationBothSide = GuiModule.instance.animationBothSide.getValBoolean();
        searchSettings = GuiModule.instance.searchSettings.getValBoolean();

        mouseX /= scale;
        mouseY /= scale;

        HalqGui.mouseX = mouseX;
        HalqGui.mouseY = mouseY;

        if(!background) backgroundColor = new Colour(0, 0, 0, 0);
        else backgroundColor = GuiModule.instance.backgroundColor.getColour();

        drawDefaultBackground();
        drawScreenPre();

        if(Config.instance.guiParticles.getValBoolean()) {
            particleSystem.tick(10);
            particleSystem.render();
            particleSystem.onUpdate();
        }

        scrollWheelCheck();

        shaderableThing = () -> {};
        postRenderThing = () -> {};
        currentDescription = null;

        GL11.glPushMatrix();
        GL11.glScaled(scale, scale, 1);

        for(Frame frame : frames) {
            if(frame.reloading) continue;

            frame.render(mouseX, mouseY);

            if(frame.open) for(Component comp : frame.components) if(comp.visible()) {
                comp.updateComponent(frame.x, frame.y);
                drawComponent(comp);
            }

            frame.refresh();
        }

        if(shaderState) {
            GuiModule.instance.shaders.start();

            shaderableThing.run();

            if(currentDescription != null) currentDescription.drawScreen(mouseX, mouseY);

            GuiModule.instance.shaders.end();


            postRenderThing.run();
        }

        GL11.glPopMatrix();

        drawSelectionBar(mouseX, mouseY);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if(keyCode == 1) mc.displayGuiScreen(lastGui == null ? null : lastGui);
        super.keyTyped(typedChar, keyCode);
        for(Frame frame : frames) if(frame.open && keyCode != 1 && !frame.components.isEmpty() && !frame.reloading) for(Component mod : frame.components) if(mod.visible()) mod.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
//        if(!Kisman.instance.selectionBar.mouseClicked(mouseX, mouseY)) return;
        mouseX /= scale;
        mouseY /= scale;

        super.mouseClicked(mouseX, mouseY, mouseButton);
        for(Frame frame : frames) {
            if(frame.reloading) continue;
            if(frame.isMouseOnButton(mouseX, mouseY)) {
                if(mouseButton == 0) {
                    frame.dragging = true;
                    frame.dragX = mouseX - frame.x;
                    frame.dragY = mouseY - frame.y;
                }
                else if(mouseButton == 1) frame.open = !frame.open;
            }
            if(frame.open && !frame.components.isEmpty()) for(Component mod : frame.components) if(mod.visible()) mod.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        mouseX /= scale;
        mouseY /= scale;

        for(Frame frame : frames) {
            if(frame.reloading) continue;
            frame.dragging = false;
            if(frame.open && !frame.components.isEmpty()) for(Component mod : frame.components) if(mod.visible()) mod.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void onGuiClosed() {
        try {if(mc.player != null && mc.world != null) mc.entityRenderer.getShaderGroup().deleteShaderGroup();} catch (Exception ignored) {}
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    public static void drawStringRightSide(String text, int x, int y, int width, int height) {
        GL11.glPushMatrix();

        CustomFontUtil.drawStringWithShadow(text, x + width - textOffsetX - CustomFontUtil.getStringWidth(text), y + (double) height / 2 - (double) CustomFontUtil.getFontHeight() / 2, -1);

        GL11.glPopMatrix();
    }

    public static void drawString(String text, int x, int y, int width, int height) {
        if(currentComponent != null && mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height) Render2DUtil.drawRectWH(x + offsetsX, y + HalqGui.offsetsY, width - HalqGui.offsetsX * 2, height - HalqGui.offsetsY * 2, HalqGui.hoverColor.getRGB());

        GL11.glPushMatrix();

        switch (stringLocateMode) {
            case Center:
                CustomFontUtil.drawCenteredStringWithShadow(text, x + (double) width / 2, y + (double) height / 2 - (double) CustomFontUtil.getFontHeight() / 2, -1);
                break;
            case Left:
                CustomFontUtil.drawStringWithShadow(text, x + textOffsetX, y + (double) height / 2 - (double) CustomFontUtil.getFontHeight() / 2, -1);
                break;
        }

        GL11.glPopMatrix();
    }

    public static void drawSuffix(String suffix, String parentText, double x, double y, double width, double height, Colour colour, int step) {
        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 1);

        switch (stringLocateMode) {
            case Center:
                switch(step) {
                    case 1 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (float) ((x + width / 2) + CustomFontUtil.getStringWidth(parentText)), (float) y * 2, colour.getRGB());
                        break;
                    case 2 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (float) ((x + width / 2) + CustomFontUtil.getStringWidth(parentText)), (float) (y + (height / 2) - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f / 2)) * 2f, colour.getRGB());
                        break;
                    case 3 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (float) ((x + width / 2) + CustomFontUtil.getStringWidth(parentText)), (float) (y + height - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f)) * 2, colour.getRGB());
                        break;
                }
                break;
            case Left:
                switch(step) {
                    case 1 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (float) (x + CustomFontUtil.getStringWidth(parentText) + textOffsetX) * 2, (float) y * 2, colour.getRGB());
                        break;
                    case 2 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (float) (x + CustomFontUtil.getStringWidth(parentText) + textOffsetX) * 2, (float) (y + (height / 2) - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f / 2f)) * 2f, colour.getRGB());
                        break;
                    case 3 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (float) (x + CustomFontUtil.getStringWidth(parentText) + textOffsetX) * 2, (float) (y + height - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f)) * 2, colour.getRGB());
                        break;
                }
                break;
        }

        GL11.glPopMatrix();
    }

    public static void drawSuffix(String suffix, String parentText, double x, double y, double width, double height, int count, int step) {
        drawSuffix(
                suffix,
                parentText,
                x,
                y,
                width,
                height,
                HalqGui.getGradientColour(count),
                step
        );
    }

    public static void drawCenteredString(String text, int x, int y, int width, int height) {
        CustomFontUtil.drawCenteredStringWithShadow(text, x + (double) width / 2, y + (double) height / 2 - (double) CustomFontUtil.getFontHeight() / 2, -1);
    }

    public static Colour getGradientColour(int count) {
        switch(Config.instance.guiGradient.getValString()) {
            case "None": return primaryColor;
            case "Rainbow": return new Colour(ColorUtils.rainbow(count * diff, 1, 1));
            case "Astolfo": return new Colour(ColorUtils.getAstolfoRainbow(count * diff));
            case "Pulsive": return ColorUtils.twoColorEffect(primaryColor, primaryColor.setBrightness(0.25f), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 6.0 * (count * diff) / 60.0);
        }
        return primaryColor;
    }

    private void scrollWheelCheck() {
        int dWheel = Mouse.getDWheel();
        if(dWheel < 0) for(Frame frame : frames) {
            if(frame.reloading) continue;
            if(Config.instance.horizontalScroll.getValBoolean() && Keyboard.getEventKeyState() && Keyboard.getEventKey() == Config.instance.keyForHorizontalScroll.getKey()) frame.x = frame.x - (int) Config.instance.scrollSpeed.getValDouble();
            else frame.y = frame.y - (int) Config.instance.scrollSpeed.getValDouble();
        } else if(dWheel > 0) for(Frame frame : frames) {
            if(frame.reloading) continue;
            if(Config.instance.horizontalScroll.getValBoolean() && Keyboard.getEventKeyState() && Keyboard.getEventKey() == Config.instance.keyForHorizontalScroll.getKey()) frame.x = frame.x + (int) Config.instance.scrollSpeed.getValDouble();
            else frame.y = frame.y + (int) Config.instance.scrollSpeed.getValDouble();
        }
     }

    public static void renderComponent(
            Component component
    ) {
        if(!component.visible()) return;

        if(line) {
            Render2DUtil.drawRectWH(
                    component.getX(),
                    component.getY(),
                    1,
                    component.getRawHeight(),
                    getGradientColour(component.getCount()).getRGB()
            );
            Render2DUtil.drawRectWH(
                    component.getX() + LayerControllerKt.getModifiedWidth(component.getLayer(), HalqGui.width) - 1,
                    component.getY(),
                    1,
                    component.getRawHeight(),
                    getGradientColour(component.getCount()).getRGB()
            );
        }

        if(component instanceof Openable) {
            Openable openable = (Openable) component;

            if(test && openable.isOpen() && !openable.getComponents().isEmpty()) {
                Render2DUtil.drawRectWH(
                        component.getX(),
                        component.getY() + component.getRawHeight(),
                        LayerControllerKt.getModifiedWidth(component.getLayer(), HalqGui.width),
                        1,
                        getGradientColour(openable.getComponents().get(0).getCount()).getRGB()
                );

                int height = doIterationUpdateComponent(
                        openable.getComponents(),
                        0
                );

                Render2DUtil.drawRectWH(
                        component.getX(),
                        component.getY() + component.getRawHeight() + height - 1,
                        LayerControllerKt.getModifiedWidth(component.getLayer(), HalqGui.width),
                        1,
                        getGradientColour(openable.getComponents().get(openable.getComponents().size() - 1).getCount()).getRGB()
                );
            }
        }
    }

    public static int doIterationUpdateComponent(
            ArrayList<Component> components,
            int height
    ) {
        for(Component component : components) {
            if(!component.visible()) continue;
            height += component.getRawHeight();
            if(component instanceof Openable) {
                Openable openable = (Openable) component;
                if(openable.isOpen()) height = doIterationUpdateComponent(openable.getComponents(), height);
            }
        }

        return height;
    }

    public static void drawComponentOutline(Component component, boolean offsetsCheck, boolean drawSecondLines, boolean full) {
        if(componentsOutline) {
            int width0 = LayerControllerKt.getModifiedWidth(component.getLayer(), width);

            if(!full && (offsetsX > 0 || offsetsY > 0 || !offsetsCheck)) {
                double width1 = width0 - (offsetsX * 2);

                Render2DUtil.drawRectWH(//h
                        component.getX() + offsetsX,
                        component.getY() + offsetsY,
                        width1,
                        lineWidth,
                        outlineColor.getRGB()
                );
                Render2DUtil.drawRectWH(//h
                        component.getX() + offsetsX,
                        component.getY() + offsetsY + component.getRawHeight() - (offsetsY * 2) - lineWidth,
                        width1,
                        lineWidth,
                        outlineColor.getRGB()
                );
                Render2DUtil.drawRectWH(//v
                        component.getX() + offsetsX,
                        component.getY() + offsetsY,
                        lineWidth,
                        component.getRawHeight() - (offsetsY * 2),
                        outlineColor.getRGB()
                );
                Render2DUtil.drawRectWH(//v
                        component.getX() + offsetsX + width1 - lineWidth,
                        component.getY() + offsetsY,
                        lineWidth,
                        component.getRawHeight() - (offsetsY * 2),
                        outlineColor.getRGB()
                );
            }

            if(drawSecondLines) {
                if(full) {
                    Render2DUtil.drawRectWH(//h
                            component.getX(),
                            component.getY(),
                            width0,
                            lineWidth,
                            outlineColor.getRGB()
                    );
                    Render2DUtil.drawRectWH(//h
                            component.getX(),
                            component.getY() + component.getRawHeight() - lineWidth,
                            width0,
                            lineWidth,
                            outlineColor.getRGB()
                    );
                }

                Render2DUtil.drawRectWH(//v
                        component.getX(),
                        component.getY(),
                        lineWidth,
                        component.getRawHeight(),
                        outlineColor.getRGB()
                );
                Render2DUtil.drawRectWH(//v
                        component.getX() + width0 - lineWidth,
                        component.getY(),
                        lineWidth,
                        component.getRawHeight(),
                        outlineColor.getRGB()
                );
            }
        }
    }

    public static boolean visible(String name) {
        return Kisman.instance.halqGui.searchBar.text().isEmpty() || name.toLowerCase().contains(Kisman.instance.halqGui.searchBar.text().toLowerCase());
    }

    public static boolean visible(Component component) {
        if(Kisman.instance.halqGui.searchBar.text().isEmpty()) return true;

        if(component instanceof Openable) {
            if(component instanceof ModuleComponent && ((Openable) component).isOpen()) for(Component component0 : ((Openable) component).getComponents()) if(component0 instanceof Button && component0.visible()) return true;
        } else if(component instanceof SettingComponent && searchSettings) {
            return visible(((SettingComponent) component).setting().displayName);
        }

        return false;
    }

    public static void drawComponent(Component component) {
        component.drawScreen(mouseX, mouseY);

        boolean flag = !(component instanceof Header) && !(component instanceof Description);

        if(component instanceof ShaderableImplementation) {
            ShaderableImplementation shaderable = (ShaderableImplementation) component;

            shaderable.normalRender().run();

            if(shaderState) {
                addShaderRunnable(shaderable.shaderRender().getFirst());
                addPostRenderRunnable(shaderable.shaderRender().getSecond());

                if(flag) {
                    addShaderRunnable(() -> renderComponent(component));
                    addPostRenderRunnable(() -> drawComponentOutline(component, true, HalqGui.outlineTest, false));
                }
            } else {
                shaderable.shaderRender().getFirst().run();
                shaderable.shaderRender().getSecond().run();

                if(flag) {
                    renderComponent(component);
                    drawComponentOutline(component, true, HalqGui.outlineTest, false);
                }
            }
        }
    }

    private static void addShaderRunnable(Runnable runnable) {
        shaderableThing = UtilityKt.compare(shaderableThing, runnable);
    }

    private static void addPostRenderRunnable(Runnable runnable) {
        postRenderThing = UtilityKt.compare(postRenderThing, runnable);
    }

    public static void drawRectWH(double x, double y, double w, double h, int color, double coeff, boolean state) {
        //TODO: vertical animation
        boolean flag = false;

        if(animationState) {
            if (animationAlpha) {
                Colour color0 = new Colour(color);
                color = color0.withAlpha((int) (coeff * color0.a)).getRGB();
                flag = true;
            }

            if (animationDirection) {
                if (animationReverseDirection || animationBothSide) Render2DUtil.drawRectWH(x + w - (w * coeff), y, w * coeff, h, color);
                if (!animationReverseDirection || animationBothSide) Render2DUtil.drawRectWH(x, y, w * coeff, h, color);

                return;
            }
        }

        Render2DUtil.drawRectWH(x, y, w, h, flag ? color : new Colour(color).withAlpha(state ? 255 : 0).getRGB());
    }

    public enum LocateMode {
        Center, Left
    }
}
