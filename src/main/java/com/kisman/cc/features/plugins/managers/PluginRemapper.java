package com.kisman.cc.features.plugins.managers;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.plugins.utils.Mapping;
import com.kisman.cc.features.plugins.utils.Srg2NotchService;
import com.kisman.cc.util.UtilityKt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.jar.*;

public class PluginRemapper extends Srg2NotchService
{
    public File[] remap(Collection<File> files)
            throws IOException, URISyntaxException
    {
        File[] remapped = new File[files.size()];

        int index = 0;
        for (File file : files)
        {
            Kisman.LOGGER.info("Remapping: " + file.getName());
            URL url = UtilityKt.toUrl(file.toURI());
            String toURL = url.toString();
            toURL = toURL.substring(0, toURL.length() - 4) + "-vanilla.jar";
            URL to = UtilityKt.toUrl(toURL);

            this.remap(url, to);
            File remappedFile = new File(to.toURI());
            remapped[index++] = remappedFile;
        }

        return remapped;
    }

    @Override
    protected void handleEntry(JarEntry entry,
                               JarOutputStream jos,
                               JarFile jar,
                               Mapping mapping)
            throws IOException
    {
        if (entry.getName().equals(JarFile.MANIFEST_NAME))
        {
            try (InputStream is = jar.getInputStream(entry))
            {
                jos.putNextEntry(new JarEntry(entry.getName()));

                Manifest manifest = new Manifest(is);
                Attributes attr = manifest.getMainAttributes();
                attr.put(new Attributes.Name("3arthh4ckVanilla"), "true");

                manifest.write(jos);
                jos.flush();
                jos.closeEntry();
            }

            return;
        }

        super.handleEntry(entry, jos, jar, mapping);
    }

}
