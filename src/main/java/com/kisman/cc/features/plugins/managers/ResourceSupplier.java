package com.kisman.cc.features.plugins.managers;

import com.kisman.cc.features.plugins.exceptions.ResourceException;
import net.minecraft.client.resources.IResource;

@FunctionalInterface
public interface ResourceSupplier
{
    IResource get() throws ResourceException;
}