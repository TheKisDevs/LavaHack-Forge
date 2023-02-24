package com.kisman.cc.features.hud;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.loadingscreen.progressbar.EventProgressBar;
import com.kisman.cc.features.hud.modules.*;
import com.kisman.cc.settings.util.ShaderPattern;
import com.kisman.cc.util.UtilityKt;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class HudModuleManager {
	public ArrayList<HudModule> modules = new ArrayList<>();
	public ArrayList<ShaderableHudModule> shaderableModules = new ArrayList<>();

	private final HudModule fakeModule = new FakeHudModule("Shaders");
	public final ShaderPattern shaders = new ShaderPattern(fakeModule).init();

	public HudModuleManager() {
		MinecraftForge.EVENT_BUS.register(this);
		Kisman.instance.progressBar.steps++;
	}

	public void init() {
		Kisman.EVENT_BUS.post(new EventProgressBar("HUD Module Manager"));

		add(new TwoBeeTwoTeeQueue());
		add(new ArmorHUD());
		add(new ArrayListModule());
		add(new BindList());
		add(new Coords());
		add(new CrystalPerSecond());
		add(new CurrentConfig());
		add(new Fps());
		add(new Indicators());
		add(new InventoryHud());
		add(new Logo());
		add(new PearlCooldown());
		add(new Ping());
		add(new PotionHud());
		add(new PvpInfo());
		add(new PvpResources());
		add(new Radar());
		add(new ServerIp());
		add(new Speed());
		add(new TargetHUD());
		add(new TextRadar());
		add(new Tps());
		add(new Welcomer());

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
