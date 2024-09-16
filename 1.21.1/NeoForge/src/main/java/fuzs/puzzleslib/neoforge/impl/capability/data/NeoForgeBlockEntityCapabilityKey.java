package fuzs.puzzleslib.neoforge.impl.capability.data;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.capability.v3.data.BlockEntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Predicate;

public class NeoForgeBlockEntityCapabilityKey<T extends BlockEntity, C extends CapabilityComponent<T>> extends NeoForgeCapabilityKey<T, C> implements BlockEntityCapabilityKey<T, C> {

    public NeoForgeBlockEntityCapabilityKey(DeferredHolder<AttachmentType<?>, AttachmentType<C>> attachmentType, Codec<C> codec, Predicate<Object> filter) {
        super(attachmentType, codec, filter);
    }
}
