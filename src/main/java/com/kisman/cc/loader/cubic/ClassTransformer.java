package com.kisman.cc.loader.cubic;

import com.kisman.cc.loader.cubic.CubicLoader;
import com.kisman.cc.loader.antidump.AntiDump;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.service.mojang.MixinServiceLaunchWrapper;
import sun.reflect.Reflection;

import java.util.function.Supplier;

public class ClassTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(Reflection.getCallerClass(3) != LaunchClassLoader.class && Reflection.getCallerClass(3) != MixinServiceLaunchWrapper.class){
            // some tries to dump us :(
            AntiDump.dumpDetected();
            return new byte[0];
        }
        Supplier<byte[]> bytes = CubicLoader.map.get(name);
        if(bytes == null)
            return basicClass;
        return bytes.get();
    }
}
