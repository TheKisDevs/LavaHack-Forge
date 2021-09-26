package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.BlockUtil;
import com.kisman.cc.util.PlayerUtil;
import com.kisman.cc.util.RenderUtil;
import i.gishreloaded.gishcode.utils.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class Surround extends Module {
    private BlockPos[][] blocks;

    private boolean render;
    private boolean toggled;

    public Surround() {
        super("Surround", "Surround", Category.COMBAT);

        this.blocks = PlayerUtil.SurroundBlockPos();

        Kisman.instance.settingsManager.rSetting(new Setting("PlaceMode", this, "Simple", new ArrayList<>(Arrays.asList("Simple", "Legit", "Packet"))));

        Kisman.instance.settingsManager.rSetting(new Setting("Render", this, false));
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        String placeMode = Kisman.instance.settingsManager.getSettingByName(this, "PlaceMode").getValString();

        this.render = Kisman.instance.settingsManager.getSettingByName(this, "Render").getValBoolean();
        this.blocks = PlayerUtil.SurroundBlockPos();

        //start

        this.toggled = true;

        if(BlockUtils.getBlock(this.blocks[0][1]) != Blocks.AIR) {
            if(placeMode.equalsIgnoreCase("Single")) {
                BlockUtils.placeBlockSimple(this.blocks[0][1]);
            }
        }

        //complete

        this.toggled = false;
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if(this.toggled) {
            RenderUtil.drawBlockESP(this.blocks[0][1], 1, 0, 0);
        }
    }
}
