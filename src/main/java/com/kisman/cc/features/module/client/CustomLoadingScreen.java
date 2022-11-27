package com.kisman.cc.features.module.client;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;

public class CustomLoadingScreen extends Module {

    public CustomLoadingScreen(){
        super("CustomLoadingScreen", Category.CLIENT);
        instance = this;
    }

    public static CustomLoadingScreen instance;

    public void start(){
    }
}
