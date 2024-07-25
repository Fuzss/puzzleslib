package fuzs.puzzleslib.impl.config.annotation;

import com.google.common.base.Predicates;
import fuzs.puzzleslib.api.config.v3.Config;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

public abstract class LimitedEntry<T> extends ValueEntry<T> {
    
    public LimitedEntry(Field field) {
        super(field);
    }

    private Set<String> getAllowedValues() {
        Config.AllowedValues allowedValues = this.field.getDeclaredAnnotation(Config.AllowedValues.class);
        if (allowedValues != null && allowedValues.values().length != 0) {
            return new LinkedHashSet<>(Arrays.asList(allowedValues.values()));
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public List<String> getComments() {
        List<String> comments = super.getComments();
        Set<String> allowedValues = this.getAllowedValues();
        if (!allowedValues.isEmpty()) {
            comments.add("Allowed Values: " + String.join(", ", allowedValues));
        }
        return comments;
    }

    public Predicate<Object> getValidator() {
        Set<String> allowedValues = this.getAllowedValues();
        if (!allowedValues.isEmpty()) {
            return (Object o) -> {
                return o != null && allowedValues.contains(o instanceof Enum<?> ? ((Enum<?>) o).name() : o.toString());
            };
        } else {
            return Predicates.alwaysTrue();
        }
    }
}
