package com.kisman.cc.util.enums.dynamic;

import com.kisman.cc.util.math.MathUtil;
import org.cubic.dynamictask.AbstractTask;

/**
 * @author _kisman_
 * @since 12:05 of 04.08.2022
 */
public class EasingEnum {
    private static final AbstractTask.DelegateAbstractTask<Double> task = AbstractTask.types(
            Double.class,//Mutated progress
            Double.class//Raw progress
    );

    public enum EasingOld {
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

        EasingOld(AbstractTask<Double> task) {
            this.abstractTask = task;
        }

        public AbstractTask<Double> getTask(){
            return abstractTask;
        }
    }

    public enum Easing {
        Linear(task.task(arg -> arg.fetch(0))),

        InSine(task.task(arg -> 1.0 - Math.cos(((Double) arg.fetch(0) * Math.PI) / 2))),
        OutSine(task.task(arg -> Math.sin(((Double) arg.fetch(0) * Math.PI) / 2))),
        InOutSine(task.task(arg -> -(Math.cos(Math.PI * (Double) arg.fetch(0)) - 1) / 2)),

        InQuad(task.task(arg -> (Double) arg.fetch(0) * (Double) arg.fetch(0))),
        OutQuad(task.task(arg -> 1 - (1 - (Double) arg.fetch(0)) * (1 - (Double) arg.fetch(0)))),
        InOutQuad(task.task(arg -> (Double) arg.fetch(0) < 0.5 ? 2 * (Double) arg.fetch(0) * (Double) arg.fetch(0) : 1 - Math.pow(-2 * (Double) arg.fetch(0) + 2, 2) / 2)),

        InCubic(task.task(arg -> (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0))),
        OutCubic(task.task(arg -> 1 - Math.pow(1 - (Double) arg.fetch(0), 3))),
        InOutCubic(task.task(arg ->  (Double) arg.fetch(0) < 0.5 ? 4 *  (Double) arg.fetch(0) *  (Double) arg.fetch(0) *  (Double) arg.fetch(0) : 1 - Math.pow(-2 *  (Double) arg.fetch(0) + 2, 3) / 2)),

        InQuart(task.task(arg ->  (Double) arg.fetch(0) *  (Double) arg.fetch(0) *  (Double) arg.fetch(0) *  (Double) arg.fetch(0))),
        OutQuart(task.task(arg -> 1 - Math.pow(1 - (Double) arg.fetch(0), 4))),
        InOutQuart(task.task(arg -> (Double) arg.fetch(0) < 0.5 ? 8 * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) : 1 - Math.pow(-2 * (Double) arg.fetch(0) + 2, 4) / 2)),

