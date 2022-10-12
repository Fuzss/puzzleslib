package fuzs.puzzleslib.core;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.impl.PuzzlesLib;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * helper class for reflection operations, similar to the Forge thing, but we want to use this on Fabric as well
 */
@SuppressWarnings("unchecked")
public final class ReflectionHelperV2 {
    /**
     * cache for all fields found in {@link #findField}
     */
    private static final Map<String, Field> FIELDS_CACHE = Maps.newIdentityHashMap();
    /**
     * cache for all methods found in {@link #findMethod}
     */
    private static final Map<String, Method> METHODS_CACHE = Maps.newIdentityHashMap();
    /**
     * cache for all constructors found in {@link #findConstructor}
     */
    private static final Map<String, Constructor<?>> CONSTRUCTORS_CACHE = Maps.newIdentityHashMap();

    /**
     * @param clazz clazz to get field from
     * @param name field name
     * @return the field
     */
    @Nullable
    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, true);
    }

    /**
     * @param clazz clazz to get field from
     * @param name field name
     * @param allowCache should the returned value be recreated instead of possibly returning from cache, since cache may contain a null value
     * @return the field
     */
    @Nullable
    public static Field findField(Class<?> clazz, String name, boolean allowCache) {
        Objects.requireNonNull(clazz, "clazz was null");
        Objects.requireNonNull(name, "field name was null");
        String fieldName = getFieldName(clazz, name);
        if (allowCache && FIELDS_CACHE.containsKey(fieldName)) {
            return FIELDS_CACHE.get(fieldName);
        }
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            FIELDS_CACHE.put(fieldName, field);
            return field;
        } catch (NoSuchFieldException e) {
            PuzzlesLib.LOGGER.warn("Unable to find field {}", fieldName, e);
        }
        FIELDS_CACHE.put(fieldName, null);
        return null;
    }

    /**
     * @param clazz clazz to get method from
     * @param name method name
     * @param parameterTypes method arguments
     * @return the method
     */
    @Nullable
    public static Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        return findMethod(clazz, name, true, parameterTypes);
    }

    /**
     * @param clazz clazz to get method from
     * @param name method name
     * @param allowCache should the returned value be recreated instead of possibly returning from cache, since cache may contain a null value
     * @param parameterTypes method arguments
     * @return the method
     */
    @Nullable
    public static Method findMethod(Class<?> clazz, String name, boolean allowCache, Class<?>... parameterTypes) {
        Objects.requireNonNull(clazz, "clazz was null");
        Objects.requireNonNull(name, "method name was null");
        String methodName = getMethodName(clazz, name, parameterTypes);
        if (allowCache && METHODS_CACHE.containsKey(methodName)) {
            return METHODS_CACHE.get(methodName);
        }
        try {
            Method method = clazz.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            METHODS_CACHE.put(methodName, method);
            return method;
        } catch (NoSuchMethodException e) {
            PuzzlesLib.LOGGER.warn("Unable to find method {}", methodName, e);
        }
        METHODS_CACHE.put(methodName, null);
        return null;
    }

    /**
     * @param clazz clazz to get constructor from
     * @param parameterTypes constructor arguments
     * @param <T> class object type
     * @return the constructor
     */
    @Nullable
    public static <T> Constructor<T> findConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        return findConstructor(clazz, true, parameterTypes);
    }

    /**
     * @param clazz clazz to get constructor from
     * @param parameterTypes constructor arguments
     * @param allowCache should the returned value be recreated instead of possibly returning from cache, since cache may contain a null value
     * @param <T> class object type
     * @return the constructor
     */
    @Nullable
    public static <T> Constructor<T> findConstructor(Class<?> clazz, boolean allowCache, Class<?>... parameterTypes) {
        Objects.requireNonNull(clazz, "clazz was null");
        String constructorName = getConstructorName(clazz, parameterTypes);
        if (allowCache && CONSTRUCTORS_CACHE.containsKey(constructorName)) {
            return (Constructor<T>) CONSTRUCTORS_CACHE.get(constructorName);
        }
        try {
            Constructor<T> constructor = (Constructor<T>) clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            CONSTRUCTORS_CACHE.put(constructorName, constructor);
            return constructor;
        } catch (NoSuchMethodException e) {
            PuzzlesLib.LOGGER.warn("Unable to find constructor {}", constructorName, e);
        }
        CONSTRUCTORS_CACHE.put(constructorName, null);
        return null;
    }

    public static <T, E> Optional<T> getValue(Class<? super E> clazz, String name, E instance) {
        return getValue(findField(clazz, name), instance);
    }

    public static <T, E> boolean setValue(Class<? super E> clazz, String name, E instance, T value) {
        return setValue(findField(clazz, name), instance, value);
    }

    /**
     * @param field field instance, don't have to null check it before, it'll be done here
     * @param instance the object instance
     * @param <T> field type for auto-casting
     * @return the field value
     */
    public static <T> Optional<T> getValue(@Nullable Field field, Object instance) {
        if (field != null) {
            try {
                return Optional.ofNullable((T) field.get(instance));
            } catch (IllegalAccessException e) {
                PuzzlesLib.LOGGER.warn("Unable to access field {}", getFieldName(field), e);
            }
        }
        return Optional.empty();
    }

    /**
     * @param field field instance, don't have to null check it before, it'll be done here
     * @param instance the object instance
     * @param <T> field type for auto-casting
     * @return the field value
     */
    public static <T> boolean setValue(@Nullable Field field, Object instance, T value) {
        if (field != null) {
            try {
                field.set(instance, value);
                return true;
            } catch (IllegalAccessException e) {
                PuzzlesLib.LOGGER.warn("Unable to access field {}", getFieldName(field), e);
            }
        }
        return false;
    }

    /**
     * @param clazz clazz to get method from
     * @param name method name
     * @param parameterTypes method arguments
     * @param instance the object instance
     * @param args required method args
     * @return method result or empty
     * @param <T> return type for auto-casting
     * @param <E> instance type to invoke method on
     */
    public static <T, E> Optional<T> invokeMethod(Class<? super E> clazz, String name, Class<?>[] parameterTypes, E instance, Object[] args) {
        return invokeMethod(findMethod(clazz, name, parameterTypes), instance, args);
    }

    /**
     * @param method method instance, don't have to null check it before, it'll be done here
     * @param instance the object instance
     * @param args required method args
     * @param <T> return type for auto-casting
     * @return method result or null when void
     */
    public static <T> Optional<T> invokeMethod(@Nullable Method method, Object instance, Object... args) {
        if (method != null) {
            try {
                return Optional.ofNullable((T) method.invoke(instance, args));
            } catch (InvocationTargetException | IllegalAccessException e) {
                PuzzlesLib.LOGGER.warn("Unable to access method {}", getMethodName(method), e);
            }
        }
        return Optional.empty();
    }

    /**
     * @param clazz clazz to get constructor from
     * @param parameterTypes constructor arguments
     * @param args constructor arguments
     * @return new instance or empty
     * @param <T> instance object type
     * @param <E> class type to create instance of
     */
    public static <T, E> Optional<T> newInstance(Class<? super E> clazz, Class<?>[] parameterTypes, Object[] args) {
        return newInstance(findConstructor(clazz, parameterTypes), args);
    }

    /**
     * @param constructor constructor instance, don't have to null check it before, it'll be done here
     * @param args constructor arguments
     * @param <T> instance object type
     * @return new instance
     */
    public static <T> Optional<T> newInstance(@Nullable Constructor<T> constructor, Object... args) {
        if (constructor != null) {
            try {
                return Optional.of(constructor.newInstance(args));
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                PuzzlesLib.LOGGER.warn("Unable to access constructor {}", getConstructorName(constructor), e);
            }
        }
        return Optional.empty();
    }

    /**
     * @param field the field
     * @return full field name
     */
    private static String getFieldName(@NotNull Field field) {
        Objects.requireNonNull(field, "Cannot get name for null field");
        return getClassMemberName(field.getDeclaringClass(), field.getName());
    }

    /**
     * creates and interns the name of a field to be used in an identity map
     *
     * @param clazz parent class
     * @param field field name
     * @return full field name
     */
    private static String getFieldName(Class<?> clazz, String field) {
        return getClassMemberName(clazz, field);
    }

    /**
     * @param method the method
     * @return full method name
     */
    private static String getMethodName(@NotNull Method method) {
        Objects.requireNonNull(method, "Cannot get name for null method");
        return getMethodName(method.getDeclaringClass(), method.getName(), method.getParameterTypes());
    }

    /**
     * @param constructor the constructor
     * @return full constructor name
     */
    private static String getConstructorName(@NotNull Constructor<?> constructor) {
        Objects.requireNonNull(constructor, "Cannot get name for null constructor");
        return getConstructorName(constructor.getDeclaringClass(), constructor.getParameterTypes());
    }

    /**
     * creates and interns the name of a constructor to be used in an identity map
     * <p>same as {@link #getMethodName}, with name set to <init>
     *
     * @param clazz parent class
     * @param parameterTypes parameter types
     * @return full constructor name
     */
    private static String getConstructorName(Class<?> clazz, Class<?>... parameterTypes) {
        return getMethodName(clazz, "<init>", parameterTypes);
    }

    /**
     * creates and interns the name of a method to be used in an identity map
     *
     * @param clazz parent class
     * @param method method name
     * @param parameterTypes parameter types
     * @return full method name
     */
    private static String getMethodName(Class<?> clazz, String method, Class<?>... parameterTypes) {
        return getClassMemberName(clazz, toMethodSignature(method, parameterTypes));
    }

    /**
     * @param method method name
     * @param parameterTypes the method's parameter types
     * @return full name with parameter types
     */
    private static String toMethodSignature(String method, Class<?>... parameterTypes) {
        StringJoiner sj = new StringJoiner(",", method + "(", ")");
        for (Class<?> parameterType : parameterTypes) {
            sj.add(parameterType.getTypeName());
        }
        return sj.toString();
    }

    /**
     * creates and interns the name of a class member to be used in an identity map
     *
     * @param clazz class containing member
     * @param member prepared member name
     * @return full member name
     */
    private static String getClassMemberName(Class<?> clazz, String member) {
        return (clazz.getTypeName() + "." + member).intern();
    }
}
