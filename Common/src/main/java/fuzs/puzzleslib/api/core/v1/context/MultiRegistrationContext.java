package fuzs.puzzleslib.api.core.v1.context;

import java.util.Locale;
import java.util.Objects;

@FunctionalInterface
public interface MultiRegistrationContext<O, T> {

    @SuppressWarnings("unchecked")
    default void register(T type, O object, O... objects) {
        Objects.requireNonNull(type, "%s is null".formatted(simpleName(type)));
        Objects.requireNonNull(object, "%s is null".formatted(simpleName(object)));
        this.register(object, type);
        Objects.requireNonNull(objects, "%s is null".formatted(simpleName(objects)));
        for (O _object : objects) {
            Objects.requireNonNull(_object, "%s is null".formatted(simpleName(_object)));
            this.register(_object, type);
        }
    }

    void register(O object, T type);

    private static String simpleName(Object object) {
        return object.getClass().getSimpleName().toLowerCase(Locale.ROOT);
    }
}
