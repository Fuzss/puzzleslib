package fuzs.puzzleslib.api.core.v1;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A wrapper for Fabric's object share, will always return <code>null</code> on Forge.
 * <p>Operations are similar to a map, which this essentially represents.
 */
public interface ObjectShareAccess {

    /**
     * Gets a value from the object share.
     *
     * @param key key for value to get
     * @param <T> type of value
     * @return the value associated with <code>key</code> or <code>null</code>
     */
    default <T> Optional<T> getOptional(ResourceLocation key) {
        return this.getOptional(key.toString());
    }

    /**
     * Gets a value from the object share.
     *
     * @param key key for value to get
     * @param <T> type of value
     * @return the value associated with <code>key</code> or <code>null</code>
     */
    @SuppressWarnings("unchecked")
    default <T> Optional<T> getOptional(String key) {
        return Optional.ofNullable((T) this.get(key));
    }

    /**
     * Gets a value from the object share.
     *
     * @param key key for value to get
     * @return the value associated with <code>key</code> or <code>null</code>
     */
    @Nullable
    default Object get(ResourceLocation key) {
        return this.get(key.toString());
    }

    /**
     * Gets a value from the object share.
     *
     * @param key key for value to get
     * @return the value associated with <code>key</code> or <code>null</code>
     */
    @Nullable Object get(String key);

    /**
     * Puts a value into the object share.
     *
     * @param key   key for <code>value</code>
     * @param value new value associated with <code>key</code>
     * @return the previous value associated with <code>key</code> or <code>null</code>
     */
    @Nullable
    default Object put(ResourceLocation key, Object value) {
        return this.put(key.toString(), value);
    }

    /**
     * Puts a value into the object share.
     *
     * @param key   key for <code>value</code>
     * @param value new value associated with <code>key</code>
     * @return the previous value associated with <code>key</code> or <code>null</code>
     */
    @Nullable Object put(String key, Object value);

    /**
     * Puts a value into the object share if there is no value already associated with the provided key, or if said value is <code>null</code>.
     *
     * @param key   key for <code>value</code>
     * @param value new value associated with <code>key</code>
     * @return the value already associated with <code>key</code> or <code>value</code>
     */
    @Nullable
    default Object putIfAbsent(ResourceLocation key, Object value) {
        return this.putIfAbsent(key.toString(), value);
    }

    /**
     * Puts a value into the object share if there is no value already associated with the provided key, or if said value is <code>null</code>.
     *
     * @param key   key for <code>value</code>
     * @param value new value associated with <code>key</code>
     * @return the value already associated with <code>key</code> or <code>value</code>
     */
    @Nullable Object putIfAbsent(String key, Object value);

    /**
     * Removes a key and its associated value from the object share.
     *
     * @param key key to remove
     * @return the removed value if present
     */
    @Nullable
    default Object remove(ResourceLocation key) {
        return this.remove(key.toString());
    }

    /**
     * Removes a key and its associated value from the object share.
     *
     * @param key key to remove
     * @return the removed value if present
     */
    @Nullable Object remove(String key);
}
