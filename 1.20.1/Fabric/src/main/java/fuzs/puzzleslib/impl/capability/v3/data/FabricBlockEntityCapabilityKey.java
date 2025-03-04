package fuzs.puzzleslib.impl.capability.v3.data;

import fuzs.puzzleslib.api.capability.v3.data.BlockEntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class FabricBlockEntityCapabilityKey<T extends BlockEntity, C extends CapabilityComponent<T>> extends FabricCapabilityKey<T, C> implements BlockEntityCapabilityKey<T, C> {

    public FabricBlockEntityCapabilityKey(AttachmentType<C> attachmentType, Predicate<Object> filter, Supplier<C> factory) {
        super(attachmentType, filter, factory);
    }
}
