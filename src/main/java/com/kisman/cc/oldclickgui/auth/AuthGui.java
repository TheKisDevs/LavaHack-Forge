package com.kisman.cc.oldclickgui.auth;

import com.kisman.cc.Kisman;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.util.protect.keyauth.KeyAuthApp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class AuthGui extends GuiScreen {
    private GuiTextField keyField;
    private String key = "";
    private int statusTime;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        drawDefaultBackground();

        Render2DUtil.drawRoundedRect(width / 2 - 100, height / 4 + 2, 200, 160, 13, 0xFF141414);

        CustomFontUtil.drawCenteredStringWithShadow("Auth with license key", width / 2, height / 4 + 6, -1);

//        keyField.setX(width / 2 - 70);
//        keyField.setY(height / 4 + 50);
//        keyField.setWidth(140);
//        keyField.setHeight(22);
//        keyField.render(mouseX, mouseY, partialTicks);

        keyField.drawTextBox();

        CustomFontUtil.drawCenteredStringWithShadow("If you have access but havent key, you can dm _kisman_#5039 for help", width / 2, 10, -1);

        CustomFontUtil.drawCenteredStringWithShadow("(C) all rights reserved", width / 2, height - 14, 0xFF9C9B9C);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        if (statusTime > 0) statusTime--;
        super.updateScreen();
    }

    @Override
    protected void keyTyped(char chr, int keyCode) {
//        keyField.keyTyped(chr, keyCode);
        keyField.textboxKeyTyped(chr, keyCode);
        if (keyCode == 28) {
            if (KeyAuthApp.keyAuth.license(keyField.getText().replaceAll(" ", ""))) {

            }
            statusTime = 50;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        try {super.mouseClicked(mouseX, mouseY, button);} catch (IOException e) {e.printStackTrace();}
        keyField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void initGui() {
        super.initGui();
        KeyAuthApp.keyAuth.init();
        Keyboard.enableRepeatEvents(true);
        keyField = new GuiTextField(2, Minecraft.getMinecraft().fontRenderer, width / 2 - 70, height / 4 + 50, 140, 22);
//        keyField = new TextFieldWidget().setBorders(false);

        if(key != null && !key.isEmpty()) keyField.setText(key);

//        keyField.setFillText("XXXXXX-XXXXXX-XXXXXX-XXXXXX-XXXXXX-XXXXXX");

        buttonList.add(new GuiButton(0, width - 25, 5, 20, 20, "X"));
        buttonList.add(new GuiButton(1, width / 2 - 50, height / 4 + 100, 100, 21, "Login"));

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                this.mc.shutdown();
                break;
            case 1:
                if(KeyAuthApp.keyAuth.license(keyField.getText())) {
                    System.out.println("bebra");
                    Kisman.isOpenAuthGui = false;
                    mc.displayGuiScreen(new GuiMainMenu());
                }
                statusTime = 50;
                break;
        }
    }
}
