package fuzs.puzzleslib.forge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.LevelChunkCapabilityKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ForgeLevelChunkCapabilityKey<C extends CapabilityComponent<LevelChunk>> extends ForgeCapabilityKey<LevelChunk, C> implements LevelChunkCapabilityKey<C> {

    public ForgeLevelChunkCapabilityKey(ResourceLocation identifier, CapabilityTokenFactory<LevelChunk, C> tokenFactory, Predicate<Object> filter, Supplier<C> capabilityFactory) {
        super(identifier, tokenFactory, filter, capabilityFactory);
    }

    @Override
    public void setChanged(C capabilityComponent) {
        if (this.fallback != capabilityComponent) {
            LevelChunkCapabilityKey.super.setChanged(capabilityComponent);
        }
    }
}
