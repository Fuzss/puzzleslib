package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.*;
import fuzs.puzzleslib.impl.core.Freezable;
import fuzs.puzzleslib.impl.network.codec.CustomPacketPayloadAdapter;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class NetworkHandlerRegistryImpl implements NetworkHandler.Builder, Freezable {
    private final Map<Class<?>, CustomPacketPayload.Type<CustomPacketPayloadAdapter<?>>> messageTypes = new IdentityHashMap<>();
    private final Map<Class<?>, StreamDecoder<FriendlyByteBuf, ?>> clientboundMessages = new LinkedHashMap<>();
    private final Map<Class<?>, StreamDecoder<FriendlyByteBuf, ?>> serverboundMessages = new LinkedHashMap<>();
    protected final AtomicInteger discriminator = new AtomicInteger();
    protected final ResourceLocation channelName;
    protected boolean optional;
    protected boolean isFrozen;

    protected NetworkHandlerRegistryImpl(String modId) {
        this.channelName = ResourceLocation.fromNamespaceAndPath(modId, "play");
    }

    @Override
    public abstract <T> Packet<ClientCommonPacketListener> toClientboundPacket(ClientboundMessage<T> message);

    @Override
    public abstract <T> Packet<ServerCommonPacketListener> toServerboundPacket(ServerboundMessage<T> message);

    @Override
    public <T> void sendMessage(PlayerSet playerSet, ClientboundMessage<T> message) {
        CustomPacketPayload.Type<?> type = this.getMessageType(message);
        playerSet.broadcast(type, this.toClientboundPacket(message));
    }

    @Override
    public <T> void sendMessage(ServerboundMessage<T> message) {
        ClientPacketListener clientPacketListener = Proxy.INSTANCE.getClientPacketListener();
        CustomPacketPayload.Type<?> type = this.getMessageType(message);
        if (ClientAbstractions.INSTANCE.hasChannel(clientPacketListener, type)) {
            clientPacketListener.send(this.toServerboundPacket(message));
        }
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> Builder registerClientbound(Class<T> clazz) {
        return this.registerMessage(this.clientboundMessages, clazz, null);
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> Builder registerServerbound(Class<T> clazz) {
        return this.registerMessage(this.serverboundMessages, clazz, null);
    }

    @Override
    public <T extends MessageV2<T>> Builder registerLegacyClientbound(Class<T> clazz, StreamDecoder<FriendlyByteBuf, T> factory) {
        return this.registerMessage(this.clientboundMessages, clazz, factory);
    }

    @Override
    public <T extends MessageV2<T>> Builder registerLegacyServerbound(Class<T> clazz, StreamDecoder<FriendlyByteBuf, T> factory) {
        return this.registerMessage(this.serverboundMessages, clazz, factory);
    }

    private Builder registerMessage(Map<Class<?>, StreamDecoder<FriendlyByteBuf, ?>> messages, Class<?> clazz, @Nullable StreamDecoder<FriendlyByteBuf, ?> factory) {
        this.isWritableOrThrow();
        if (messages.containsKey(clazz)) {
            throw new IllegalStateException("Duplicate message of type " + clazz);
        } else {
            messages.put(clazz, factory);
        }

        return this;
    }

    @Override
    public Builder optional() {
        this.isWritableOrThrow();
        this.optional = true;
        return this;
    }

    @Override
    public void freeze() {
        for (Map.Entry<Class<?>, StreamDecoder<FriendlyByteBuf, ?>> entry : this.clientboundMessages.entrySet()) {
            if (entry.getValue() != null) {
                this.registerLegacyClientbound$Internal(entry.getKey(), entry.getValue());
            } else {
                this.registerClientbound$Internal(entry.getKey());
            }
        }

        for (Map.Entry<Class<?>, StreamDecoder<FriendlyByteBuf, ?>> entry : this.serverboundMessages.entrySet()) {
            if (entry.getValue() != null) {
                this.registerLegacyServerbound$Internal(entry.getKey(), entry.getValue());
            } else {
                this.registerServerbound$Internal(entry.getKey());
            }
        }

        this.clientboundMessages.clear();
        this.serverboundMessages.clear();
    }

    @Override
    public boolean isFrozen() {
        return this.isFrozen;
    }

    protected BiConsumer<Throwable, Consumer<Component>> disconnectExceptionally(Class<?> clazz) {
        return (Throwable throwable, Consumer<Component> consumer) -> {
            String modName = ModContainer.getDisplayName(this.channelName.getNamespace());
            consumer.accept(Component.literal("Receiving %s from %s failed: %s".formatted(clazz.getSimpleName(),
                    modName,
                    throwable.getMessage())));
        };
    }

    protected Consumer<Consumer<Component>> disconnectWrongSide(Class<?> clazz) {
        return (Consumer<Component> consumer) -> {
            String modName = ModContainer.getDisplayName(this.channelName.getNamespace());
            consumer.accept(Component.literal("Receiving %s from %s on wrong side!".formatted(clazz.getSimpleName(),
                    modName)));
        };
    }

    @SuppressWarnings("unchecked")
    protected <T> CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> registerMessageType(Class<T> clazz) {
        ResourceLocation resourceLocation = this.channelName.withPath((String path) -> path + "/"
                + this.discriminator.getAndIncrement());
        CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type = new CustomPacketPayload.Type<>(resourceLocation);
        this.messageTypes.put(clazz,
                (CustomPacketPayload.Type<CustomPacketPayloadAdapter<?>>) (CustomPacketPayload.Type<?>) type);
        return type;
    }

    protected <T1 extends MessageV3<T2, ?>, T2, L extends PacketListener> Packet<L> toPacket(Function<CustomPacketPayload, Packet<L>> packetFactory, T1 message) {
        CustomPacketPayload.Type<CustomPacketPayloadAdapter<T2>> type = this.getMessageType(message);
        return packetFactory.apply(new CustomPacketPayloadAdapterImpl<>(type, message.unwrap()));
    }

    @SuppressWarnings("unchecked")
    protected <T1 extends MessageV3<T2, ?>, T2> CustomPacketPayload.Type<CustomPacketPayloadAdapter<T2>> getMessageType(T1 message) {
        Class<T2> clazz = (Class<T2>) message.unwrap().getClass();
        CustomPacketPayload.Type<CustomPacketPayloadAdapter<T2>> type = (CustomPacketPayload.Type<CustomPacketPayloadAdapter<T2>>) (CustomPacketPayload.Type<?>) this.messageTypes.get(
                clazz);
        Objects.requireNonNull(type, "Unknown message of type: " + clazz);
        return type;
    }

    protected abstract <T extends Record & ClientboundMessage<T>> void registerClientbound$Internal(Class<?> clazz);

    protected abstract <T extends Record & ServerboundMessage<T>> void registerServerbound$Internal(Class<?> clazz);

    protected abstract <T extends MessageV2<T>> void registerLegacyClientbound$Internal(Class<?> clazz, StreamDecoder<FriendlyByteBuf, ?> factory);

    protected abstract <T extends MessageV2<T>> void registerLegacyServerbound$Internal(Class<?> clazz, StreamDecoder<FriendlyByteBuf, ?> factory);
}
