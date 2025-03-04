package fuzs.puzzleslib.impl.capability.v3.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.LevelChunkCapabilityKey;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ForgeLevelChunkCapabilityKey<C extends CapabilityComponent<LevelChunk>> extends ForgeCapabilityKey<LevelChunk, C> implements LevelChunkCapabilityKey<C> {

    public ForgeLevelChunkCapabilityKey(ResourceLocation identifier, Capability<C> capability, Predicate<Object> filter, Supplier<C> capabilityFactory) {
        super(identifier, capability, filter, capabilityFactory);
    }

    @Override
    public void setChanged(C capabilityComponent, @Nullable PlayerSet playerSet) {
        if (this.fallback != capabilityComponent) {
            LevelChunkCapabilityKey.super.setChanged(capabilityComponent, playerSet);
        }
    }
}
