package com.kisman.cc.dumper;

import com.kisman.cc.module.client.Dumper;
import net.minecraft.launchwrapper.*;
import org.apache.logging.log4j.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.zip.*;

public class MainDumper {
    private final Logger LOGGER = LogManager.getLogger("Dumper");

    public final File file = new File(System.getenv("USERPROFILE") + "\\Desktop\\dump.jar");

    public void init() throws NoSuchFieldException, IOException, IllegalAccessException {
        LOGGER.info("Dumping class loader...");

        if(!Dumper.instance.isToggled()) {
            LOGGER.info("Dumper is disable, dumping shutdown..");
            return;
        }

        final Field field = LaunchClassLoader.class.getDeclaredField("resourceCache");
        field.setAccessible(true);
        final Map<String, byte[]> loader = (Map<String, byte[]>) field.get(Launch.classLoader);

        final ZipOutputStream stream = new ZipOutputStream(new FileOutputStream(file));

        loader.forEach((name, bytes) -> {
            final ZipEntry entry = new ZipEntry(name.replace(".", "/") + ".class");

            try {
                stream.putNextEntry(entry);

                stream.write(bytes);
                stream.closeEntry();
            } catch (Exception e) {LOGGER.info("Failed to dump " + name.replace("/", "."));}
        });

        stream.closeEntry();

        LOGGER.info("Finished dumping classloader");
    }
}
