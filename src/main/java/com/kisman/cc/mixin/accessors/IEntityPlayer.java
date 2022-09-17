package com.kisman.cc.mixin.accessors;

import com.kisman.cc.features.module.combat.autorer.MotionPredictor;

/**
 * @author _kisman_
 * @since 14:54 of 17.09.2022
 */
public interface IEntityPlayer {
    void setPredictor(MotionPredictor predictor);
    MotionPredictor getPredictor();
}
