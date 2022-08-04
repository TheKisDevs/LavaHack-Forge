package com.kisman.cc.util.enums.dynamic;

import com.kisman.cc.util.math.MathUtil;
import org.cubic.dynamictask.AbstractTask;

/**
 * @author _kisman_
 * @since 12:05 of 04.08.2022
 */
public class ScalingEnum {
    private static final AbstractTask.DelegateAbstractTask<Double> task = AbstractTask.types(
            Double.class,//Mutated progress
            Double.class//Raw progress
    );

    public enum Scaling {
        Linear(task.task(arg -> arg.fetch(0))),
        Curve(task.task(arg -> MathUtil.curve(arg.fetch(0)))),
//        Curve2(task.task(arg -> MathUtil.curve2(arg.fetch(0)))),
        Sin(task.task(arg -> Math.sin(((Double) arg.fetch(0)) * (Math.PI / 2.0)))),
        Cos(task.task(arg -> Math.cos(((Double) arg.fetch(0)) * (Math.PI / 2.0)))),
        Tan(task.task(arg -> Math.tan(((Double) arg.fetch(0)) * (Math.PI / 2.0)))),
        Asin(task.task(arg -> Math.asin(((Double) arg.fetch(0)) * (Math.PI / 2.0)))),
        Acos(task.task(arg -> Math.acos(((Double) arg.fetch(0)) * (Math.PI / 2.0)))),
        Atan(task.task(arg -> Math.atan(((Double) arg.fetch(0)) * (Math.PI / 2.0)))),
        FullSin(task.task(arg -> Math.sin(((Double) arg.fetch(0)) * (Math.PI / 2.0) * 2))),
//        FullCos(task.task(arg -> Math.cos(((Double) arg.fetch(0)) * (Math.PI / 2.0) * 2))),
//        FullTan(task.task(arg -> Math.tan(((Double) arg.fetch(0)) * (Math.PI / 2.0) * 2))),
        FullAsin(task.task(arg -> Math.asin(((Double) arg.fetch(0)) * (Math.PI / 2.0) * 2))),
        FullAcos(task.task(arg -> Math.acos(((Double) arg.fetch(0)) * (Math.PI / 2.0) * 2))),
        FullAtan(task.task(arg -> Math.atan(((Double) arg.fetch(0)) * (Math.PI / 2.0) * 2))),
        FullCurve(task.task(arg -> MathUtil.curve(((Double) arg.fetch(0)) * 2.0))),
//        FullCurve2(task.task(arg -> MathUtil.curve2(((Double) arg.fetch(0)) * 2.0))),
        ReverseCurve(task.task(arg -> MathUtil.curve(((Double) arg.fetch(0)) + 1))),
//        ReverseCurve2(task.task(arg -> MathUtil.curve2(((Double) arg.fetch(0)) + 1))),
        ReverseSin(task.task(arg -> Math.sin(((Double) arg.fetch(0)) * (Math.PI / 2.0) + 1))),
        ReverseCos(task.task(arg -> Math.cos(((Double) arg.fetch(0)) * (Math.PI / 2.0) + 1))),
//        ReverseTan(task.task(arg -> Math.tan(((Double) arg.fetch(0)) * (Math.PI / 2.0) + 1))),
        ReverseAsin(task.task(arg -> Math.asin(((Double) arg.fetch(0)) * (Math.PI / 2.0) + 1))),
//        ReverseAcos(task.task(arg -> Math.acos(((Double) arg.fetch(0)) * (Math.PI / 2.0) + 1))),
        ReverseAtan(task.task(arg -> Math.atan(((Double) arg.fetch(0)) * (Math.PI / 2.0) + 1))),
        FullReverseSin(task.task(arg -> Math.sin(((Double) arg.fetch(0)) * (Math.PI / 2.0) * 2.0) + 1)),
        FullReverseCos(task.task(arg -> Math.cos(((Double) arg.fetch(0)) * (Math.PI / 2.0) * 2.0) + 1)),
        FullReverseTan(task.task(arg -> Math.tan(((Double) arg.fetch(0)) * (Math.PI / 2.0) * 2.0) + 1)),
        FullReverseAsin(task.task(arg -> Math.asin(((Double) arg.fetch(0)) * (Math.PI / 2.0) * 2.0) + 1)),
        FullReverseAcos(task.task(arg -> Math.acos(((Double) arg.fetch(0)) * (Math.PI / 2.0) * 2.0) + 1)),
        FullReverseAtan(task.task(arg -> Math.atan(((Double) arg.fetch(0)) * (Math.PI / 2.0) * 2.0 + 1)));
//        FullReverseCurve(task.task(arg -> MathUtil.curve(2.0 * ((Double) arg.fetch(0)) + 1)));//,
//        FullReverseCurve2(task.task(arg -> MathUtil.curve2(2.0 *((Double) arg.fetch(0)) + 1)));

        private final AbstractTask<Double> abstractTask;

        Scaling(AbstractTask<Double> task) {
            this.abstractTask = task;
        }

        public AbstractTask<Double> getTask(){
            return abstractTask;
        }
    }
}
