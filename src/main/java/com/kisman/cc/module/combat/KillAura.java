//package com.kisman.cc.module.combat;
//
//import com.kisman.cc.Kisman;
//import com.kisman.cc.module.Category;
//import com.kisman.cc.module.Module;
//import com.kisman.cc.settings.Setting;
//import net.minecraft.entity.EntityLivingBase;
//
//public class KillAura extends Module {
//    private EntityLivingBase target;
//    private long current, last;
//    private int delay = 8;
//    private float yaw, pitch;
//    private boolean others;
//
//    public KillAura() {
//        super("KillAura", "", Category.COMBAT);
//        Kisman.instance.settingsManager.rSetting(new Setting("Crack Size", this, 5, 0, 15, true));
//        Kisman.instance.settingsManager.rSetting(new Setting("Existed", this, 30, 0, 500, true));
//        Kisman.instance.settingsManager.rSetting(new Setting("FOV", this, 360, 0, 360, true));
//        Kisman.instance.settingsManager.rSetting(new Setting("AutoBlock", this, true));
//        Kisman.instance.settingsManager.rSetting(new Setting("Invisibles", this, false));
//        Kisman.instance.settingsManager.rSetting(new Setting("Players", this, true));
//        Kisman.instance.settingsManager.rSetting(new Setting("Animals", this, false));
//        Kisman.instance.settingsManager.rSetting(new Setting("Monsters", this, false));
//        Kisman.instance.settingsManager.rSetting(new Setting("Villagers", this, false));
//        Kisman.instance.settingsManager.rSetting(new Setting("Teams", this, false));
//    }
//
//    public void update() {
//        target = getClosest(mc.playerController.getBlockReachDistance());
//        if(target == null)
//            return;
//        updateTime();
//        yaw = mc.thePlayer.rotationYaw;
//        pitch = mc.thePlayer.rotationPitch;
//        boolean block = target != null && Tutorial.instance.settingsManager.getSettingByName("AutoBlock").getValBoolean() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
//        if(block && target.getDistanceToEntity(mc.thePlayer) < 8F)
//            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
//        if(current - last > 1000 / delay) {
//            attack(target);
//            resetTime();
//        }
//    }
//}
