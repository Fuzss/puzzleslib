package fuzs.puzzleslib.impl.attachment;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class DataAttachmentTypeImpl<T, A> implements DataAttachmentType<T, A> {
    private final AttachmentTypeAdapter<T, A> attachmentType;
    private final Map<Predicate<T>, A> defaultValues;
    private final BiConsumer<T, A> synchronizer;

    public DataAttachmentTypeImpl(AttachmentTypeAdapter<T, A> attachmentType, Map<Predicate<T>, A> defaultValues, @Nullable BiConsumer<T, A> synchronizer) {
        this.attachmentType = attachmentType;
        this.defaultValues = ImmutableMap.copyOf(defaultValues);
        this.synchronizer = synchronizer != null ? synchronizer : (T o1, A o2) -> {
            // NO-OP
        };
    }

    @Nullable
    private A getDefaultValue(T holder) {
        for (Map.Entry<Predicate<T>, A> entry : this.defaultValues.entrySet()) {
            if (entry.getKey().test(holder)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public @Nullable A get(T holder) {
        if (!this.attachmentType.hasData(holder)) {
            A defaultValue = this.getDefaultValue(holder);
            if (defaultValue != null) {
                this.attachmentType.setData(holder, defaultValue);
            }
        }
        if (this.attachmentType.hasData(holder)) {
            A value = this.attachmentType.getData(holder);
            // do not support setting null values (Fabric does not), the attachment type can still be removed though
            Objects.requireNonNull(value, () -> "value for " + this.attachmentType.resourceLocation() + " is null");
            return value;
        } else {
            return null;
        }
    }

    @Override
    public A getOrDefault(T holder, A defaultValue) {
        A value = this.get(holder);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean has(T holder) {
        return this.attachmentType.hasData(holder) || this.getDefaultValue(holder) != null;
    }

    @Override
    public void set(T holder, @Nullable A newValue) {
        A oldValue;
        // do not support setting null values (Fabric does not), the attachment type can still be removed though
        if (newValue != null) {
            oldValue = this.attachmentType.setData(holder, newValue);
        } else {
            oldValue = this.attachmentType.removeData(holder);
        }
        if (newValue != oldValue) {
            this.synchronizer.accept(holder, newValue);
        }
    }

    @Override
    public void update(T holder, UnaryOperator<A> valueUpdater) {
        this.set(holder, valueUpdater.apply(this.get(holder)));
    }
}
