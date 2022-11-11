package com.kisman.cc.util.world.generation;

import com.kisman.cc.Kisman;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.Util;
import net.minecraft.world.*;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * @author TudbuT
 */
@SideOnly(Side.CLIENT)
public class CustomServerProvider extends MinecraftServer {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private final WorldSettings worldSettings;

    public boolean done = false;

    private boolean isGamePaused;

    public CustomServerProvider(
            Minecraft client,
            WorldSettings worldSettings,
            YggdrasilAuthenticationService authService,
            MinecraftSessionService sessionService,
            GameProfileRepository profileRepo,
            PlayerProfileCache profileCache
    ) {
        super(new File(Kisman.fileName + "server"), client.getProxy(), client.getDataFixer(), authService, sessionService, profileRepo, profileCache);

        try {
            FileUtils.deleteDirectory(new File(Kisman.fileName + "server/main"));
        } catch (IOException e) {
            Kisman.LOGGER.error("[CustomServerProvider] Can not delete directory: " + (new File(Kisman.fileName + "server/main")));
            throw new IllegalStateException(e);
        }

        setServerOwner("_kisman_");
        setFolderName("main");
        setWorldName("main");
        setDemo(client.isDemo());
        canCreateBonusChest(worldSettings.isBonusChestEnabled());
        setBuildLimit(256);
        setPlayerList(new PlayerList(this) {
        });
        this.worldSettings = isDemo() ? WorldServerDemo.DEMO_WORLD_SETTINGS : worldSettings;
    }

    @NotNull
    @Override
    public ServerCommandManager createCommandManager(){
        return new ServerCommandManager(this);
    }

    public void loadAllWorld(String saveName, String worldNameIn, long seed, WorldType type, String generatorOptions){
        convertMapIfNeeded(saveName);
        ISaveHandler saveHandler = getActiveAnvilConverter().getSaveLoader(saveName, true);
        setResourcePackFromWorld(getFolderName(), saveHandler);
        WorldInfo worldInfo = saveHandler.loadWorldInfo();

        if(worldInfo == null)
            worldInfo = new WorldInfo(worldSettings, worldNameIn);
        else
            worldInfo.setWorldName(worldNameIn);

        if (false) { //Forge: Dead Code, implement below.
            for (int i = 0; i < this.worlds.length; ++i)
            {
                int j = 0;

                if (i == 1)
                {
                    j = -1;
                }

                if (i == 2)
                {
                    j = 1;
                }

                if (i == 0)
                {
                    if (this.isDemo())
                    {
                        worlds[i] = (WorldServer)(new WorldServerDemo(this, saveHandler, worldInfo, j, this.profiler)).init();
                    }
                    else
                    {
                        worlds[i] = (WorldServer)(new WorldServer(this, saveHandler, worldInfo, j, this.profiler)).init();
                    }

                    worlds[i].initialize(this.worldSettings);
                }
                else
                {
                    worlds[i] = (WorldServer)(new WorldServerMulti(this, saveHandler, j, this.worlds[0], this.profiler)).init();
                }

                worlds[i].addEventListener(new ServerWorldEventHandler(this, this.worlds[i]));
            }
        }// Forge: End Dead Code

        WorldServer overWorld = (isDemo() ? (WorldServer) (new WorldServerDemo(this, saveHandler, worldInfo, 0, this.profiler)).init() :
                (WorldServer)(new WorldServer(this, saveHandler, worldInfo, 0, this.profiler)).init());

        overWorld.initialize(worldSettings);
        for(int dim : DimensionManager.getStaticDimensionIDs()){
            WorldServer world = (dim == 0 ? overWorld : (WorldServer)(new WorldServerMulti(this, saveHandler, dim, overWorld, this.profiler)).init());
            world.addEventListener(new ServerWorldEventHandler(this, world));
            if (!this.isSinglePlayer())
            {
                world.getWorldInfo().setGameType(getGameType());
            }
            MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(world));
        }

        this.getPlayerList().setPlayerManager(new WorldServer[]{overWorld});

        this.initialWorldChunkLoad();

