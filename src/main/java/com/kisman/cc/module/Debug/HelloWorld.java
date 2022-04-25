package com.kisman.cc.module.Debug;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;

public class HelloWorld extends Module {
    public HelloWorld() {
        super("HelloWorld", "Test module, print \"Hello, World\" into the chat every 5 seconds", Category.DEBUG);
    }

    @Override
    public void onEnable()
    {
        super.onEnable();

        if(mc.player == null || mc.world == null) return;
        mc.player.sendMessage(new TextComponentString("§2[" + getName() + "]:§f  " + "Debug module enabled"));
    }

    @Override
    public void onDisable()
    {
        super.onDisable();

        if(mc.player == null || mc.world == null) return;
        mc.player.sendMessage(new TextComponentString("§2[" + getName() + "]:§f  " + "Debug module disabled"));
    }

    @Override
    public void update()
    {
        if(mc.player == null || mc.world == null) return;

        counter++;
        if (counter > 20 * 5)
        {
            mc.player.sendMessage(new TextComponentString("§2[" + getName() + "]:§f  " + "Hello, World!"));
            counter = 0;
        }
    }

    private int counter = 0;
}
