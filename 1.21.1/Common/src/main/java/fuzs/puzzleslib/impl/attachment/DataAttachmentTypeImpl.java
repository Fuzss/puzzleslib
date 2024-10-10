package fuzs.puzzleslib.impl.attachment;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class DataAttachmentTypeImpl<T, V> implements DataAttachmentType<T, V> {
    private final AttachmentTypeAdapter<T, V> attachmentType;
    private final Map<Predicate<T>, V> defaultValues;
    private final BiConsumer<T, V> synchronizer;

    public DataAttachmentTypeImpl(AttachmentTypeAdapter<T, V> attachmentType, Map<Predicate<T>, V> defaultValues, @Nullable BiConsumer<T, V> synchronizer) {
        this.attachmentType = attachmentType;
        this.defaultValues = ImmutableMap.copyOf(defaultValues);
        this.synchronizer = synchronizer != null ? synchronizer : (T o1, V o2) -> {
            // NO-OP
        };
    }

    @Nullable
    private V getDefaultValue(T holder) {
        for (Map.Entry<Predicate<T>, V> entry : this.defaultValues.entrySet()) {
            if (entry.getKey().test(holder)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public @Nullable V get(T holder) {
        if (!this.attachmentType.hasData(holder)) {
            V defaultValue = this.getDefaultValue(holder);
            if (defaultValue != null) {
                this.attachmentType.setData(holder, defaultValue);
            }
        }
        if (this.attachmentType.hasData(holder)) {
            V value = this.attachmentType.getData(holder);
            // do not support setting null values (Fabric does not), the attachment type can still be removed though
            Objects.requireNonNull(value, () -> "value for " + this.attachmentType.resourceLocation() + " is null");
            return value;
        } else {
            return null;
        }
    }

    @Override
    public V getOrDefault(T holder, V defaultValue) {
        V value = this.get(holder);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean has(T holder) {
        return this.attachmentType.hasData(holder) || this.getDefaultValue(holder) != null;
    }

    @Override
    public void set(T holder, @Nullable V newValue) {
        V oldValue;
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
    public void update(T holder, UnaryOperator<V> valueUpdater) {
        this.set(holder, valueUpdater.apply(this.get(holder)));
    }
}
