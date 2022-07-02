package com.kisman.cc.util.io;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class MinecraftChatOutputStream extends OutputStream {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private final ByteBuffer buf;

    private int size;

    public MinecraftChatOutputStream(){
        this.buf = ByteBuffer.allocate(8192);
        this.size = 0;
    }

    @Override
    public void write(int b) throws IOException {
        if(size == -1)
            throw new IOException("Output stream is closed");
        buf.put((byte) b);
        size++;
    }

    @Override
    public void flush() throws IOException {
        if(size == -1)
            throw new IOException("Output stream is closed");
        byte[] bytes = buf.array();
        String text = new String(bytes, 0, size);
        buf.clear();
        size = 0;
        TextComponentTranslation component = new TextComponentTranslation(text);
        mc.ingameGUI.getChatGUI().printChatMessage(component);
    }

    @Override
    public void close() throws IOException {
        if(size == -1)
            throw new IOException("Output stream is closed");
        byte[] bytes = buf.array();
        String text = new String(bytes, 0, size);
        buf.clear();
        size = -1;
        TextComponentTranslation component = new TextComponentTranslation(text);
        mc.ingameGUI.getChatGUI().printChatMessage(component);
    }
}
