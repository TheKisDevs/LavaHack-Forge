package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;

public class JumpCircle extends Module {
    public JumpCircle() {
        super("JumpCircle", Category.RENDER);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        Cylinder circle = new Cylinder();

        circle.setDrawStyle(GLU.GLU_FILL);
//        circle.draw();
    }
}
