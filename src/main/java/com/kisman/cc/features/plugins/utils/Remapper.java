package com.kisman.cc.features.plugins.utils;

import org.objectweb.asm.tree.ClassNode;

public interface Remapper
{
    void remap(ClassNode cn, Mapping mapping);

}
