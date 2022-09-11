package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Don't use this, this doesn't work
 */
public class LocateCommand extends Command {

    public LocateCommand() {
        super("locate");
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        if(args.length < 1){
            ChatUtility.error().printClientMessage("[Locate] To few arguments");
            return;
        }

        if(mc.isSingleplayer()){
            ChatUtility.warning().printClientMessage("[Locate] You are in single player");
            return;
        }

        WorldServer worldServer = DimensionManager.getWorld(0);
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "FakePlayer");
        FakePlayer fakePlayer = new FakePlayer(worldServer, gameProfile);
        MinecraftServer server = fakePlayer.mcServer;
        if(server == null){
            ChatUtility.warning().printClientMessage("[Locate] Server is null");
            return;
        }
        EntityPlayer player = server.getPlayerList().getPlayers().stream().filter(entityPlayerMP -> entityPlayerMP.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if(player == null){
            ChatUtility.warning().printClientMessage("[Locate] Could not locate " + args[0]);
            return;
        }
        ChatUtility.complete().printClientMessage("[Locate] The location of " + player.getName() + " is: "
                + " x: " + player.posX + ", y: " + player.posY + ", z:" + player.posZ);
    }
}
