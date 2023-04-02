package com.kisman.cc.features.hud;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.loadingscreen.progressbar.EventProgressBar;
import com.kisman.cc.features.hud.modules.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.ShaderPattern;
import com.kisman.cc.util.UtilityKt;
import com.kisman.cc.util.enums.LinkedPlaces;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class HudModuleManager {
	public ArrayList<HudModule> modules = new ArrayList<>();
	public ArrayList<ShaderableHudModule> shaderableModules = new ArrayList<>();

	private final HudModule fakeModule = new FakeHudModule("Shaders");
	public final ShaderPattern shaders = new ShaderPattern(fakeModule).init();

	public final Setting offsetXSetting = new Setting("Offset X", null, 1, 0, 5, true).onChange(setting -> { offsetX = setting.getValInt(); });
	public final Setting offsetYSetting = new Setting("Offset Y", null, 1, 0, 5, true).onChange(setting -> { offsetY = setting.getValInt(); });

	public int offsetX = 1;
	public int offsetY = 1;

	public HudModuleManager() {
		MinecraftForge.EVENT_BUS.register(this);
		Kisman.instance.progressBar.steps++;
	}

	public void init() {
		Kisman.EVENT_BUS.post(new EventProgressBar("HUD Module Manager"));

		add(new TwoBeeTwoTeeQueue());
		add(new ArmorHUD());
		add(ArrayList2.instance);
		add(new BindList());
		add(new Coords2());
		add(new CrystalPerSecond2());
		add(new CurrentConfig());
		add(new Fps2());
		add(new Indicators());
		add(new InventoryHud());
		add(new Logo());
		add(new PearlCooldown());
		add(new Ping2());
		add(new PotionHud2());
		add(new PvpInfo());
		add(new PvpResources());
		add(new Radar());
		add(new ServerIp2());
		add(new Speed());
		add(new TargetHUD());
		add(new TextRadar());
		add(new Tps2());
		add(new Welcomer2());

		add(fakeModule);
	}

	private void add(HudModule module) {
		modules.add(module);

		Kisman.LOGGER.info("Initializing " + module.getName() + " hud module!");

		if(module instanceof ShaderableHudModule) shaderableModules.add((ShaderableHudModule) module);
	}
	
	public HudModule getModule(String name) {
		for (HudModule m : this.modules) if (m.getName().equalsIgnoreCase(name)) return m;
		return null;
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Text event) {
		for(LinkedPlaces place : LinkedPlaces.values()) place.refresh();

		Runnable preNormalRender = () -> {};
		Runnable shaderRender = () -> {};
		Runnable postNormalRender = () -> {};

		boolean canDraw = false;

		for(ShaderableHudModule module : shaderableModules) if(module.isToggled() && module.shaderSetting.getValBoolean()) {
			if(module.preRender) preNormalRender = UtilityKt.compare(preNormalRender, module.preNormalRender);
			if(module.postRender) postNormalRender = UtilityKt.compare(postNormalRender, module.postNormalRender);

			shaderRender = UtilityKt.compare(shaderRender, module.shaderRender);

			canDraw = true;
		}

		if(canDraw) {
			preNormalRender.run();

			shaders.start();
			shaderRender.run();
			shaders.end();

			postNormalRender.run();
		}
	}
}
