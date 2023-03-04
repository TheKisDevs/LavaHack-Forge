package com.kisman.cc.mixin.accessors;

import java.io.DataOutput;

/**
 * @author _kisman_
 * @since 22:32 of 02.03.2023
 */
public interface INBTTagCompound {
    void handleWrite(DataOutput output);
}
