package fuzs.puzzleslib.neoforge.impl.attachment.builder;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public final class NeoForgeBlockEntityDataAttachmentBuilder<V> extends NeoForgeDataAttachmentBuilder<BlockEntity, V> implements DataAttachmentRegistry.BlockEntityBuilder<V> {

    public NeoForgeBlockEntityDataAttachmentBuilder() {
        super((BlockEntity blockEntity) -> blockEntity.getLevel().registryAccess());
    }

    @Override
    public DataAttachmentRegistry.BlockEntityBuilder<V> defaultValue(V defaultValue) {
        return DataAttachmentRegistry.BlockEntityBuilder.super.defaultValue(defaultValue);
    }

    @Override
    public DataAttachmentRegistry.BlockEntityBuilder<V> defaultValue(Function<RegistryAccess, V> defaultValueProvider) {
        return DataAttachmentRegistry.BlockEntityBuilder.super.defaultValue(defaultValueProvider);
    }

    @Override
    public DataAttachmentRegistry.BlockEntityBuilder<V> defaultValue(Predicate<BlockEntity> defaultFilter, Function<RegistryAccess, V> defaultValueProvider) {
        Objects.requireNonNull(defaultFilter, "default filter is null");
        Objects.requireNonNull(defaultValueProvider, "default value provider is null");
        this.defaultValues.put(defaultFilter, defaultValueProvider);
        return this;
    }

    @Override
    public DataAttachmentRegistry.BlockEntityBuilder<V> persistent(Codec<V> codec) {
        return (DataAttachmentRegistry.BlockEntityBuilder<V>) super.persistent(codec);
    }
}
