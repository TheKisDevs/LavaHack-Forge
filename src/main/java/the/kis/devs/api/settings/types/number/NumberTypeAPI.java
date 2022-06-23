package the.kis.devs.api.settings.types.number;

import com.kisman.cc.settings.types.number.NumberType;

/**
 * @author _kisman_
 * @since 11:52 of 21.06.2022
 */
public enum NumberTypeAPI {
    PERCENT(NumberType.PERCENT),
    TIME(NumberType.TIME),
    DECIMAL(NumberType.DECIMAL),
    INTEGER(NumberType.INTEGER);

    private final NumberType type;

    NumberTypeAPI(NumberType type) {
        this.type = type;
    }

    public NumberType getFormatter() {
        return type;
    }
}
