package com.kisman.cc.loader.security;

import sun.misc.Unsafe;

public class Unsecure {

    private static final Unsafe unsafe = UnsafeProvider.unsafe;

    public static long getAddress(Object o){
        Object[] array = new Object[]{o};
        long baseOffset = unsafe.arrayBaseOffset(Object[].class);
        return normalize(unsafe.getInt(array, baseOffset));
    }

    public static long normalize(int val){
        if(val >= 0)
            return val;
        return (~0L >>> 32) & val;
    }
}
