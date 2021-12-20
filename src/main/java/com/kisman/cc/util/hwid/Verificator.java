package com.kisman.cc.util.hwid;

import com.kisman.cc.Kisman;
import com.kisman.cc.app.HWIDWindow;
import com.kisman.cc.util.discord.DiscordWebhook;
import net.minecraft.client.Minecraft;

import java.io.IOException;

public class Verificator {
    public boolean preInit() {
        if(!HWID.getHWIDList().contains(HWID.getHWID()) && !HWID.getHWIDList().contains("0")) {
            HWIDWindow.init();
            throw new NoStackTraceThrowable("Verify HWID Failed!");
        } else {
            if(HWID.getHWIDList().contains(HWID.getHWID())) {
//                Kisman.instance.mainWindow.frame.setVisible(false);
                Kisman.LOGGER.info("HWID Verify! Done");
                return true;
            } else if(HWID.getHWIDList().contains("0")) {
                DiscordWebhook hook = new DiscordWebhook(Kisman.instance.HWID_LOGS);
                hook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle("HWID Logs!")
                        .addField("ING: ", Minecraft.getMinecraft().session.getUsername(), true)
                        .addField("HWID: ", HWID.getHWID(), true)
                );

                try {
                    hook.execute();
                } catch (IOException e) {}

                throw new NoStackTraceThrowable("Register HWID Complete! ShutDown!");
            }
        }

        return false;
    }
}
