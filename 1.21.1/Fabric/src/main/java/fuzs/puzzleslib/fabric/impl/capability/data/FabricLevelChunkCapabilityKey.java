package fuzs.puzzleslib.fabric.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.LevelChunkCapabilityKey;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class FabricLevelChunkCapabilityKey<C extends CapabilityComponent<LevelChunk>> extends FabricCapabilityKey<LevelChunk, C> implements LevelChunkCapabilityKey<C> {

    public FabricLevelChunkCapabilityKey(Supplier<AttachmentType<C>> attachmentType, Predicate<Object> filter, Supplier<C> factory) {
        super(attachmentType, filter, factory);
    }
}
