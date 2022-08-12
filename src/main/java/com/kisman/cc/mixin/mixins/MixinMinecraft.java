 package com.kisman.cc.mixin.mixins;

 import baritone.api.BaritoneAPI;
 import baritone.api.IBaritone;
 import baritone.api.event.events.BlockInteractEvent;
 import baritone.api.event.events.TickEvent;
 import baritone.api.event.events.WorldEvent;
 import baritone.api.event.events.type.EventState;
 import com.kisman.cc.Kisman;
 import com.kisman.cc.event.events.EventClientTick;
 import com.kisman.cc.features.module.player.AntiDesync;
 import com.kisman.cc.features.module.player.Interaction;
 import com.kisman.cc.features.viaforge.ViaForge;
 import net.minecraft.client.entity.EntityPlayerSP;
 import net.minecraft.client.gui.GuiScreen;
 import net.minecraft.client.main.GameConfiguration;
 import net.minecraft.client.multiplayer.PlayerControllerMP;
 import net.minecraft.client.multiplayer.WorldClient;
 import net.minecraft.client.settings.GameSettings;
 import net.minecraft.item.ItemStack;
 import net.minecraft.util.EnumActionResult;
 import net.minecraft.util.EnumHand;
 import net.minecraft.util.math.BlockPos;
 import org.spongepowered.asm.lib.Opcodes;
 import org.spongepowered.asm.mixin.Mixin;

 import net.minecraft.client.Minecraft;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.injection.*;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

 import java.util.function.BiFunction;

 @Mixin(value = Minecraft.class, priority = 10000)
 public class MixinMinecraft {
  @Shadow public GameSettings gameSettings;
  @Shadow public EntityPlayerSP player;
  @Shadow public PlayerControllerMP playerController;
  @Shadow private void clickMouse() {}

  private boolean mt_handActive = false;
  private boolean mt_isHittingBlock = false;

  /**
   * @author Cubic
   */
  @Inject(method = "runTick", at = @At("HEAD"))
  public void runTickPre(CallbackInfo ci){
   EventClientTick.Pre eventClientTick = new EventClientTick.Pre();
   Kisman.EVENT_BUS.post(eventClientTick);
  }

  /**
   * @author Cubic
   */
  @Inject(method = "runTick", at = @At("RETURN"))
  public void runTickPost(CallbackInfo ci) {
   EventClientTick.Post eventClientTick = new EventClientTick.Post();
   Kisman.EVENT_BUS.post(eventClientTick);
   if(AntiDesync.INSTANCE.isToggled()) AntiDesync.INSTANCE.onClientTickPost();
  }

  @Inject( method = "processKeyBinds", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z", shift = At.Shift.BEFORE, ordinal = 2 ) )
  public void mt_processKeyBinds( CallbackInfo info ) {
   Interaction module = (Interaction) Kisman.instance.moduleManager.getModule("Interaction");
   if(module.isToggled() && module.multiTask.getValBoolean()) {
    while(gameSettings.keyBindAttack.isPressed())
     clickMouse();
   }
  }

  @Inject( method = "rightClickMouse", at = @At( "HEAD" ) )
  public void mt_rightClickMouse( CallbackInfo info ) {
   Interaction module = (Interaction) Kisman.instance.moduleManager.getModule("Interaction");
   if(module.isToggled() && module.multiTask.getValBoolean()) {
    mt_isHittingBlock = playerController.getIsHittingBlock();
    playerController.isHittingBlock = false;
   }
  }

  @Inject(method = "rightClickMouse", at = @At("RETURN"))
  public void mt_rightClickMousePost(CallbackInfo ci) {
   Interaction module = (Interaction) Kisman.instance.moduleManager.getModule("Interaction");
   if (module.isToggled() && module.multiTask.getValBoolean() && !playerController.getIsHittingBlock()) playerController.isHittingBlock = mt_isHittingBlock;
  }

  @Inject(method = "sendClickBlockToController", at = @At("HEAD"))
  public void mt_sendClickBlockToControllerPre(boolean leftClick, CallbackInfo ci) {
   Interaction module = (Interaction) Kisman.instance.moduleManager.getModule("Interaction");
   if (module.isToggled() && module.multiTask.getValBoolean()) {
    mt_handActive = player.isHandActive();
    player.handActive = false;
   }
  }

  @Inject(method = "sendClickBlockToController", at = @At("RETURN"))
  public void mt_sendClickBlockToControllerPost(boolean leftClick, CallbackInfo ci) {
   Interaction module = (Interaction) Kisman.instance.moduleManager.getModule("Interaction");
   if (module.isToggled() && module.multiTask.getValBoolean() && !player.isHandActive()) player.handActive = mt_handActive;
  }

  @Inject(method = "<init>", at = @At("RETURN"))
  public void injectConstructor(GameConfiguration p_i45547_1_, CallbackInfo ci) {
   try {ViaForge.getInstance().start();} catch (Exception e) {Kisman.LOGGER.error("[ViaForge] ViaForge did not loaded! If you need it, restart the client");}}


  //Baritone
  @Shadow public WorldClient world;

  @Inject(method = "init", at = @At("RETURN"))
  private void postInit(CallbackInfo ci) {
   BaritoneAPI.getProvider().getPrimaryBaritone();
  }

  @Inject(
          method = "runTick",
          at = @At(
                  value = "FIELD",
                  opcode = Opcodes.GETFIELD,
                  target = "net/minecraft/client/Minecraft.currentScreen:Lnet/minecraft/client/gui/GuiScreen;",
                  ordinal = 5,
                  shift = At.Shift.BY,
                  by = -3
          )
  )
  private void runTick(CallbackInfo ci) {
   final BiFunction<EventState, TickEvent.Type, TickEvent> tickProvider = TickEvent.createNextProvider();

   for (IBaritone baritone : BaritoneAPI.getProvider().getAllBaritones()) {

    TickEvent.Type type = baritone.getPlayerContext().player() != null && baritone.getPlayerContext().world() != null
            ? TickEvent.Type.IN
            : TickEvent.Type.OUT;

    baritone.getGameEventHandler().onTick(tickProvider.apply(EventState.PRE, type));
   }

  }

  @Inject(
          method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V",
          at = @At("HEAD")
  )
  private void preLoadWorld(WorldClient world, String loadingMessage, CallbackInfo ci) {
   // If we're unloading the world but one doesn't exist, ignore it
   if (this.world == null && world == null) {
    return;
   }

   // mc.world changing is only the primary baritone

   BaritoneAPI.getProvider().getPrimaryBaritone().getGameEventHandler().onWorldEvent(
           new WorldEvent(
                   world,
                   EventState.PRE
           )
   );
  }

  @Inject(
          method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V",
          at = @At("RETURN")
  )
  private void postLoadWorld(WorldClient world, String loadingMessage, CallbackInfo ci) {
   // still fire event for both null, as that means we've just finished exiting a world

   // mc.world changing is only the primary baritone
   BaritoneAPI.getProvider().getPrimaryBaritone().getGameEventHandler().onWorldEvent(
           new WorldEvent(
                   world,
                   EventState.POST
           )
   );
  }

  @Redirect(
          method = "runTick",
          at = @At(
                  value = "FIELD",
                  opcode = Opcodes.GETFIELD,
                  target = "net/minecraft/client/gui/GuiScreen.allowUserInput:Z"
          )
  )
  private boolean isAllowUserInput(GuiScreen screen) {
   // allow user input is only the primary baritone
   return (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing() && player != null) || screen.allowUserInput;
  }

  @Inject(
          method = "clickMouse",
          at = @At(
                  value = "INVOKE",
                  target = "net/minecraft/client/multiplayer/PlayerControllerMP.clickBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z"
          ),
          locals = LocalCapture.CAPTURE_FAILHARD
  )
  private void onBlockBreak(CallbackInfo ci, BlockPos pos) {
   // clickMouse is only for the main player
   BaritoneAPI.getProvider().getPrimaryBaritone().getGameEventHandler().onBlockInteract(new BlockInteractEvent(pos, BlockInteractEvent.Type.START_BREAK));
  }

  @Inject(
          method = "rightClickMouse",
          at = @At(
                  value = "INVOKE",
                  target = "net/minecraft/client/entity/EntityPlayerSP.swingArm(Lnet/minecraft/util/EnumHand;)V"
          ),
          locals = LocalCapture.CAPTURE_FAILHARD
  )
  private void onBlockUse(CallbackInfo ci, EnumHand var1[], int var2, int var3, EnumHand enumhand, ItemStack itemstack, BlockPos blockpos, int i, EnumActionResult enumactionresult) {
   // rightClickMouse is only for the main player
   BaritoneAPI.getProvider().getPrimaryBaritone().getGameEventHandler().onBlockInteract(new BlockInteractEvent(blockpos, BlockInteractEvent.Type.USE));
  }
 }
