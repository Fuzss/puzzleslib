package fuzs.puzzleslib.capability.data;

import fuzs.puzzleslib.capability.CapabilityController;
import fuzs.puzzleslib.capability.data.CapabilityKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * implementation of {@link CapabilityKey} for the Forge mod loader
 * @param <T> capability type
 */
public class ForgeCapabilityKey<T> implements CapabilityKey<T> {
    /**
     * the wrapped {@link Capability}
     */
    private final Capability<T> capability;
    /**
     * id just like ComponentKey on Fabric has it stored directly in the key
     */
    private final ResourceLocation id;
    /**
     * capability type class just like ComponentKey on Fabric has it stored directly in the key
     */
    private final Class<T> componentClass;

    public ForgeCapabilityKey(Capability<T> capability, ResourceLocation id, Class<T> componentClass) {
        this.capability = capability;
        this.id = id;
        this.componentClass = componentClass;
        CapabilityController.submit(this);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public Class<T> getComponentClass() {
        return this.componentClass;
    }

    @Nullable
    @Override
    public <V> T get(@Nullable V provider) {
        if (provider instanceof CapabilityProvider<?> capabilityProvider) {
            LazyOptional<T> optional = capabilityProvider.getCapability(this.capability);
            if (optional.isPresent()) {
                return optional.orElseThrow(IllegalStateException::new);
            }
        }
        return null;
    }

    @Override
    public <V> Optional<T> maybeGet(@Nullable V provider) {
        if (provider instanceof CapabilityProvider<?> capabilityProvider) {
            return capabilityProvider.getCapability(this.capability).resolve();
        }
        return Optional.empty();
    }
}
