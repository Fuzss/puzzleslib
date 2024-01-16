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
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class ForgeCapabilityKey<T, C extends CapabilityComponent<T>> implements CapabilityKey<T, C> {
    private final ResourceLocation identifier;
    private final CapabilityTokenFactory<T, C> tokenFactory;
    private Capability<C> capability;

    public ForgeCapabilityKey(ResourceLocation identifier, CapabilityTokenFactory<T, C> tokenFactory) {
        this.identifier = identifier;
        this.tokenFactory = tokenFactory;
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
            C capabilityComponent = capabilityProvider.getCapability(this.capability).resolve().orElse(null);
            Objects.requireNonNull(capabilityComponent, "data is null");
            return capabilityComponent;
        } else {
            throw new IllegalArgumentException("Invalid capability holder: %s".formatted(holder));
        }
    }

    @Override
    public boolean isProvidedBy(@Nullable Object holder) {
        return holder instanceof ICapabilityProvider capabilityProvider && capabilityProvider.getCapability(this.capability).isPresent();
    }

    @FunctionalInterface
    public interface CapabilityTokenFactory<T, C extends CapabilityComponent<T>> extends Function<CapabilityToken<C>, Capability<C>> {

    }

    @FunctionalInterface
    public interface ForgeCapabilityKeyFactory<T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> extends BiFunction<ResourceLocation, CapabilityTokenFactory<T, C>, K> {

    }
}
