package i.gishreloaded.gishcode.utils;

import com.kisman.cc.module.ModuleManager;

// import i.gishreloaded.gishcode.hack.Hack;
// import i.gishreloaded.gishcode.hack.hacks.AntiBot;
import i.gishreloaded.gishcode.managers.EnemyManager;
// import i.gishreloaded.gishcode.managers.FriendManager;
// import i.gishreloaded.gishcode.managers.HackManager;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ValidUtils {
    ModuleManager moduleManager = new ModuleManager();

	public static boolean isLowHealth(EntityLivingBase entity, EntityLivingBase entityPriority) {
		return entityPriority == null || entity.getHealth() < entityPriority.getHealth();
	}
	
	public static boolean isClosest(EntityLivingBase entity, EntityLivingBase entityPriority) {
		return entityPriority == null || Wrapper.INSTANCE.player().getDistance(entity) < Wrapper.INSTANCE.player().getDistance(entityPriority);
	}
	
    public static boolean isInAttackFOV(EntityLivingBase entity, int fov) {
        return Utils.getDistanceFromMouse(entity) <= fov;
    }
    
    public static boolean isInAttackRange(EntityLivingBase entity, float range) {
        return entity.getDistance(Wrapper.INSTANCE.player()) <= range;
    }
	
    public static boolean isValidEntity(EntityLivingBase e) {
		//Module targets = moduleManager.getName("Targets");
		//if(targets.is) {
			// if(targets.isToggledValue("Players") && e instanceof EntityPlayer) {
			// 	return false;
			// } 
			// else 
			// if(targets.isToggledValue("Mobs") && e instanceof EntityLiving) {
			// 	return false;
			// } else {
    
            // }
		//}
		return true;  
	}
    
	
	public static boolean pingCheck(EntityLivingBase entity) {
		//Hack hack = HackManager.getHack("AntiBot");
		//if(hack.isToggled() && hack.isToggledValue("PingCheck") && entity instanceof EntityPlayer) {
			if (Wrapper.INSTANCE.mc().getConnection().getPlayerInfo(entity.getUniqueID()) != null) {
				if (Wrapper.INSTANCE.mc().getConnection().getPlayerInfo(entity.getUniqueID()).getResponseTime() > 5) {
					return true;
				}
			}
        	return false;
		//}
		//return true;
	}
    
	public static boolean isBot(EntityLivingBase entity) {
    	if(entity instanceof EntityPlayer) {
    		EntityPlayer player = (EntityPlayer)entity;
    		//Hack hack = HackManager.getHack("AntiBot");
			//return hack.isToggled() && AntiBot.isBot(player);
    	}
    	return false;
    }
    
	public static boolean isFriendEnemy(EntityLivingBase entity) {
		if(entity instanceof EntityPlayer) {
    		EntityPlayer player = (EntityPlayer)entity;
    		String ID = Utils.getPlayerName(player);
    		// if(FriendManager.friendsList.contains(ID)) {
    		// 	return false;
    		// }
    		if(true) {//HackManager.getHack("Enemys").isToggled()
    			if(!EnemyManager.enemysList.contains(ID)) {
    				return false;
    			}
    		}
    	}
		return true;
	}
	public static boolean isTeam(EntityLivingBase entity) {
		//Hack teams = HackManager.getHack("Teams");
		if(true) {//teams.isToggled()
			if(entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if(true) {//teams.isToggledMode("Base")
					if(player.getTeam() != null && Wrapper.INSTANCE.player().getTeam() != null) {
						if(player.getTeam().isSameTeam(Wrapper.INSTANCE.player().getTeam())){
							return false;
						}
					}
				}
				if(true) {//teams.isToggledMode("ArmorColor")
					if(!Utils.checkEnemyColor(player)) {
						return false;
					}
				}
				if(true) {//teams.isToggledMode("NameColor")
					if(!Utils.checkEnemyNameColor(player)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	public static boolean isInvisible(EntityLivingBase entity) {
		//Hack targets = HackManager.getHack("Targets");
		if(false) {//!targets.isToggledValue("Invisibles")
			if(entity.isInvisible()) {
				return false;
			}
		}
		return true;
	}
	public static boolean isNoScreen() {
		if(true) {//HackManager.getHack("NoGuiEvents").isToggled()
			if(!Utils.screenCheck()) {
				return false;
			}
		}
		return true;
	}
}

