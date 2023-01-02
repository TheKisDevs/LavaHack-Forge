package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.math.MathUtil;
import com.kisman.cc.util.render.Rendering;
import com.kisman.cc.util.render.cubicgl.CubicGL;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class RectTest extends Module {

    private final Setting mode = register(new Setting("Mode", this, Modes.Linear));
    private final Setting speed = register(new Setting("Speed", this, 20, 1, 100, true));

    private final int x2 = Display.getWidth() - 30;
    private final int y2 = Display.getHeight() - 30;
    private final int x1 = Display.getWidth() - 300;
    private int y1 = Display.getHeight() - 300;

    private volatile boolean canDraw = false;

    private volatile long start;

    public RectTest(){
        super("RectTest", Category.DEBUG);
    }

    @SubscribeEvent
    public void onBlockHit(PlayerInteractEvent.LeftClickBlock event){
        if(mc.player == null || mc.world == null)
            return;

        if(!isToggled())
            return;

        y1 -= 200;
        start = System.currentTimeMillis();
        canDraw = true;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event){
        if(mc.player == null || mc.world == null)
            return;

        if(!isToggled())
            return;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        Rendering.start();
//        Rendering.prepare();
        boolean original = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        scissors();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(x1, y1, 0);
        bufferbuilder.pos(x2, y1, 0);
        bufferbuilder.pos(x2, y2, 0);
        bufferbuilder.pos(x1, y2, 0);
        tessellator.draw();
        if(!original)
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        Rendering.end();
    }

    private void scissors(){
        if(!canDraw)
            return;

        double cur = System.currentTimeMillis() - start;
        double progress = cur / (speed.getValInt() * 50.0);

        if(progress > 1.0){
            canDraw = false;
            return;
        }

        CubicGL.scissors(x2, y2, x1, (int) (y1 + 200.0 - (progress * 200.0)));
    }

    private double mutateProgress(double progress){
        switch ((Modes) mode.getValEnum()){
            case Curve:
                return MathUtil.curve(progress);
            case Sin:
                return Math.sin(progress * (Math.PI / 2.0));
        }
        return progress;
    }

    enum Modes {
        Linear,
        Curve,
        Sin
    }
}
