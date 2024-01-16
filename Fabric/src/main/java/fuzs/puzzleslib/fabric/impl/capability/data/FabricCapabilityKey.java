package fuzs.puzzleslib.fabric.impl.capability.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityKey;
import fuzs.puzzleslib.impl.capability.GlobalCapabilityRegister;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public abstract class FabricCapabilityKey<T, C extends CapabilityComponent<T>> implements CapabilityKey<T, C> {
    private final ComponentKey<ComponentHolder> capability;
    private final Predicate<Object> filter;

    public FabricCapabilityKey(ComponentKey<ComponentHolder> capability, Predicate<Object> filter) {
        this.capability = capability;
        this.filter = filter;
        GlobalCapabilityRegister.register(this);
    }

    @Override
    public ResourceLocation identifier() {
        return this.capability.getId();
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

    @Override
    public boolean isProvidedBy(@Nullable Object holder) {
        return this.filter.test(holder);
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
