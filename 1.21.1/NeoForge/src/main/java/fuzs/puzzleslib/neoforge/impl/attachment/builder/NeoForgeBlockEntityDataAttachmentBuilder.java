package fuzs.puzzleslib.neoforge.impl.attachment.builder;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;
import java.util.function.Predicate;

public final class NeoForgeBlockEntityDataAttachmentBuilder<V> extends NeoForgeDataAttachmentBuilder<BlockEntity, V> implements DataAttachmentRegistry.BlockEntityBuilder<V> {

    @Override
    public DataAttachmentRegistry.BlockEntityBuilder<V> defaultValue(V defaultValue) {
        return DataAttachmentRegistry.BlockEntityBuilder.super.defaultValue(defaultValue);
    }

    @Override
    public DataAttachmentRegistry.BlockEntityBuilder<V> defaultValue(Predicate<BlockEntity> defaultFilter, V defaultValue) {
        Objects.requireNonNull(defaultFilter, "default filter is null");
        Objects.requireNonNull(defaultValue, "default value is null");
        this.defaultValues.put(defaultFilter, defaultValue);
        return this;
    }

    @Override
    public DataAttachmentRegistry.BlockEntityBuilder<V> persistent(Codec<V> codec) {
        return (DataAttachmentRegistry.BlockEntityBuilder<V>) super.persistent(codec);
    }
}
