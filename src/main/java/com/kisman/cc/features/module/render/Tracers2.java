package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.RenderEntitiesEvent;
import com.kisman.cc.event.events.RenderEntityEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.util.TracersPattern;
import me.zero.alpine.listener.Listener;

public class Tracers2 extends Module {
    private final TracersPattern pattern = new TracersPattern(this).preInit().init();

    public Tracers2(){
        super("Tracers", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(renderEntityCheck);
        Kisman.EVENT_BUS.subscribe(renderEntitiesStart);
        Kisman.EVENT_BUS.subscribe(renderEntitiesEnd);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(renderEntityCheck);
        Kisman.EVENT_BUS.unsubscribe(renderEntitiesStart);
        Kisman.EVENT_BUS.unsubscribe(renderEntitiesEnd);
    }

    private final Listener<RenderEntitiesEvent.Start> renderEntitiesStart = new Listener<>(event -> pattern.preRenderEntities());
    private final Listener<RenderEntityEvent.Check> renderEntityCheck = new Listener<>(event -> pattern.renderEntity(event.getEntity()));
    private final Listener<RenderEntitiesEvent.End> renderEntitiesEnd = new Listener<>(event -> pattern.postRenderEntities());
}
