package fuzs.puzzleslib.config.annotation;

import fuzs.puzzleslib.config.ConfigHolder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * config annotation for individual config fields
 * a file containing these annotations needs to be registered via a {@link ConfigHolder}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Config {
    /**
     * @return name of this config option, will use field as name when empty
     */
    String name() default "";

    /**
     * @return optional config option description
     */
    String[] description() default {};

    /**
     * @return subcategory for this option
     */
    String[] category() default {};

    /**
     * @return does this option require a world restart, currently unused by forge
     */
    boolean worldRestart() default false;

    /**
     * range for int values
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface IntRange {
        /**
         * @return min value
         */
        int min() default Integer.MIN_VALUE;
        /**
         * @return max value
         */
        int max() default Integer.MAX_VALUE;
    }

    /**
     * range for long values
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface LongRange {
        /**
         * @return min value
         */
        long min() default Long.MIN_VALUE;
        /**
         * @return max value
         */
        long max() default Long.MAX_VALUE;
    }

    /**
     * range for float values
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface FloatRange {
        /**
         * @return min value
         */
        float min() default Float.MIN_VALUE;
        /**
         * @return max value
         */
        float max() default Float.MAX_VALUE;
    }

    /**
     * range for double values
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface DoubleRange {
        /**
         * @return min value
         */
        double min() default Double.MIN_VALUE;
        /**
         * @return max value
         */
        double max() default Double.MAX_VALUE;
    }

    /**
     * allowed values for string, enum and corresponding list options
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface AllowedValues {
        /**
         * @return allowed values
         */
        String[] values();
    }
}
