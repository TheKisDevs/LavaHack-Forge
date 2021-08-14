package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.settings.Setting;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import i.gishreloaded.gishcode.wrappers.*;

public class ColorPicker extends Component{
    private final int width = 85;
    private final int height = 85;
    private int offset;

    private Setting colorPicker;
    private Button button;

    private static final ResourceLocation COLOR_PICKER_TEXTURE = new ResourceLocation("minecraft", "textures/setting/colorpicker");

    public ColorPicker(Setting colorPicker, Button button, int offset) {
        this.offset = offset;
        this.colorPicker = colorPicker;
        this.button = button;
    }

    @Override
    public void renderComponent() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(COLOR_PICKER_TEXTURE);
        //GuiScreen.drawModalRectWithCustomSizedTexture(button.parent.getX() + 3, button.parent.getY() + this.offset, 0, 0, width, height, 85, 85);
        //Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, textureWidth, textureHeight);
        Gui.drawModalRectWithCustomSizedTexture(button.parent.getX() + 3, button.parent.getY() + this.offset, 0, 0, width, height, 85, 85);
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {

    }
}
