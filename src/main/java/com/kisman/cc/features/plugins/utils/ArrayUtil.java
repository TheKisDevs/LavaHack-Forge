package com.kisman.cc.features.plugins.utils;

public class ArrayUtil
{
    public static boolean contains(char ch, char[] array)
    {
        for (char c : array)
        {
            if (ch == c)
            {
                return true;
            }
        }

        return false;
    }

}