        done = true;
    }

    @Override
    public boolean init() throws IOException {
        Kisman.LOGGER.info("[CustomServerProvider] Starting server version 1.12.2");
        setOnlineMode(true);
        setCanSpawnAnimals(true);
        setCanSpawnNPCs(true);
        setAllowPvp(true);
        setAllowFlight(true);
        Kisman.LOGGER.info("[CustomServerProvider] Generating Keypair");
        if(!FMLCommonHandler.instance().handleServerAboutToStart(this))
            return false;
        loadAllWorld(getFolderName(), getWorldName(), worldSettings.getSeed(), worldSettings.getTerrainType(), worldSettings.getGeneratorOptions());
        setMOTD(getServerOwner() + " - " + worlds[0].getWorldInfo().getWorldName());
        return FMLCommonHandler.instance().handleServerStarting(this);
    }

    @Override
    public void tick(){
        boolean flag = this.isGamePaused;
        this.isGamePaused = Minecraft.getMinecraft().getConnection() != null && Minecraft.getMinecraft().isGamePaused();

        if (!flag && this.isGamePaused)
        {
            Kisman.LOGGER.info("[CustomServerProvider] Saving and pausing game...");
            this.getPlayerList().saveAllPlayerData();
            this.saveAllWorlds(false);
        }

        if (this.isGamePaused)
        {
            synchronized (this.futureTaskQueue)
            {
                while (!this.futureTaskQueue.isEmpty())
                {
                    Util.runTask(this.futureTaskQueue.poll(), Kisman.LOGGER);
                }
            }
        }
        else {
            super.tick();

            if (mc.gameSettings.renderDistanceChunks != this.getPlayerList().getViewDistance()) {
                Kisman.LOGGER.info("[CustomServerProvider] Changing view distance to {}, from {}", mc.gameSettings.renderDistanceChunks, this.getPlayerList().getViewDistance());
                this.getPlayerList().setViewDistance(mc.gameSettings.renderDistanceChunks);
            }

            WorldInfo worldinfo1 = this.worlds[0].getWorldInfo();
            WorldInfo worldinfo = mc.world.getWorldInfo();

            if (!worldinfo1.isDifficultyLocked() && worldinfo.getDifficulty() != worldinfo1.getDifficulty()) {
                Kisman.LOGGER.info("Changing difficulty to {}, from {}", worldinfo.getDifficulty(), worldinfo1.getDifficulty());
                this.setDifficultyForAllWorlds(worldinfo.getDifficulty());
            } else if (worldinfo.isDifficultyLocked() && !worldinfo1.isDifficultyLocked()) {
                Kisman.LOGGER.info("Locking difficulty to {}", (Object) worldinfo.getDifficulty());

                for (WorldServer worldserver : this.worlds) {
                    if (worldserver != null) {
                        worldserver.getWorldInfo().setDifficultyLocked(true);
                    }
                }
            }
        }
    }

    @Override
    public boolean canStructuresSpawn() {
        return false;
    }

    @Override
    public GameType getGameType() {
        return worldSettings.getGameType();
    }

    @Override
    public EnumDifficulty getDifficulty() {
        return mc.world.getWorldInfo().getDifficulty();
    }

    @Override
    public boolean isHardcore() {
        return worldSettings.getHardcoreEnabled();
    }

    public boolean shouldBroadcastRconToOps()
    {
        return true;
    }

    public boolean shouldBroadcastConsoleToOps()
    {
        return true;
    }

    public void saveAllWorlds(boolean isSilent)
    {
        super.saveAllWorlds(isSilent);
    }

    public File getDataDirectory()
    {
        return new File("ttcSaves");
    }

    public boolean isDedicatedServer()
    {
        return false;
    }

    public boolean shouldUseNativeTransport()
    {
        return false;
    }

    public void finalTick(CrashReport report)
    {
        //this.mc.crashed(report);
    }

    public CrashReport addServerInfoToCrashReport(CrashReport report)
    {
        report = super.addServerInfoToCrashReport(report);
        report.getCategory().addDetail("Type", () -> "Integrated Server (map_client.txt)");
        report.getCategory().addDetail("Is Modded", () -> {
            String s = ClientBrandRetriever.getClientModName();

            if (!s.equals("vanilla"))
            {
                return "Definitely; Client brand changed to '" + s + "'";
            }
            else
            {
                s = CustomServerProvider.this.getServerModName();

                if (!"vanilla".equals(s))
                {
                    return "Definitely; Server brand changed to '" + s + "'";
                }
                else
                {
                    return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.";
                }
            }
        });
        return report;
    }

    public void setDifficultyForAllWorlds(EnumDifficulty difficulty)
    {
        super.setDifficultyForAllWorlds(difficulty);
    }

    @Override
    public void addServerStatsToSnooper(Snooper playerSnooper)
    {
        //super.addServerStatsToSnooper(playerSnooper);
    }

    public boolean isSnooperEnabled()
    {
        return Minecraft.getMinecraft().isSnooperEnabled();
    }

    @Override
    public String shareToLAN(GameType type, boolean allowCheats)
    {
        return "";
    }

    public void stopServer()
    {
        initiateShutdown();
        super.stopServer();
    }

    public void initiateShutdown()
    {
        // No need to check that, you cant join it anyway!
        /*
        if (isServerRunning())
            Futures.getUnchecked(this.addScheduledTask(new Runnable()
            {
                public void run()
                {
                    for (EntityPlayerMP entityplayermp : Lists.newArrayList(WorldGeneratorV2.this.getPlayerList().getPlayers()))
                    {
                        if (!entityplayermp.getUniqueID().equals(WorldGeneratorV2.this.mc.player.getUniqueID()))
                        {
                            WorldGeneratorV2.this.getPlayerList().playerLoggedOut(entityplayermp);
                        }
                    }
                }
            }));

         */
        super.initiateShutdown();

    }

    public void setGameType(GameType gameMode)
    {
        super.setGameType(gameMode);
        this.getPlayerList().setGameType(gameMode);
    }

    public boolean isCommandBlockEnabled()
    {
        return true;
    }

    public int getOpPermissionLevel()
    {
        return 4;
    }
}
