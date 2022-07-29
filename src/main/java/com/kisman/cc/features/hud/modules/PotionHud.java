package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.actors.threadpool.Arrays;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PotionHud extends HudModule {

    private final Setting offsets = register(new Setting("Offsets", this, 2, 0, 10, true));
    private final Setting sort = register(new Setting("Sort", this, true));
    private final Setting sortMode = register(new Setting("SortMode", this, "Alphabetical", new ArrayList<>(Arrays.asList(new String[]{"Alphabetical", "Length", "PotionDuration"}))));

    public PotionHud(){
        super("PotionHud", "oh god", true);
        setX(1);
        setY(1);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event){

        if(mc.player == null || mc.world == null)
            return;

        int height = offsets.getValInt() + CustomFontUtil.getFontHeight();
        int w = 0;
        int count = 0;

        List<PotionEffect> effects;
        if(sort.getValBoolean()){
            effects = mc.player.getActivePotionEffects().stream().sorted((getComparator())).collect(Collectors.toList());
        } else {
            effects = new ArrayList<>(mc.player.getActivePotionEffects());
        }

        for(PotionEffect effect : effects){
            String name = effect.getEffectName();
            int duration = effect.getDuration();
            int amplifier = effect.getAmplifier();
            String str = fmt(name, duration, amplifier);
            CustomFontUtil.drawStringWithShadow(str, getX(), getY(), Color.WHITE.getRGB());
            if(name.length() > w)
                w = name.length();
            count++;
        }

        setH(count * height);
    }

    private String fmt(String name, int duration, int amplifier){
        return name + " " + amplifier + " (" + duration + ")";
    }

    private Comparator<PotionEffect> getComparator(){
        if(sortMode.getValString().equals("Alphabetical")){
            return Comparator.comparing(PotionEffect::getEffectName);
        }
        if(sortMode.getValString().equals("Length")){
            return Comparator.comparingInt(o -> o.getEffectName().length());
        }
        return Comparator.comparingInt(PotionEffect::getDuration);
    }

    /*
    private final static class SortEnum {

        private static final AbstractTask.DelegateAbstractTask<Comparator<PotionEffect>> task = AbstractTask.typesResolve();

        private enum SortModes {
            Alphabetical(task.task(args -> Comparator.comparing(PotionEffect::getEffectName))),
            Length(task.task(args -> Comparator.comparingInt(o -> o.getEffectName().length()))),
            PotionDuration(task.task(args -> Comparator.comparingInt(PotionEffect::getDuration)));

            private final AbstractTask<Comparator<PotionEffect>> abstractTask;

            SortModes(AbstractTask<Comparator<PotionEffect>> task){
                this.abstractTask = task;
            }

            private AbstractTask<Comparator<PotionEffect>> getTask(){
                return this.abstractTask;
            }
        }
    }
     */
}
