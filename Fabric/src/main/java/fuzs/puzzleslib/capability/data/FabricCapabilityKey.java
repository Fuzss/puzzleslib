package fuzs.puzzleslib.capability.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import fuzs.puzzleslib.capability.CapabilityController;
import fuzs.puzzleslib.capability.data.CapabilityComponent;
import fuzs.puzzleslib.capability.data.CapabilityKey;
import fuzs.puzzleslib.capability.data.ComponentHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
/**
 * implementation of {@link CapabilityKey} for the Fabric mod loader
 * due to how a {@link ComponentKey} is forced to have the same type parameters as the component instance provided by the corresponding factory,
 * this has to work with our {@link ComponentHolder} wrapper instead of the component directly
 * therefore this class also includes a (safe) unchecked cast when retrieving the component
 * @param <T> capability type
 */
public class FabricCapabilityKey<T extends CapabilityComponent> implements CapabilityKey<T> {
    /**
     * the wrapped {@link ComponentKey}, which is always for a {@link ComponentHolder}
     */
    private final ComponentKey<ComponentHolder> capability;
    /**
     * the component class, so we can get the actual class from {@link #getComponentClass()}
     * also used to set type parameter to enable casting in getters
     */
    private final Class<T> componentClass;

    /**
     * @param capability the wrapped {@link ComponentKey}
     * @param componentClass capability type class for setting type parameter
     */
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
