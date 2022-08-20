package fuzs.puzzleslib.capability.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * implementation of {@link CapabilityKey} for the Forge mod loader
 *
 * @param <C> capability type
 */
public class ForgeCapabilityKey<C extends CapabilityComponent> implements CapabilityKey<C> {
    /**
     * id just like ComponentKey on Fabric has it stored directly in the key
     */
    private final ResourceLocation id;
    /**
     * capability type class just like ComponentKey on Fabric has it stored directly in the key
     */
    private final Class<C> componentClass;
    /**
     * factory for creating capability on Forge, necessary to do this here as we cannot wrap {@link CapabilityToken} for the common project
     */
    private final CapabilityTokenFactory<C> factory;
    /**
     * the wrapped {@link Capability}
     */
    private Capability<C> capability;

    /**
     * @param id                capability id
     * @param componentClass    capability type class
     */
    public ForgeCapabilityKey(ResourceLocation id, Class<C> componentClass, CapabilityTokenFactory<C> factory) {
        this.id = id;
        this.componentClass = componentClass;
        this.factory = factory;
    }

    /**
     * creates the capability
     *
     * @param token     the token
     */
    public void createCapability(CapabilityToken<C> token) {
        this.capability = this.factory.apply(token);
    }

    @Override
    public ResourceLocation getId() {
        this.validateCapability();
        return this.id;
    }

    @Override
    public Class<C> getComponentClass() {
        this.validateCapability();
        return this.componentClass;
    }

    @Nullable
    @Override
    public <V> C get(@Nullable V provider) {
        this.validateCapability();
        if (provider instanceof ICapabilityProvider capabilityProvider) {
            LazyOptional<C> optional = capabilityProvider.getCapability(this.capability);
            if (optional.isPresent()) {
                return optional.orElseThrow(IllegalStateException::new);
            }
        }
        return null;
    }

    @Override
    public <V> Optional<C> maybeGet(@Nullable V provider) {
        this.validateCapability();
        if (provider instanceof ICapabilityProvider capabilityProvider) {
            return capabilityProvider.getCapability(this.capability).resolve();
        }
        return Optional.empty();
    }

    /**
     * check if a token has been supplied and the capability has been created
     */
    private void validateCapability() {
        Objects.requireNonNull(this.capability, "No valid capability implementation registered for %s".formatted(this.id));
    }

    /**
     * create the capability
     *
     * @param <C> capability type
     */
    public interface CapabilityTokenFactory<C extends CapabilityComponent> {

        /**
         * create the capability
         *
         * @param token     the token
         * @return          capability for token
         */
        Capability<C> apply(CapabilityToken<C> token);
    }
}
