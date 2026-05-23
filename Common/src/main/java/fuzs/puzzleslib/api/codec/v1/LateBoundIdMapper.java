package fuzs.puzzleslib.api.codec.v1;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.util.v1.CodecExtras;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Copied from Minecraft 26.1.
 */
public class LateBoundIdMapper<I, V> {
    public final BiMap<I, V> idToValue = HashBiMap.create();

    public Codec<V> codec(Codec<I> idCodec) {
        BiMap<V, I> valueToId = this.idToValue.inverse();
        return CodecExtras.idResolverCodec(idCodec, this.idToValue::get, valueToId::get);
    }

    public LateBoundIdMapper<I, V> put(I id, V value) {
        Objects.requireNonNull(value, () -> "Value for " + id + " is null");
        this.idToValue.put(id, value);
        return this;
    }

    public Set<V> values() {
        return Collections.unmodifiableSet(this.idToValue.values());
    }
}
