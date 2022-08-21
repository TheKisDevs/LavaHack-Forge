package the.kis.devs.remapper.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class Srg2NotchService
{
    private final ASMRemapper remapper = new ASMRemapper();

    public void remap(File from, File to)
            throws IOException
    {
        Mapping mapping = Mapping.fromResource("mappings.csv");
        JarFile jar = new JarFile(from);
        // TODO: copy to temp file first in case remapper fucks up
        try (FileOutputStream fos = new FileOutputStream(to);
             JarOutputStream jos = new JarOutputStream(fos))
        {
            for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements();)
            {
                JarEntry next = e.nextElement();
                handleEntry(next, jos, jar, mapping);
            }
        }
    }

    protected void handleEntry(JarEntry entry, JarOutputStream jos, JarFile jar, Mapping mapping) throws IOException {
        try (InputStream is = jar.getInputStream(entry)) {
            jos.putNextEntry(new JarEntry(entry.getName()));

            if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                byte[] bytes = StreamUtil.toByteArray(is);
                System.out.println("Found class to remap: " + entry.getName());
                jos.write(remapper.transform(bytes, mapping));
            } else StreamUtil.copy(is, jos);

            jos.flush();
            jos.closeEntry();
        }
    }

}
