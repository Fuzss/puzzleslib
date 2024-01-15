package fuzs.puzzleslib.impl.capability;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Map;
import java.util.Set;

public final class GlobalCapabilityRegister {
    public static final Map<ResourceLocation, CapabilityKey<?, ?>> REGISTER = Maps.newConcurrentMap();
    public static final Set<Class<?>> VALID_CAPABILITY_TYPES = Set.of(Entity.class, BlockEntity.class, LevelChunk.class, Level.class);

    private GlobalCapabilityRegister() {

    }

    public static <T, C extends CapabilityComponent<T>> void register(CapabilityKey<T, C> capabilityKey) {
        if (REGISTER.put(capabilityKey.identifier(), capabilityKey) != null) {
            throw new IllegalStateException("Duplicate capability %s".formatted(capabilityKey.identifier()));
        }
    }

    public static CapabilityKey<?, ?> retrieve(ResourceLocation id) {
        CapabilityKey<?, ?> capabilityKey = REGISTER.get(id);
        if (capabilityKey != null) {
            return capabilityKey;
        } else {
            throw new IllegalStateException("No capability registered for id %s".formatted(id));
        }
    }
}
