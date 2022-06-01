package fuzs.puzzleslib.capability;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FabricCapabilityKey<T extends Component> implements CapabilityKey<T> {
    private final ComponentKey<T> capability;

    public FabricCapabilityKey(ComponentKey<T> capability) {
        this.capability = capability;
    }

    @Override
    public ResourceLocation getId() {
        return this.capability.getId();
    }

    @Override
    public Class<T> getComponentClass() {
        return this.capability.getComponentClass();
    }

    @Nullable
    @Override
    public <V> T get(@Nullable V provider) {
        if (provider == null) return null;
        return this.capability.get(provider);
    }

    @Override
    public <V> Optional<T> maybeGet(@Nullable V provider) {
        return this.capability.maybeGet(provider);
    }
}
