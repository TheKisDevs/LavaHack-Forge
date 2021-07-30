//package com.kisman.cc.module.movement;
//
//import com.kisman.cc.event.events.EventPostMotionUpdate;
//import com.kisman.cc.module.Category;
//import com.kisman.cc.module.Module;
//
//public class LongJump extends Module {
//    public LongJump() {
//        super("LongJump", "long jump", Category.MOVEMENT);
//    }
//
//    public void onPost(EventPostMotionUpdate event) {
//        if((mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) && mc.gameSettings.keyBindJump.isKeyDown()) {
//            float dir = mc.player.rotationYaw + ((mc.player.moveForward < 0) ? 180 : 0) + ((mc.player.moveStrafing > 0) ? (-90F * ((mc.player.moveForward < 0) ? -.5F : ((mc.player.moveForward > 0) ? .4F : 1F))) : 0);
//            float xDir = (float)Math.cos((dir + 90F) * Math.PI / 180);
//            float zDir = (float)Math.sin((dir + 90F) * Math.PI / 180);
//            if(mc.player.collidedVertically && (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) && mc.gameSettings.keyBindJump.isKeyDown()) {
//                mc.player.motionX = xDir * .29F;
//                mc.player.motionZ = zDir * .29F;
//            }
//            if(mc.player.motionY == .33319999363422365 && (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown())) {
////                if (mc.player.isPotionActive(Potion.moveSpeed)) {
////                    mc.player.motionX = xDir * 1.34;
////                    mc.player.motionZ = zDir * 1.34;
////                } else {
//                    mc.player.motionX = xDir * 1.261;
//                    mc.player.motionZ = zDir * 1.261;
//                //}
//            }
//        }
//    }
//}
