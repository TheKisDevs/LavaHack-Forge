package com.kisman.cc.gui.mainmenu.gui;

import com.kisman.cc.Kisman;
import com.kisman.cc.gui.alts.AltManagerGUI;
import com.kisman.cc.util.UtilityKt;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class KismanMainMenuGui extends GuiScreen {
    private final GuiScreen lastGui;

    private GuiButton pingBypassButton;

    public KismanMainMenuGui(GuiScreen lastGui) {this.lastGui = lastGui;}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        CustomFontUtil.drawCenteredStringWithShadow(Kisman.getName() + " " + Kisman.getVersion(), width / 4f, 6, ColorUtils.astolfoColors(100, 100));
        GL11.glPopMatrix();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void actionPerformed(@NotNull GuiButton button) throws IOException {
        switch(button.id) {
            case 1:
                mc.displayGuiScreen(Kisman.instance.halqGui.setLastGui(this));
                break;
            case 2:
                UtilityKt.openLink("https://discord.gg/GRAbsr6Cf4");
                break;
            case 3:
                UtilityKt.openLink("https://www.youtube.com/channel/UCWxQLRT9CXqcK6YyiKHrrNw");
                break;
            case 4:
                mc.displayGuiScreen(Kisman.instance.viaForgeGui.setLastGui(this));
                break;
            case 5:
                mc.displayGuiScreen(new AltManagerGUI(this));
                break;
            case 6:
                mc.displayGuiScreen(lastGui);
                break;
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if(id == 7) mc.displayGuiScreen(this);
    }

    @Override
    public void initGui() {
        super.initGui();
        addButtons(height / 8 + 48 - 6, 72 - 48);
    }

    private void addButtons(int y, int offset) {
        buttonList.add(new GuiButton(1, width / 2 - 100, y, "Gui"));
        buttonList.add(new GuiButton(2, width / 2 - 100, y + offset, "Discord"));
        buttonList.add(new GuiButton(3, width / 2 - 100, y + offset * 2, "YouTube"));
        buttonList.add(new GuiButton(4, width / 2 - 100, y + offset * 4, 98, 20, "Version"));
        buttonList.add(new GuiButton(5, width / 2 + 2, y + offset * 4, 98, 20, "Alts"));

        buttonList.add(new GuiButton(6, width / 2 - 100, y + offset * 7, "Back"));
    }
}