        InQuint(task.task(arg -> (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0))),
        OutQuin(task.task(arg -> 1 - Math.pow(1 - (Double) arg.fetch(0), 5))),
        InOutQuint(task.task(arg -> (Double) arg.fetch(0) < 0.5 ? 16 * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) : 1 - Math.pow(-2 * (Double) arg.fetch(0) + 2, 5) / 2)),

        InExpo(task.task(arg ->  (Double) arg.fetch(0) == 0 ? 0 : Math.pow(2, 10 *  (Double) arg.fetch(0) - 10))),
        OutExpo(task.task(arg -> (Double) arg.fetch(0) == 1 ? 1 : 1 - Math.pow(2, -10 * (Double) arg.fetch(0)))),
        InOutExpo(task.task(arg -> (Double) arg.fetch(0) == 0 ? 0 :  (Double) arg.fetch(0) == 1 ? 1 :  (Double) arg.fetch(0) < 0.5 ? Math.pow(2, 20 *  (Double) arg.fetch(0) - 10) / 2 : (2 - Math.pow(2, -20 *  (Double) arg.fetch(0) + 10)) / 2)),

        InCircle(task.task(arg -> 1 - Math.sqrt(1 - Math.pow(arg.fetch(0), 2)))),
        OutCircle(task.task(arg -> Math.sqrt(1 - Math.pow((Double) arg.fetch(0) - 1, 2)))),
        InOutCircle(task.task(arg -> (Double) arg.fetch(0) < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * (Double) arg.fetch(0), 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * (Double) arg.fetch(0) + 2, 2)) + 1) / 2))

        ;

        private final AbstractTask<Double> abstractTask;

        Easing(AbstractTask<Double> task) {
            this.abstractTask = task;
        }

        public AbstractTask<Double> getTask(){
            return abstractTask;
        }
    }

    public enum EasingReverse {
        Linear(task.task(arg -> 1 - (Double) arg.fetch(0))),

        InSine(task.task(arg -> Math.cos(((Double) arg.fetch(0) * Math.PI) / 2))),
        OutSine(task.task(arg -> 1 - Math.sin(((Double) arg.fetch(0) * Math.PI) / 2))),
        InOutSine(task.task(arg -> 1 - -(Math.cos(Math.PI * (Double) arg.fetch(0)) - 1) / 2)),

        InQuad(task.task(arg -> 1- (Double) arg.fetch(0) * (Double) arg.fetch(0))),
        OutQuad(task.task(arg -> (1 - (Double) arg.fetch(0)) * (1 - (Double) arg.fetch(0)))),
        InOutQuad(task.task(arg -> 1 - (Double) arg.fetch(0) < 0.5 ? 2 * (Double) arg.fetch(0) * (Double) arg.fetch(0) : 1 - Math.pow(-2 * (Double) arg.fetch(0) + 2, 2) / 2)),

        InCubic(task.task(arg -> 1 - (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0))),
        OutCubic(task.task(arg -> Math.pow(1 - (Double) arg.fetch(0), 3))),
        InOutCubic(task.task(arg -> 1 -  (Double) arg.fetch(0) < 0.5 ? 4 *  (Double) arg.fetch(0) *  (Double) arg.fetch(0) *  (Double) arg.fetch(0) : 1 - Math.pow(-2 *  (Double) arg.fetch(0) + 2, 3) / 2)),

        InQuart(task.task(arg ->  1 - (Double) arg.fetch(0) *  (Double) arg.fetch(0) *  (Double) arg.fetch(0) *  (Double) arg.fetch(0))),
        OutQuart(task.task(arg -> Math.pow(1 - (Double) arg.fetch(0), 4))),
        InOutQuart(task.task(arg -> 1 - (Double) arg.fetch(0) < 0.5 ? 8 * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) : 1 - Math.pow(-2 * (Double) arg.fetch(0) + 2, 4) / 2)),

        InQuint(task.task(arg -> 1 - (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0))),
        OutQuin(task.task(arg -> Math.pow(1 - (Double) arg.fetch(0), 5))),
        InOutQuint(task.task(arg -> 1 - (Double) arg.fetch(0) < 0.5 ? 16 * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) : 1 - Math.pow(-2 * (Double) arg.fetch(0) + 2, 5) / 2)),

        InExpo(task.task(arg ->  1 - (Double) arg.fetch(0) == 0 ? 0 : Math.pow(2, 10 *  (Double) arg.fetch(0) - 10))),
        OutExpo(task.task(arg -> 1 - (Double) arg.fetch(0) == 1 ? 1 : 1 - Math.pow(2, -10 * (Double) arg.fetch(0)))),
        InOutExpo(task.task(arg -> 1 - (Double) arg.fetch(0) == 0 ? 0 :  (Double) arg.fetch(0) == 1 ? 1 :  (Double) arg.fetch(0) < 0.5 ? Math.pow(2, 20 *  (Double) arg.fetch(0) - 10) / 2 : (2 - Math.pow(2, -20 *  (Double) arg.fetch(0) + 10)) / 2)),

        InCircle(task.task(arg -> Math.sqrt(1 - Math.pow(arg.fetch(0), 2)))),
        OutCircle(task.task(arg -> 1 - Math.sqrt(1 - Math.pow((Double) arg.fetch(0) - 1, 2)))),
        InOutCircle(task.task(arg -> 1 - (Double) arg.fetch(0) < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * (Double) arg.fetch(0), 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * (Double) arg.fetch(0) + 2, 2)) + 1) / 2))

        ;

        private final AbstractTask<Double> abstractTask;

        EasingReverse(AbstractTask<Double> task) {
            this.abstractTask = task;
        }

        public AbstractTask<Double> getTask(){
            return abstractTask;
        }
    }
}
