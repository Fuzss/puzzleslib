package fuzs.puzzleslib.common.api.attachment.v4;

import org.jspecify.annotations.Nullable;

import java.util.function.UnaryOperator;

/**
 * Common attachment type implementation.
 *
 * @param <T> attachment holder type
 * @param <V> attachment value type
 */
public interface DataAttachmentType<T, V> {

    /**
     * Get the value for the attachment type.
     *
     * @param holder the attachment holder
     * @return the attachment value if present, or {@code null}
     *
     * @see #getOrDefault(Object, Object)
     */
    @Nullable V get(T holder);

    /**
     * Get the value for the attachment type, if no value is present, return the provided default value.
     *
     * @param holder       the attachment holder
     * @param defaultValue the fallback value
     * @return the attachment value
     *
     * @see #get(Object)
     */
    V getOrDefault(T holder, V defaultValue);

    /**
     * Check if the holder has a non-null value for the attachment type.
     *
     * @param holder the attachment holder
     * @return is a value present
     */
    boolean has(T holder);

    /**
     * Set a new value for the attachment type.
     * <p>
     * Setting a {@code null} value removes the attachment from the holder.
     *
     * @param holder the attachment holder
     * @param value  the new attachment value
     * @return the attachment value if present, or {@code null}
     *
     * @see #remove(Object)
     */
    @Nullable V set(T holder, @Nullable V value);

    /**
     * Remove the value for the attachment type.
     *
     * @param holder the attachment holder
     * @return the attachment value if present, or {@code null}
     *
     * @see #set(Object, Object)
     */
    @Nullable V remove(T holder);

    /**
     * Updates the attachment value on the holder.
     * <p>
     * Keep in mind the incoming value can be {@code null}.
     *
     * @param holder       the attachment holder
     * @param valueUpdater the update operation to apply
     */
    void apply(T holder, UnaryOperator<V> valueUpdater);
}
