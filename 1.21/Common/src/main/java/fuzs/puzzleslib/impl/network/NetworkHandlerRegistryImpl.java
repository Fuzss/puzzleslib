package fuzs.puzzleslib.impl.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class NetworkHandlerRegistryImpl implements NetworkHandlerV3.Builder {
    private final Map<Class<?>, ResourceLocation> messageNames = Maps.newIdentityHashMap();
    private final Queue<Class<?>> clientboundMessages = Lists.newLinkedList();
    private final Queue<Class<?>> serverboundMessages = Lists.newLinkedList();
    protected final AtomicInteger discriminator = new AtomicInteger();
    protected final ResourceLocation channelName;
    protected boolean optional;

    protected NetworkHandlerRegistryImpl(ResourceLocation channelName) {
        this.channelName = channelName;
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> Builder registerClientbound(Class<T> clazz) {
        if (this.clientboundMessages.contains(clazz)) {
            throw new IllegalStateException("Duplicate message of type " + clazz);
        }
        this.clientboundMessages.add(clazz);
        return this;
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> Builder registerServerbound(Class<T> clazz) {
        if (this.serverboundMessages.contains(clazz)) {
            throw new IllegalStateException("Duplicate message of type " + clazz);
        }
        this.serverboundMessages.add(clazz);
        return this;
    }

    @Override
    public Builder optional() {
        this.optional = true;
        return this;
    }

    @Override
    public void build() {
        while (!this.clientboundMessages.isEmpty()) {
            this.registerClientbound$Internal(this.clientboundMessages.poll());
        }
        while (!this.serverboundMessages.isEmpty()) {
            this.registerServerbound$Internal(this.serverboundMessages.poll());
        }
    }

    protected ResourceLocation registerMessageType(Class<?> clazz) {
        ResourceLocation messageName = ResourceLocationHelper.fromNamespaceAndPath(this.channelName.toLanguageKey(), String.valueOf(this.discriminator.getAndIncrement()));
        this.messageNames.put(clazz, messageName);
        return messageName;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Record, S extends PacketListener> Packet<S> toPacket(T message, BiFunction<ResourceLocation, Consumer<FriendlyByteBuf>, Packet<S>> packetFactory) {
        Class<T> clazz = (Class<T>) message.getClass();
        ResourceLocation channelName = this.messageNames.get(clazz);
        Objects.requireNonNull(channelName, "Unknown message of type " + clazz);
        return packetFactory.apply(channelName, (FriendlyByteBuf friendlyByteBuf) -> {
            MessageSerializers.findByType(clazz).write(friendlyByteBuf, message);
        });
    }

    protected abstract <T extends Record & ClientboundMessage<T>> void registerClientbound$Internal(Class<?> clazz);

    protected abstract <T extends Record & ServerboundMessage<T>> void registerServerbound$Internal(Class<?> clazz);
}
