package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import org.lwjgl.input.Keyboard;

public class ClickGUI extends Module {

	public ClickGUI() {
		super("ClickGUI", "menu.skeet", Category.CLIENT);
		this.setKey(Keyboard.KEY_U);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		mc.displayGuiScreen(Kisman.instance.clickGui);
		this.setToggled(false);
	}
}
