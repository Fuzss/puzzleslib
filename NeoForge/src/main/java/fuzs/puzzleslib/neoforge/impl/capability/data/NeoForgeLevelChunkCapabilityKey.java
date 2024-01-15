package fuzs.puzzleslib.neoforge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.LevelChunkCapabilityKey;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Predicate;

public class NeoForgeLevelChunkCapabilityKey<C extends CapabilityComponent<LevelChunk>> extends NeoForgeCapabilityKey<LevelChunk, C> implements LevelChunkCapabilityKey<C> {

    public NeoForgeLevelChunkCapabilityKey(DeferredHolder<AttachmentType<?>, AttachmentType<C>> holder, Predicate<Object> filter) {
        super(holder, filter);
    }
}
