package fuzs.puzzleslib.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ForgeCapabilityKey<T> implements CapabilityKey<T> {
    private final ResourceLocation id;
    private final Class<T> componentClass;
    private final Capability<T> capability;

    public ForgeCapabilityKey(ResourceLocation id, Class<T> componentClass, Capability<T> capability) {
        this.id = id;
        this.componentClass = componentClass;
        this.capability = capability;
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
