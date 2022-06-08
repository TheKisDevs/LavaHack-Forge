package com.kisman.cc.features.plugins.managers;

import net.minecraft.client.resources.IResource;

@FunctionalInterface
public interface ResourceSupplier
{
    IResource get() throws ResourceException;
}