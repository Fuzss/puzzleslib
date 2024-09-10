package fuzs.puzzleslib.impl.capability;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public final class GlobalCapabilityRegister {
    private static final Map<ResourceLocation, CapabilityKey<?, ?>> REGISTER = Maps.newConcurrentMap();
    private static final Set<Class<?>> VALID_CAPABILITY_TYPES = Set.of(Entity.class, BlockEntity.class, LevelChunk.class, Level.class);

    private GlobalCapabilityRegister() {

    }

    public static <T, C extends CapabilityComponent<T>> void register(CapabilityKey<T, C> capabilityKey) {
        if (REGISTER.put(capabilityKey.identifier(), capabilityKey) != null) {
            throw new IllegalStateException("Duplicate capability " + capabilityKey.identifier());
        }
    }

    @NotNull
    public static CapabilityKey<?, ?> get(ResourceLocation identifier) {
        CapabilityKey<?, ?> capabilityKey = REGISTER.get(identifier);
        if (capabilityKey != null) {
            return capabilityKey;
        } else {
            throw new IllegalStateException("No capability registered for identifier " + identifier);
        }
    }

    public static void testHolderType(Class<?> holderType) {
        if (!GlobalCapabilityRegister.VALID_CAPABILITY_TYPES.contains(holderType)) {
            throw new IllegalArgumentException(holderType.getName() + " is an invalid type");
        }
    }
}
