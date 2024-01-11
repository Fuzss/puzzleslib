package fuzs.puzzleslib.fabric.impl.capability.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v2.data.CapabilityKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
/**
 * implementation of {@link CapabilityKey} for the Fabric mod loader
 * due to how a {@link ComponentKey} is forced to have the same type parameters as the component instance provided by the corresponding factory,
 * this has to work with our {@link ComponentHolder} wrapper instead of the component directly
 * therefore this class also includes a (safe) unchecked cast when retrieving the component
 *
 * @param <C> capability type
 */
public class FabricCapabilityKey<C extends CapabilityComponent> implements CapabilityKey<C> {
    /**
     * the wrapped {@link ComponentKey}, which is always for a {@link ComponentHolder}
     */
    private final ComponentKey<ComponentHolder> capability;
    /**
     * the component class, so we can get the actual class from {@link #getComponentClass()}
     * also used to set type parameter to enable casting in getters
     */
    private final Class<C> componentClass;

    /**
     * @param capability the wrapped {@link ComponentKey}
     * @param componentClass capability type class for setting type parameter
     */
    public FabricCapabilityKey(ComponentKey<ComponentHolder> capability, Class<C> componentClass) {
        this.capability = capability;
        this.componentClass = componentClass;
        CapabilityController.submit(this);
    }

    @Override
    public ResourceLocation getId() {
        return this.capability.getId();
    }

    @Override
    public Class<C> getComponentClass() {
        return this.componentClass;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <V> C get(@Nullable V provider) {
        if (provider == null) return null;
        ComponentHolder holder = this.capability.getNullable(provider);
        if (holder == null) return null;
        return (C) holder.component();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Optional<C> maybeGet(@Nullable V provider) {
        return this.capability.maybeGet(provider).map(holder -> (C) holder.component());
    }

    /**
     * a factory for capability keys on Fabric, required to support unique implementation for players
     *
     * @param <C> capability type
     * @param <T> capability key type
     */
    public interface FabricCapabilityKeyFactory<C extends CapabilityComponent, T extends CapabilityKey<C>> {

        /**
         * factory method
         *
         * @param capability the wrapped {@link ComponentKey}
         * @param componentClass capability type class for setting type parameter
         * @return                  the constructed capability key
         */
        T apply(ComponentKey<ComponentHolder> capability, Class<C> componentClass);
    }
}
