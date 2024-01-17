package fuzs.puzzleslib.fabric.impl.capability.data;

import dev.onyxstudios.cca.api.v3.component.ComponentAccess;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityKey;
import fuzs.puzzleslib.impl.capability.GlobalCapabilityRegister;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public abstract class FabricCapabilityKey<T, C extends CapabilityComponent<T>> implements CapabilityKey<T, C> {
    private final ComponentKey<ComponentAdapter<T, C>> componentKey;

    public FabricCapabilityKey(ComponentKey<ComponentAdapter<T, C>> componentKey) {
        this.componentKey = componentKey;
        GlobalCapabilityRegister.register(this);
    }

    @Override
    public ResourceLocation identifier() {
        return this.componentKey.getId();
    }

    @Override
    public C get(@NotNull T holder) {
        Objects.requireNonNull(holder, "holder is null");
        if (holder instanceof ComponentAccess && this.isProvidedBy(holder)) {
            ComponentAdapter<T, C> componentAdapter = this.componentKey.getNullable(holder);
            Objects.requireNonNull(componentAdapter, "data is null");
            C capabilityComponent = componentAdapter.getComponent();
            Objects.requireNonNull(capabilityComponent, "data is null");
            return capabilityComponent;
        } else {
            throw new IllegalArgumentException("Invalid capability holder: %s".formatted(holder));
        }
    }

    @Override
    public boolean isProvidedBy(@Nullable Object holder) {
        return holder instanceof ComponentAccess && this.componentKey.isProvidedBy(holder);
    }

    @FunctionalInterface
    public interface Factory<T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> extends Function<ComponentKey<ComponentAdapter<T, C>>, K> {

    }
}
