package i.gishreloaded.gishcode.utils.visual;

import com.kisman.cc.Kisman;

//import i.gishreloaded.gishcode.hack.hacks.GhostMode;

import i.gishreloaded.gishcode.utils.Utils;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ChatUtils {
	// TODO Rewrite to LogManager
	
	public static void component(ITextComponent component)
	{
		if(Wrapper.INSTANCE.player() == null || Wrapper.INSTANCE.mc().ingameGUI.getChatGUI() == null)
			return;
			Wrapper.INSTANCE.mc().ingameGUI.getChatGUI()
				.printChatMessage(new TextComponentTranslation(TextFormatting.WHITE + "")
					.appendSibling(component));
	}

	public static void simpleMessage(Object message) {
		component(new TextComponentTranslation((String) message));
	}
	
	public static void message(Object message)
	{
		component(new TextComponentTranslation(TextFormatting.GRAY + "[" + TextFormatting.WHITE + Kisman.NAME + TextFormatting.GRAY + "] " + message));
	}

	public static void complete(Object message) {
		component(new TextComponentTranslation(TextFormatting.GRAY + "[" + TextFormatting.GREEN + Kisman.NAME + TextFormatting.GRAY + "] " + message));
	}
	
	public static void warning(Object message)
	{
		component(new TextComponentTranslation(TextFormatting.GRAY + "[" + TextFormatting.GOLD + Kisman.NAME + TextFormatting.GRAY + "] " + message));
	}
	
	public static void error(Object message)
	{
		component(new TextComponentTranslation(TextFormatting.GRAY + "[" + TextFormatting.RED + Kisman.NAME + TextFormatting.GRAY + "] " + message));
	}
}
