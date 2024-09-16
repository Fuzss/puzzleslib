package fuzs.puzzleslib.impl.attachment.builder;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public abstract class DataAttachmentBuilder<T, A> implements DataAttachmentRegistry.Builder<T, A> {
    protected final Map<Predicate<T>, A> defaultValues = new LinkedHashMap<>();
    @Nullable
    protected Codec<A> codec;

    @Override
    public DataAttachmentRegistry.Builder<T, A> defaultValue(A defaultValue) {
        Objects.requireNonNull(defaultValue, "default value is null");
        this.defaultValues.put(Predicates.alwaysTrue(), defaultValue);
        return this;
    }

    @Override
    public DataAttachmentRegistry.Builder<T, A> persistent(Codec<A> codec) {
        Objects.requireNonNull(codec, "codec is null");
        this.codec = codec;
        return this;
    }

    @Nullable
    public BiConsumer<T, A> getSynchronizer(ResourceLocation resourceLocation, AttachmentTypeAdapter<T, A> attachmentType) {
        return null;
    }
}
