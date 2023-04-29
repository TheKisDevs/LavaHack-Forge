package com.kisman.cc.util.enums.dynamic;

import com.kisman.cc.util.ReflectionUtilsKt;
import com.kisman.cc.util.client.providers.AbstractTaskProvider;
import com.kisman.cc.util.math.MathKt;
import org.cubic.dynamictask.AbstractTask;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;

/**
 * @author _kisman_
 * @since 12:05 of 04.08.2022
 */
public class EasingEnum implements AbstractTaskProvider {
    public static ArrayList<IEasing> allEasings = new ArrayList<>();

    public static ArrayList<IEasing> allEasingsNormal = new ArrayList<>();
    public static ArrayList<IEasing> allEasingsReverse = new ArrayList<>();

    public static ArrayList<Easing> inEasings = new ArrayList<>();
    public static ArrayList<Easing> outEasings = new ArrayList<>();

    public static ArrayList<EasingReverse> inEasingsReverse = new ArrayList<>();
    public static ArrayList<EasingReverse> outEasingsReverse = new ArrayList<>();

    @Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME) public @interface In {}
    @Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME) public @interface Out {}

    public interface IEasing {
        default double inc(float n) {
            if(n < 0) return 0;
            if(n > 1) return 1;
            return getTask().doTask((double) n);
        }

        default double dec0(float n) {
            return 1 - inc(n);
        }

        default double dec(float n, float min, float max) {
            if(max == min) return 0;
            if(max < min) {
                float oldMax = max;
                max = min;
                min = oldMax;
            }
            if(n <= 0) return max;
            if(n >= 1) return min;

            return MathKt.lerp(min, max, dec0(n));
        }

        default double dec(float n) {
            if(n <= 0) return 1;
            if(n >= 1) return 0;

            return dec0(n);
        }

        AbstractTask<Double> getTask();
    }

    public enum Easing implements IEasing {
        @In @Out Linear(dd.task(arg -> arg.fetch(0))),

        @In Curve(dd.task(arg -> MathKt.curve(arg.fetch(0)))),

        @In InSine(dd.task(arg -> 1.0 - Math.cos(((Double) arg.fetch(0) * Math.PI) / 2))),
        @Out OutSine(dd.task(arg -> Math.sin(((Double) arg.fetch(0) * Math.PI) / 2))),
        @In InOutSine(dd.task(arg -> -(Math.cos(Math.PI * (Double) arg.fetch(0)) - 1) / 2)),

        @In InQuad(dd.task(arg -> (Double) arg.fetch(0) * (Double) arg.fetch(0))),
        @Out OutQuad(dd.task(arg -> 1 - (1 - (Double) arg.fetch(0)) * (1 - (Double) arg.fetch(0)))),
        @In InOutQuad(dd.task(arg -> (Double) arg.fetch(0) < 0.5 ? 2 * (Double) arg.fetch(0) * (Double) arg.fetch(0) : 1 - Math.pow(-2 * (Double) arg.fetch(0) + 2, 2) / 2)),

        @In InCubic(dd.task(arg -> (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0))),
        @Out OutCubic(dd.task(arg -> 1 - Math.pow(1 - (Double) arg.fetch(0), 3))),
        @In InOutCubic(dd.task(arg ->  (Double) arg.fetch(0) < 0.5 ? 4 *  (Double) arg.fetch(0) *  (Double) arg.fetch(0) *  (Double) arg.fetch(0) : 1 - Math.pow(-2 *  (Double) arg.fetch(0) + 2, 3) / 2)),

        @In InQuart(dd.task(arg ->  (Double) arg.fetch(0) *  (Double) arg.fetch(0) *  (Double) arg.fetch(0) *  (Double) arg.fetch(0))),
        @Out OutQuart(dd.task(arg -> 1 - Math.pow(1 - (Double) arg.fetch(0), 4))),
        @In InOutQuart(dd.task(arg -> (Double) arg.fetch(0) < 0.5 ? 8 * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) : 1 - Math.pow(-2 * (Double) arg.fetch(0) + 2, 4) / 2)),

        @In InQuint(dd.task(arg -> (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0))),
        @Out OutQuin(dd.task(arg -> 1 - Math.pow(1 - (Double) arg.fetch(0), 5))),
        @In InOutQuint(dd.task(arg -> (Double) arg.fetch(0) < 0.5 ? 16 * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) * (Double) arg.fetch(0) : 1 - Math.pow(-2 * (Double) arg.fetch(0) + 2, 5) / 2)),

        @In InExpo(dd.task(arg ->  (Double) arg.fetch(0) == 0 ? 0 : Math.pow(2, 10 *  (Double) arg.fetch(0) - 10))),
        @Out OutExpo(dd.task(arg -> (Double) arg.fetch(0) == 1 ? 1 : 1 - Math.pow(2, -10 * (Double) arg.fetch(0)))),
        @In InOutExpo(dd.task(arg -> (Double) arg.fetch(0) == 0 ? 0 :  (Double) arg.fetch(0) == 1 ? 1 :  (Double) arg.fetch(0) < 0.5 ? Math.pow(2, 20 *  (Double) arg.fetch(0) - 10) / 2 : (2 - Math.pow(2, -20 *  (Double) arg.fetch(0) + 10)) / 2)),

        @In InCircle(dd.task(arg -> 1 - Math.sqrt(1 - Math.pow(arg.fetch(0), 2)))),
        @Out OutCircle(dd.task(arg -> Math.sqrt(1 - Math.pow((Double) arg.fetch(0) - 1, 2)))),
        @In InOutCircle(dd.task(arg -> (Double) arg.fetch(0) < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * (Double) arg.fetch(0), 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * (Double) arg.fetch(0) + 2, 2)) + 1) / 2))

        ;

        private final AbstractTask<Double> abstractTask;

        Easing(AbstractTask<Double> task) {
            this.abstractTask = task;
        }

        @Override
        public AbstractTask<Double> getTask(){
            return abstractTask;
        }
    }

    public enum EasingReverse implements IEasing {
        @In @Out Linear(Easing.Linear),

        @In Curve(Easing.Curve),

        @In InSine(Easing.InSine),
        @Out OutSine(Easing.OutSine),
        @In InOutSine(Easing.InOutSine),

        @In InQuad(Easing.InQuad),
        @Out OutQuad(Easing.OutQuad),
        @In InOutQuad(Easing.InOutQuad),

        @In InCubic(Easing.InCubic),
        @Out OutCubic(Easing.OutCubic),
        @In InOutCubic(Easing.InOutCubic),

        @In InQuart(Easing.InQuart),
        @Out OutQuart(Easing.OutQuart),
        @In InOutQuart(Easing.InOutQuart),

        @In InQuint(Easing.InQuint),
        @Out OutQuin(Easing.OutQuin),
        @In InOutQuint(Easing.InOutQuint),

        @In InExpo(Easing.InExpo),
        @Out OutExpo(Easing.OutExpo),
        @In InOutExpo(Easing.InOutExpo),

        @In InCircle(Easing.InCircle),
        @Out OutCircle(Easing.OutCircle),
        @In InOutCircle(Easing.InOutCircle);

        private final AbstractTask<Double> abstractTask;

        EasingReverse(Easing easing) {
            this.abstractTask = dd.task(arg -> 1 - easing.abstractTask.doTask((Object) arg.fetch(0)));
        }

        @Override
        public AbstractTask<Double> getTask() {
            return abstractTask;
        }
    }

    static {
        new EasingEnum().staticBlock();
    }

    private void staticBlock() {
        for(Easing easing : Easing.values()) {
            if(ReflectionUtilsKt.annotationCheck(easing, In.class)) inEasings.add(easing);
            if(ReflectionUtilsKt.annotationCheck(easing, Out.class)) outEasings.add(easing);
        }

        for(EasingReverse easing : EasingReverse.values()) {
            if(ReflectionUtilsKt.annotationCheck(easing, In.class)) inEasingsReverse.add(easing);
            if(ReflectionUtilsKt.annotationCheck(easing, Out.class)) outEasingsReverse.add(easing);
        }

        allEasings.addAll(inEasings);
        allEasings.addAll(outEasings);
        allEasings.addAll(inEasingsReverse);
        allEasings.addAll(outEasingsReverse);
        allEasingsNormal.addAll(inEasings);
        allEasingsNormal.addAll(outEasings);
        allEasingsReverse.addAll(inEasingsReverse);
        allEasingsReverse.addAll(outEasingsReverse);
    }
}
