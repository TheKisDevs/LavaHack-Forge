package com.kisman.cc.settings.types.number;

import com.kisman.cc.gui.csgo.Utils;

import java.util.Locale;
import java.util.function.Function;

/**
 * @author _kisman_
 * @since 16:46 of 20.06.2022
 */
public enum NumberType {
    PERCENT(number -> String.format(Locale.ENGLISH, "%.1f%%", number.floatValue())),
    TIME(number -> Utils.Companion.formatTime(number.longValue())),
    DECIMAL(number -> String.format(Locale.ENGLISH, "%.4f", number.floatValue())),
    INTEGER(number -> Long.toString(number.longValue()));

    private Function<Number, String> formatter;

    NumberType(Function<Number, String> formatter) {
        this.formatter = formatter;
    }

    public Function<Number, String> getFormatter() {
        return formatter;
    }
}
