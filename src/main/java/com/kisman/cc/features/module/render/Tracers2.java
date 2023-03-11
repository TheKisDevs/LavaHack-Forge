package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventUpdateEntities;
import com.kisman.cc.event.events.EventUpdateEntity;
import com.kisman.cc.event.events.RenderEntitiesEvent;
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
        Kisman.EVENT_BUS.subscribe(updateEntity);
        Kisman.EVENT_BUS.subscribe(updateEntitiesPre);
        Kisman.EVENT_BUS.subscribe(updateEntitiesPost);
        Kisman.EVENT_BUS.subscribe(renderEntitiesEnd);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(updateEntity);
        Kisman.EVENT_BUS.unsubscribe(updateEntitiesPre);
        Kisman.EVENT_BUS.unsubscribe(updateEntitiesPost);
        Kisman.EVENT_BUS.unsubscribe(renderEntitiesEnd);
    }

    private final Listener<EventUpdateEntities.Pre> updateEntitiesPre = new Listener<>(event -> pattern.preUpdateEntities());
    private final Listener<EventUpdateEntity> updateEntity = new Listener<>(event -> pattern.updateEntity(event.getEntity()));
    private final Listener<EventUpdateEntities.Post> updateEntitiesPost = new Listener<>(event -> pattern.postUpdateEntities());
    private final Listener<RenderEntitiesEvent.End> renderEntitiesEnd = new Listener<>(event -> pattern.postRenderEntities());
}
