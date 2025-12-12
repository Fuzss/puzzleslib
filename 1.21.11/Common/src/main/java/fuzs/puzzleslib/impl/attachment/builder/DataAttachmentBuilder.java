package fuzs.puzzleslib.impl.attachment.builder;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class DataAttachmentBuilder<T, V, B extends DataAttachmentRegistry.Builder<T, V, B>> implements DataAttachmentRegistry.Builder<T, V, B> {
    protected final Map<Predicate<T>, Function<RegistryAccess, V>> defaultValues = new LinkedHashMap<>();
    @Nullable
    protected Codec<V> codec;
    @Nullable
    protected StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec;
    @Nullable
    private Function<T, PlayerSet> synchronizationTargets;

    @Override
    public B defaultValue(Function<RegistryAccess, V> defaultValueProvider) {
        Objects.requireNonNull(defaultValueProvider, "default value provider is null");
        this.defaultValues.put(Predicates.alwaysTrue(), defaultValueProvider);
        return this.getThis();
    }

    @Override
    public B persistent(Codec<V> codec) {
        Objects.requireNonNull(codec, "codec is null");
        this.codec = codec;
        return this.getThis();
    }

    @Override
    public B networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec, Function<T, PlayerSet> synchronizationTargets) {
        Objects.requireNonNull(streamCodec, "stream codec is null");
        Objects.requireNonNull(synchronizationTargets, "synchronization targets is null");
        this.streamCodec = streamCodec;
        this.synchronizationTargets = synchronizationTargets;
        return this.getThis();
    }

    protected abstract RegistryAccess getRegistryAccess(T holder);

    protected boolean syncWith(T holder, ServerPlayer serverPlayer) {
        Objects.requireNonNull(this.synchronizationTargets, "synchronization targets is null");
        MutableBoolean mutableBoolean = new MutableBoolean();
        this.synchronizationTargets.apply(holder).apply((ServerPlayer serverPlayerX) -> {
            if (serverPlayer == serverPlayerX) {
                mutableBoolean.setTrue();
            }
        });
        return mutableBoolean.booleanValue();
    }
}
