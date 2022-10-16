package com.kisman.cc.util.providers;

import org.cubic.dynamictask.*;

public interface AbstractTaskProvider {
    AbstractTask.DelegateAbstractTask<Double> dd = AbstractTask.types(
            Double.class,
            Double.class
    );
}