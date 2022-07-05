package com.kisman.cc.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author _kisman_
 * @since 12:51 of 04.07.2022
 */
public class Utility {
    public static boolean runningFromIntelliJ() {
        return System.getProperty("java.class.path").contains("idea_rt.jar");
    }

    public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) os.write(buffer, 0, len);
        return os.toByteArray();
    }
}
