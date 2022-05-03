package com.kisman.cc.gui.book;

import com.kisman.cc.gui.book.components.ActionButton;
import com.kisman.cc.gui.book.components.FormatButton;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.*;

@SideOnly(Side.CLIENT)
public class BookEditingGui extends GuiScreenBook {
    private int x, y;

    private Field gettingSigned;
    private Field book_title;
    private Method insert;

    public BookEditingGui(EntityPlayer player, ItemStack book) {
        super(player, book, true);

        // Get access to bookGettingSigned field
        Class<GuiScreenBook> screenBook = GuiScreenBook.class;
        try {
            this.gettingSigned = screenBook.getDeclaredField("field_146480_s");
        } catch (NoSuchFieldException e) {
            try {
                this.gettingSigned = screenBook.getDeclaredField("bookGettingSigned");
            } catch (NoSuchFieldException e2) {
                throw new RuntimeException("FamilyFunPack error: no such field " + e2.getMessage() + " in class GuiScreenBook");
            }
        }
        this.gettingSigned.setAccessible(true);

        // Get access to bookTitle field
        try {
            this.book_title = screenBook.getDeclaredField("field_146482_z");
        } catch (NoSuchFieldException e) {
            try {
                this.book_title = screenBook.getDeclaredField("bookTitle");
            } catch (NoSuchFieldException e2) {
                throw new RuntimeException("FamilyFunPack error: no such field " + e2.getMessage() + " in class GuiScreenBook");
            }
        }
        this.book_title.setAccessible(true);

        // Get access to pageInsertIntoCurrent method
        try {
            this.insert = screenBook.getDeclaredMethod("func_146459_b", String.class);
        } catch(NoSuchMethodException e) {
            try {
                this.insert = screenBook.getDeclaredMethod("pageInsertIntoCurrent", String.class);
            } catch(NoSuchMethodException e2) {
                throw new RuntimeException("FamilyFunPack Error: no method pageInsertIntoCurrent in class GuiScreenBook");
            }
        }
        this.insert.setAccessible(true);
    }

    public void initGui() {
        super.initGui();

        this.x = ((this.width - 192) / 2) - 58;
        this.y = 9;

        // Title
        GuiLabel title = (new GuiLabel(this.fontRenderer, 0, this.x + 2, this.y + 4, 60, 16, 0xffffffff)).setCentered();
        title.addLine("Formatting");
        this.labelList.add(title);

        // Color formats
        for(int i = 0; i < 16; i ++) {
            this.buttonList.add(new FormatButton(i, this.x + 16 + (i / 8) * 24, this.y + 21 + (i % 8) * 8, this.fontRenderer, TextFormatting.fromColorIndex(i)));
        }

        // Special formats
        int i = this.y + 28 + 8 * 8;
        for(String format : TextFormatting.getValidValues(false, true)) {
            this.buttonList.add(new FormatButton(0, this.x + 4, i, this.fontRenderer, TextFormatting.getValueByName(format)));
            i += 12;
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw background
        Gui.drawRect(this.x, this.y, this.x + 64, this.y + 168, new Color(0, 0, 0, 255).getRGB());
        Gui.drawRect(this.x, this.y, this.x + 64, this.y + 1, -1);
        Gui.drawRect(this.x, this.y, this.x + 1, this.y + 168, -1);
        Gui.drawRect(this.x, this.y + 167, this.x + 64, this.y + 168, -1);
        Gui.drawRect(this.x + 63, this.y, this.x + 64, this.y + 168, -1);

        // Draw labels
        GlStateManager.pushMatrix();
        float scale = 0.7f;
        GlStateManager.scale(scale, scale, scale);

        int i = this.y + 29 + 8 * 8;
        for(String format : TextFormatting.getValidValues(false, true)) {
            TextFormatting f = TextFormatting.getValueByName(format);
            this.drawString(this.fontRenderer, f.toString() + format, (int)((float)(this.x + 14) / scale), (int)((float)i / scale), 0xffffffff);
            i += 12;
        }

        GlStateManager.popMatrix();

        // draw book gui + buttons
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void keyTyped(char typedChat, int keyCode) {
        try {
            boolean signing = (boolean) this.gettingSigned.get(this);

            if(signing) { // Modify signing behavior to enable 32 chars title
                String title = (String) this.book_title.get(this);
                super.keyTyped(typedChat, keyCode);
                if(keyCode != 14 && keyCode != 28 && keyCode != 156 && title.length() < 32) {
                    this.book_title.set(this, title + typedChat);
                }
            } else super.keyTyped(typedChat, keyCode);
        } catch (IllegalAccessException | IOException e) {
            throw new RuntimeException("kisman.cc error: " + e.getMessage());
        }
    }

    public void appendFormat(String format) {
        try {
            boolean signing = (boolean) this.gettingSigned.get(this);
            if(signing) { // title
                String title = (String) this.book_title.get(this);
                if(format.length() + title.length() <= 32) {
                    this.book_title.set(this, title + format);
                }
            } else { // book
                this.insert.invoke(this, format);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("kisman.cc error: " + e.getMessage());
        }
    }

    protected void actionPerformed(GuiButton button) {
        if(button instanceof ActionButton) {
            ((ActionButton) button).onClick(this);
        }
        try {
            super.actionPerformed(button);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
