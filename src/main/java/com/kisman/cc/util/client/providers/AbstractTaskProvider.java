package com.kisman.cc.util.client.providers;

import org.cubic.dynamictask.*;

public interface AbstractTaskProvider {
    AbstractTask.DelegateAbstractTask<Double> dd = AbstractTask.types(
            Double.class,
            Double.class
    );
}