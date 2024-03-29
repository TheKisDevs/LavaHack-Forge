package com.kisman.cc.features.module.client;

import com.kisman.cc.features.module.*;
import com.kisman.cc.util.io.ClipboardImage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * @author BloomWareClient
 */
public class BetterScreenshot extends Module {
    @ModuleInstance
    public static BetterScreenshot instance;

    public BetterScreenshot() {
        super("BetterScreenshot", "offix sori no mne 'eto otchen nado ni termay repy poshaluysta", Category.CLIENT);
    }

    public static Image getLatestScreenshot() throws IOException {
        ImageIcon imageIcon = new ImageIcon(Files.list((new File(mc.mcDataDir.getAbsolutePath() + "/screenshots/")).toPath()).filter(f -> !Files.isDirectory(f)).max(Comparator.comparingLong(f -> f.toFile().lastModified())).get().toString());

        return imageIcon.getIconWidth() > 0 && imageIcon.getIconHeight() > 0 ? imageIcon.getImage() : null;
    }

    public static void copyToClipboard(Image image) {
        new Thread(() -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ClipboardImage(image), null)).start();
    }
}
