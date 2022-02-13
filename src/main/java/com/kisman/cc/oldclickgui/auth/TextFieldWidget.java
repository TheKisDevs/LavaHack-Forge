package com.kisman.cc.oldclickgui.auth;

import java.awt.Color;

import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

public class TextFieldWidget {

	private String fillText = "";

	private String text = "";

	private boolean focused = false;
	private boolean borders = true;

	private int maxLength;

	private int color;

	private double x;
	private double y;
	private double width;
	private double height;

	private boolean obfedText;

	public TextFieldWidget() {
		this.setColor(0xFF0A0A0A);
		maxLength = 80;
		obfedText = false;
	}

	public void render(int mouseX, int mouseY, float partialTicks) {
		if (borders) Render2DUtil.drawBorderedRect(x + 1, y + 1, width - 2, height - 2, 1, 0xFFBEBBBB, color);
		else Render2DUtil.drawRect(x, y, x + width, y + height, color);

		double diff = Math.max(CustomFontUtil.getStringWidth(getText()) + 4 - getWidth(), 0);

		String text = obfedText ? getText().replaceAll("(?s).", "*") : getText();
		double diffY = obfedText ? 1.5 : 0;

		Render2DUtil.startScissor(x, y, width, height);
		if (!getText().isEmpty()) CustomFontUtil.drawString(text, getX() + 3 - diff, getY() + (getHeight() / 2) - (CustomFontUtil.getFontHeight() / 2) + diffY, -1);
		if (focused) Render2DUtil.drawRectWH(getX() + CustomFontUtil.getStringWidth(text) + 4 - diff, getY() + (getHeight() + 4) / 2, 4, 1, new Color(255, 255, 255, System.currentTimeMillis() % 2000 > 1000 ? 255 : 0).getRGB());

		Render2DUtil.stopScissor();

		if (!focused && text.isEmpty() && !fillText.isEmpty()) CustomFontUtil.drawString(fillText, getX() + 3, getY() + (getHeight() / 2) - (CustomFontUtil.getFontHeight() / 2), 0xFF868686);
	}

	public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
		focused = Render2DUtil.isHovered(mouseX, mouseY, x, y, width, height) && mouseButton == 0;
	}

	public void keyTyped(Character typedChar, int keyCode) {
		if (focused) {
			if (keyCode == Keyboard.KEY_BACK) if (getText().length() != 0) setText(getText().substring(0, getText().length() - 1));
			else if (GuiScreen.isKeyComboCtrlV(keyCode)) setText(getText() + GuiScreen.getClipboardString());
			else if (typedChar != null && text.length() < maxLength && ChatAllowedCharacters.isAllowedCharacter(typedChar)) setText(getText() + typedChar);
		}
	}

	public void onGuiClose() {
		focused = false;
	}

	public TextFieldWidget setBorders(boolean borders) {
		this.borders = borders;
		return this;
	}

	public TextFieldWidget setColor(int color) {
		this.color = color;
		return this;
	}

	public TextFieldWidget setObfedText(boolean obf) {
		this.obfedText = obf;
		return this;
	}

	public boolean isFocused() {
		return focused;
	}

	/**
	 * Getter for text in field
	 *
	 * @return text in field
	 */
	public String getText() {
		return text;
	}

	/**
	 * Setter for text in field
	 *
	 * @param value text
	 */
	public void setText(String value) {
		text = value;
	}

	/**
	 * Sets the fill text
	 *
	 * @param value text
	 */
	public void setFillText(String value) {
		fillText = value;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public int getColor() {
		return color;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

}