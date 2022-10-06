package com.kisman.cc.util.enums.dynamic;

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
