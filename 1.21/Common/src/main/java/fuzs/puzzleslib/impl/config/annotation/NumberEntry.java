package fuzs.puzzleslib.impl.config.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

public abstract class NumberEntry<T extends Number, A extends Annotation> extends ValueEntry<T> {
    private final Class<A> rangeClazz;

    public NumberEntry(Field field, Class<A> rangeClazz) {
        super(field);
        this.rangeClazz = rangeClazz;
    }

    public Optional<A> getRangeAnnotation() {
        return Optional.ofNullable(this.field.getDeclaredAnnotation(this.rangeClazz));
    }

    public abstract T min();

    public abstract T max();
}
