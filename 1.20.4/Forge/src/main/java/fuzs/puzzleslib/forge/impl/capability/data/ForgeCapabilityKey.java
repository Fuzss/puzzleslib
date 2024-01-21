package fuzs.puzzleslib.forge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityKey;
import fuzs.puzzleslib.impl.capability.GlobalCapabilityRegister;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class ForgeCapabilityKey<T, C extends CapabilityComponent<T>> implements CapabilityKey<T, C> {
    private final ResourceLocation identifier;
    private final CapabilityTokenFactory<T, C> tokenFactory;
    private final Predicate<Object> filter;
    private final Supplier<C> capabilityFactory;
    private Capability<C> capability;
    @Nullable
    protected C fallback;

    public ForgeCapabilityKey(ResourceLocation identifier, CapabilityTokenFactory<T, C> tokenFactory, Predicate<Object> filter, Supplier<C> capabilityFactory) {
        this.identifier = identifier;
        this.tokenFactory = tokenFactory;
        this.filter = filter;
        this.capabilityFactory = capabilityFactory;
        GlobalCapabilityRegister.register(this);
    }

    public void createCapability(CapabilityToken<C> token) {
        this.capability = this.tokenFactory.apply(token);
    }

    @Override
    public ResourceLocation identifier() {
        return this.identifier;
    }

    @Override
    public C get(@NotNull T holder) {
        Objects.requireNonNull(holder, "holder is null");
        if (holder instanceof ICapabilityProvider capabilityProvider && this.isProvidedBy(holder)) {
            C capabilityComponent = capabilityProvider.getCapability(this.capability).resolve().orElseGet(() -> {
                // Forge invalidates capabilities e.g. when an entity has died
                // Our implementation always expects a value to be present though, so return a dummy value that is not serialized nor synchronized
                return this.fallback == null ? this.fallback = this.capabilityFactory.get() : this.fallback;
            });
            Objects.requireNonNull(capabilityComponent, "data is null");
            return capabilityComponent;
        } else {
            throw new IllegalArgumentException("Invalid capability holder: %s".formatted(holder));
        }
    }

    @Override
    public boolean isProvidedBy(@Nullable Object holder) {
        return holder instanceof ICapabilityProvider && this.filter.test(holder);
    }

    @FunctionalInterface
    public interface CapabilityTokenFactory<T, C extends CapabilityComponent<T>> extends Function<CapabilityToken<C>, Capability<C>> {

    }

    @FunctionalInterface
    public interface ForgeCapabilityKeyFactory<T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> {

        K apply(ResourceLocation identifier, CapabilityTokenFactory<T, C> tokenFactory, Predicate<Object> filter, Supplier<C> capabilityFactory);
    }
}
