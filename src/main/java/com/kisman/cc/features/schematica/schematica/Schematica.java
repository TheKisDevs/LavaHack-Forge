package com.kisman.cc.features.schematica.schematica;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.schematica.schematica.handler.ConfigurationHandler;
import com.kisman.cc.features.schematica.schematica.handler.DownloadHandler;
import com.kisman.cc.features.schematica.schematica.handler.QueueTickHandler;
import com.kisman.cc.features.schematica.schematica.network.PacketHandler;
import com.kisman.cc.features.schematica.schematica.proxy.ClientProxy;
import com.kisman.cc.features.schematica.schematica.reference.Reference;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;

public class Schematica {
    public static File CONFIG_FOLDER = new File(Kisman.fileName + "schematica/");
    public static File CONFIG_FILE = new File(Kisman.fileName + "schematica/schematica.kis");

    public static Schematica instance = new Schematica();

    public static ClientProxy proxy = new ClientProxy();

    public static HashMap<String, String> properties = new HashMap<>();

    public void init() {
        Kisman.processAccountData();

        if(!CONFIG_FILE.exists()) {
            try {
                CONFIG_FILE.createNewFile();
            } catch (IOException e) {
                Reference.logger.error("Cannot create the config file!");
            }
        }

        ConfigurationHandler.init(CONFIG_FILE);

        proxy.preInit();


        PacketHandler.init();

        MinecraftForge.EVENT_BUS.register(QueueTickHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(DownloadHandler.INSTANCE);


        proxy.init();


        proxy.postInit();



        StringWriter sw = new StringWriter();

        try {
            InputStream is = getClass().getResourceAsStream("/assets/schematica/lang/eu_us.lang");
            if(is == null) throw new Exception();
            IOUtils.copy(is, sw, Charset.defaultCharset());
        } catch (Exception e) {
            Reference.logger.error("Cannot read lang file!");
        }

        for(String line : sw.toString().split("\n")) {
            if(!line.startsWith("#") && line.contains("=")) {
                String[] split = line.split("=");
                properties.put(split[0], split[1]);
            }
        }
    }
}
