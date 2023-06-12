package fuzs.puzzleslib.util;

import fuzs.puzzleslib.impl.PuzzlesLib;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * small helper methods
 */
public class PuzzlesUtil {

    /**
     * perform <code>action</code> on a (most likely newly created) <code>object</code>, then return it
     *
     * @param object        object to consume
     * @param consumer      action
     * @param <T>           object type
     *
     * @return the object
     */
    public static <T> T make(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }

    /**
     * run an action, if an exception occurs run a different action
     *
     * @param object        object for actions
     * @param action        action to attempt
     * @param orElse        action in case <code>action</code> throws an exception
     * @param <T>           type of object
     *
     * @return was there an exception
     */
    public static <T> boolean runOrElse(@NotNull T object, Consumer<T> action, Consumer<T> orElse) {
        try {
            action.accept(object);
        } catch (Exception e) {
            PuzzlesLib.LOGGER.error("Unable to handle object {}: {}", object.getClass().getSimpleName(), e.getMessage());
            orElse.accept(object);
            return false;
        }
        return true;
    }

    /**
     * perform action for nullable object
     *
     * @param object        nullable object
     * @param action        action to perform
     * @param <T>           type of object
     *
     * @return was <code>object</code> null
     */
    public static <T> boolean acceptIfPresent(@Nullable T object, Consumer<T> action) {
        if (object != null) {
            action.accept(object);
            return true;
        }
        return false;
    }

    /**
     * get an instance that might not have been created yet
     *
     * @param instance      instance to get, might have to be created
     * @param supplier      supplier for creating instance
     * @param consumer      save created instance
     * @param <T>           instance type
     *
     * @return the instance
     */
    public static <T> T getOrElse(@Nullable T instance, Supplier<T> supplier, Consumer<T> consumer) {
        if (instance == null) {
            instance = supplier.get();
            consumer.accept(instance);
        }
        return instance;
    }

    /**
     * might want to wrap this in an optional
     *
     * @param collection        collection to get entry from
     * @param weight            weight getter function
     * @param <T>               type of objects in collection
     *
     * @return random entry or null when collection is empty
     */
    @Nullable
    public static <T> T getRandomEntry(Collection<T> collection, Function<T, Integer> weight) {
        if (!collection.isEmpty()) {
            int totalWeight = (int) (collection.stream().map(weight).mapToInt(Integer::intValue).sum() * Math.random());
            for (T entry : collection) {
                totalWeight -= weight.apply(entry);
                if (totalWeight < 0) {
                    return entry;
                }
            }
        }
        return null;
    }

    /**
     * rounds a number duh
     *
     * @param toRound           number to round
     * @param decimalPlaces     amount of decimal places
     *
     * @return rounded number
     */
    public static double round(double toRound, int decimalPlaces) {
        final double power = Math.pow(10, decimalPlaces);
        return  Math.round(toRound * power) / power;
    }

    /**
     * loads a service provider interface yay
     *
     * @param clazz         service provider interface class to load
     * @param <T>           interface type
     *
     * @return loaded service
     */
    public static <T> T loadServiceProvider(Class<T> clazz) {
        return ServiceLoader.load(clazz, PuzzlesUtil.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
