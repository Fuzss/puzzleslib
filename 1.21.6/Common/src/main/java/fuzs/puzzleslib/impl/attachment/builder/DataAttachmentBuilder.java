package fuzs.puzzleslib.impl.attachment.builder;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class DataAttachmentBuilder<T, V> implements DataAttachmentRegistry.Builder<T, V> {
    protected final Map<Predicate<T>, Function<RegistryAccess, V>> defaultValues = new LinkedHashMap<>();
    @Nullable
    protected Codec<V> codec;

    @Override
    public DataAttachmentRegistry.Builder<T, V> defaultValue(Function<RegistryAccess, V> defaultValueProvider) {
        Objects.requireNonNull(defaultValueProvider, "default value provider is null");
        this.defaultValues.put(Predicates.alwaysTrue(), defaultValueProvider);
        return this;
    }

    @Override
    public DataAttachmentRegistry.Builder<T, V> persistent(Codec<V> codec) {
        Objects.requireNonNull(codec, "codec is null");
        this.codec = codec;
        return this;
    }

    @Nullable
    public BiConsumer<T, V> getSynchronizer(ResourceLocation resourceLocation, AttachmentTypeAdapter<T, V> attachmentType) {
        return null;
    }
}
