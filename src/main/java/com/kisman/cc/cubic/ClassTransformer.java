package com.kisman.cc.cubic;

import com.kisman.cc.loader.antidump.AntiDump;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;
import sun.reflect.Reflection;

public class ClassTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(Reflection.getCallerClass(3) != LaunchClassLoader.class){
            // some tries to dump us :(
            AntiDump.dumpDetected();
            return new byte[0];
        }
        return CubicLoader.map.get(name).get();
    }
}
