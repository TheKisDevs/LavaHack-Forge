package com.kisman.cc.features.module.client;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.render.objects.screen.Icons;
import net.minecraftforge.fml.client.SplashProgress;
import org.lwjgl.opengl.Display;

public class CustomLoadingScreen extends Module {

    public CustomLoadingScreen(){
        super("CustomLoadingScreen", Category.CLIENT);
    }

    public static final CustomLoadingScreen instance = new CustomLoadingScreen();

    public void start(){
        Icons.LOADING_SCREEN_IMAGE.render(0, 0, Display.getWidth(), Display.getHeight());
    }
}
