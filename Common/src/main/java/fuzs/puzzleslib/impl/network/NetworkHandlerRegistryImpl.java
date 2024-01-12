package fuzs.puzzleslib.impl.network;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public abstract class NetworkHandlerRegistryImpl implements NetworkHandlerV3.Builder {
    protected final ResourceLocation channelIdentifier;
    private final List<Class<?>> clientboundMessages = Lists.newArrayList();
    private final List<Class<?>> serverboundMessages = Lists.newArrayList();
    public boolean clientAcceptsVanillaOrMissing;
    public boolean serverAcceptsVanillaOrMissing;

    protected NetworkHandlerRegistryImpl(ResourceLocation channelIdentifier) {
        this.channelIdentifier = channelIdentifier;
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> Builder registerClientbound(Class<T> clazz) {
        if (this.clientboundMessages.contains(clazz)) throw new IllegalStateException("Duplicate message of type %s".formatted(clazz));
        this.clientboundMessages.add(clazz);
        return this;
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> Builder registerServerbound(Class<T> clazz) {
        if (this.serverboundMessages.contains(clazz)) throw new IllegalStateException("Duplicate message of type %s".formatted(clazz));
        this.serverboundMessages.add(clazz);
        return this;
    }

    @Override
    public Builder clientAcceptsVanillaOrMissing() {
        this.clientAcceptsVanillaOrMissing = true;
        return this;
    }

    @Override
    public Builder serverAcceptsVanillaOrMissing() {
        this.serverAcceptsVanillaOrMissing = true;
        return this;
    }

    @Override
    public void build() {
        for (Class<?> message : this.clientboundMessages) {
            this.registerClientbound$Internal(message);
        }
        for (Class<?> message : this.serverboundMessages) {
            this.registerServerbound$Internal(message);
        }
        this.clientboundMessages.clear();
        this.serverboundMessages.clear();
    }

    protected abstract <T extends Record & ClientboundMessage<T>> void registerClientbound$Internal(Class<?> clazz);

    protected abstract <T extends Record & ServerboundMessage<T>> void registerServerbound$Internal(Class<?> clazz);
}
