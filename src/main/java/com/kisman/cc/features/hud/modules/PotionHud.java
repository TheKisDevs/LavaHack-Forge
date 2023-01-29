package com.kisman.cc.features.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.client.collections.Bind;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ConstantConditions")
public class PotionHud extends HudModule {
    private final Setting offsets = register(new Setting("Offsets", this, 2, 0, 10, true));
    private final SettingGroup sortGroup = register(new SettingGroup(new Setting("Sort", this)));
    private final Setting sort = register(sortGroup.add(new Setting("Sort", this, true)));
    private final Setting sortMode = register(sortGroup.add(new Setting("Sort Mode", this, "Alphabet", Arrays.asList("Alphabet", "Length", "Duration")).setVisible(sort).setTitle("Mode")));
    private final Setting sortReverse = register(sortGroup.add(new Setting("Sort Reverse", this, false).setVisible(sort).setTitle("Reverse")));
    private final Setting sliders = register(new Setting("Sliders", this, false));
    private final Setting alpha = register(new Setting("Alpha", this, 255, 0, 255, true));

    private final HashMap<String, Bind<PotionEffect, Integer>> potions = new HashMap<>();

    public PotionHud(){
        super("PotionHud", "oh god", true);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(receive);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(receive);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> receive = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketEntityEffect) {
            SPacketEntityEffect packet = (SPacketEntityEffect) event.getPacket();
            if(mc.player.entityId == packet.getEntityId()) {
                PotionEffect effect = new PotionEffect(
                        Potion.getPotionById(packet.getEffectId()),
                        packet.getDuration(),
                        packet.getAmplifier()
                );

                if(potions.containsKey(effect.getEffectName())) {
                    Bind<PotionEffect, Integer> pair = potions.get(effect.getEffectName());
                    pair.getFirst().combine(effect);
                    potions.put(effect.getEffectName(), pair);
                } else potions.put(effect.getEffectName(), new Bind<>(effect, effect.getDuration()));
            }
        } else if(event.getPacket() instanceof SPacketRemoveEntityEffect) {
            SPacketRemoveEntityEffect packet = (SPacketRemoveEntityEffect) event.getPacket();
            if(mc.player == packet.getEntity(mc.world)) potions.remove(packet.getPotion().getName());
        }
    });

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event){
        int height = offsets.getValInt() + CustomFontUtil.getFontHeight() + (sliders.getValBoolean() ? offsets.getValInt() : 0);
        int w = 0;
        int count = 0;

        List<PotionEffect> effects;
        if(sort.getValBoolean()){
            effects = mc.player.getActivePotionEffects().stream().sorted(((sortReverse.getValBoolean() ? getComparator().reversed() : getComparator()))).collect(Collectors.toList());
        } else {
            effects = new ArrayList<>(mc.player.getActivePotionEffects());
        }

        for(PotionEffect effect : effects) {
            String string = I18n.format(effect.getEffectName()) + (effect.getAmplifier() > 0 ? " " + effect.getAmplifier() : "") + Potion.getPotionDurationString(effect, 1);
            w = Math.max(w, CustomFontUtil.getStringWidth(string));
        }

        setW(w + (sliders.getValBoolean() ? 10 : 0));

        for(PotionEffect effect : effects) {
            int y = (int) (getY() + count * height);
            boolean flag = false;

            if(sliders.getValBoolean() && potions.containsKey(effect.getEffectName())) {
                double sliderWidth = (getW() + 10) * ((double) potions.get(effect.getEffectName()).getFirst().getDuration() / (double) potions.get(effect.getEffectName()).getSecond());

                Render2DUtil.drawRectWH(getX(), y - offsets.getValInt(), sliderWidth, offsets.getValInt() * 2 + CustomFontUtil.getFontHeight(), ColorUtils.injectAlpha(effect.getPotion().getLiquidColor(), alpha.getValInt()).getRGB());

                flag = true;
            }
            CustomFontUtil.drawStringWithShadow(format(effect), getX() + (flag ? 5 : 0), y, flag ? -1 : effect.getPotion().getLiquidColor());
            count++;
        }

        setH(count * height);
    }

    private String format(PotionEffect effect) {
        return I18n.format(effect.getEffectName()) + (effect.getAmplifier() > 0 ? " " + effect.getAmplifier() : "") + TextFormatting.GRAY + ": " + Potion.getPotionDurationString(effect, 1);
    }

    private Comparator<PotionEffect> getComparator(){
        if(sortMode.getValString().equals("Alphabetical")) return Comparator.comparing(PotionEffect::getEffectName);
        if(sortMode.getValString().equals("Length")) return Comparator.comparingInt(effect -> (I18n.format(effect.getEffectName()) + (effect.getAmplifier() > 0 ? " " + effect.getAmplifier() : "") + Potion.getPotionDurationString(effect, 1)).length());
        return Comparator.comparingInt(PotionEffect::getDuration);
    }
}
