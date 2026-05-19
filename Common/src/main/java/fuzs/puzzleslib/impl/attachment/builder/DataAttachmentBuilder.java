package fuzs.puzzleslib.impl.attachment.builder;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class DataAttachmentBuilder<T, A> implements DataAttachmentRegistry.Builder<T, A> {
    protected final Map<Predicate<T>, Function<RegistryAccess, A>> defaultValues = new LinkedHashMap<>();
    @Nullable
    protected Codec<A> codec;
    @Nullable
    protected StreamCodec<? super RegistryFriendlyByteBuf, A> streamCodec;
    @Nullable
    private Function<T, PlayerSet> synchronizationTargets;

    @Override
    public DataAttachmentRegistry.Builder<T, A> defaultValue(Function<RegistryAccess, A> defaultValueProvider) {
        Objects.requireNonNull(defaultValueProvider, "default value provider is null");
        this.defaultValues.put(Predicates.alwaysTrue(), defaultValueProvider);
        return this;
    }

    @Override
    public DataAttachmentRegistry.Builder<T, A> persistent(Codec<A> codec) {
        Objects.requireNonNull(codec, "codec is null");
        this.codec = codec;
        return this;
    }

    @Override
    public DataAttachmentRegistry.Builder<T, A> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, A> streamCodec, Function<T, PlayerSet> targetSelector) {
        Objects.requireNonNull(streamCodec, "stream codec is null");
        Objects.requireNonNull(targetSelector, "synchronization targets is null");
        this.streamCodec = streamCodec;
        this.synchronizationTargets = targetSelector;
        return this;
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
