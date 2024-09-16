package fuzs.puzzleslib.fabric.impl.attachment;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;
import java.util.function.Predicate;

public final class FabricBlockEntityDataAttachmentBuilder<A> extends FabricDataAttachmentBuilder<BlockEntity, A> implements DataAttachmentRegistry.BlockEntityBuilder<A> {

    @Override
    public DataAttachmentRegistry.BlockEntityBuilder<A> defaultValue(A defaultValue) {
        return DataAttachmentRegistry.BlockEntityBuilder.super.defaultValue(defaultValue);
    }

    @Override
    public DataAttachmentRegistry.BlockEntityBuilder<A> defaultValue(Predicate<BlockEntity> defaultFilter, A defaultValue) {
        Objects.requireNonNull(defaultFilter, "default filter is null");
        Objects.requireNonNull(defaultValue, "default value is null");
        this.defaultValues.put(defaultFilter, defaultValue);
        return this;
    }

    @Override
    public DataAttachmentRegistry.BlockEntityBuilder<A> persistent(Codec<A> codec) {
        return (DataAttachmentRegistry.BlockEntityBuilder<A>) super.persistent(codec);
    }
}
