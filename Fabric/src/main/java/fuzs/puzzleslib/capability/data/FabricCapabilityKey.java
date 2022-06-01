package fuzs.puzzleslib.capability.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import fuzs.puzzleslib.capability.CapabilityController;
import fuzs.puzzleslib.capability.data.CapabilityComponent;
import fuzs.puzzleslib.capability.data.CapabilityKey;
import fuzs.puzzleslib.capability.data.ComponentHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FabricCapabilityKey<T extends CapabilityComponent> implements CapabilityKey<T> {
    private final ComponentKey<ComponentHolder> capability;
    private final Class<T> componentClass;

    public FabricCapabilityKey(ComponentKey<ComponentHolder> capability, Class<T> componentClass) {
        this.capability = capability;
        this.componentClass = componentClass;
        CapabilityController.submit(this);
    }

    @Override
    public ResourceLocation getId() {
        return this.capability.getId();
    }

    @Override
    public Class<T> getComponentClass() {
        return this.componentClass;
    }

    @Nullable
    @Override
    public <V> T get(@Nullable V provider) {
        if (provider == null) return null;
        ComponentHolder holder = this.capability.getNullable(provider);
        if (holder == null) return null;
        return (T) holder.component();
    }

    @Override
    public <V> Optional<T> maybeGet(@Nullable V provider) {
        return this.capability.maybeGet(provider).map(holder -> (T) holder.component());
    }
}
