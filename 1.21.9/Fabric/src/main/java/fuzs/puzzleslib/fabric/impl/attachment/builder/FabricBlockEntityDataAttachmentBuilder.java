package fuzs.puzzleslib.fabric.impl.attachment.builder;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public final class FabricBlockEntityDataAttachmentBuilder<V> extends FabricDataAttachmentBuilder<BlockEntity, V, DataAttachmentRegistry.BlockEntityBuilder<V>> implements DataAttachmentRegistry.BlockEntityBuilder<V> {

    public FabricBlockEntityDataAttachmentBuilder() {
        super((BlockEntity blockEntity) -> blockEntity.getLevel().registryAccess());
    }

    @Override
    public DataAttachmentRegistry.BlockEntityBuilder<V> getThis() {
        return this;
    }

    @Override
    public DataAttachmentRegistry.BlockEntityBuilder<V> defaultValue(Predicate<BlockEntity> defaultFilter, Function<RegistryAccess, V> defaultValueProvider) {
        Objects.requireNonNull(defaultFilter, "default filter is null");
        Objects.requireNonNull(defaultValueProvider, "default value provider is null");
        this.defaultValues.put(defaultFilter, defaultValueProvider);
        return this;
    }
}
