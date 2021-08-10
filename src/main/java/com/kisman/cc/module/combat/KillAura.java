//package com.kisman.cc.module.combat;
//
//import com.kisman.cc.Kisman;
//import com.kisman.cc.module.Category;
//import com.kisman.cc.module.Module;
//import com.kisman.cc.settings.Setting;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.player.EntityPlayer;
//
//import java.util.Iterator;
//
//public class KillAura extends Module {
//    public KillAura() {
//        super("KillAura", "", Category.COMBAT);
//    }
//
//    public void update() 	{
//        for (Iterator i = Minecraft.getMinecraft().world.loadedEntityList.iterator(); i.hasNext();) {
//            Object o = i.next();
//            if(o instanceof EntityPlayer)
//            {
//                EntityPlayer ep = (EntityPlayer) o;
//                if(!(ep instanceof EntityPlayerSP))
//                {
//                    Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().player, ep);
//                }
//            }
//        }
//    }
//}
