package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.MessageV3;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.impl.network.codec.CustomPacketPayloadAdapter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.Packet;
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

public abstract class NetworkHandlerRegistryImpl implements NetworkHandler.Builder {
    private final Map<Class<?>, CustomPacketPayload.Type<CustomPacketPayloadAdapter<?>>> messageNames = new IdentityHashMap<>();
    private final Map<Class<?>, StreamDecoder<FriendlyByteBuf, ?>> clientboundMessages = new LinkedHashMap<>();
    private final Map<Class<?>, StreamDecoder<FriendlyByteBuf, ?>> serverboundMessages = new LinkedHashMap<>();
    protected final AtomicInteger discriminator = new AtomicInteger();
    protected final ResourceLocation channelName;
    protected boolean optional;

    protected NetworkHandlerRegistryImpl(ResourceLocation channelName) {
        this.channelName = channelName;
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> Builder registerClientbound(Class<T> clazz) {
        this.registerMessage(this.clientboundMessages, clazz, null);
        return this;
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> Builder registerServerbound(Class<T> clazz) {
        this.registerMessage(this.serverboundMessages, clazz, null);
        return this;
    }

    @Override
    public <T extends MessageV2<T>> Builder registerLegacyClientbound(Class<T> clazz, StreamDecoder<FriendlyByteBuf, T> factory) {
        this.registerMessage(this.clientboundMessages, clazz, factory);
        return this;
    }

    @Override
    public <T extends MessageV2<T>> Builder registerLegacyServerbound(Class<T> clazz, StreamDecoder<FriendlyByteBuf, T> factory) {
        this.registerMessage(this.serverboundMessages, clazz, factory);
        return this;
    }

    private void registerMessage(Map<Class<?>, StreamDecoder<FriendlyByteBuf, ?>> messages, Class<?> clazz, @Nullable StreamDecoder<FriendlyByteBuf, ?> factory) {
        if (messages.containsKey(clazz)) {
            throw new IllegalStateException("Duplicate message of type " + clazz);
        } else {
            messages.put(clazz, factory);
        }
    }

    @Override
    public Builder optional() {
        this.optional = true;
        return this;
    }

    @Override
    public void build() {
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

    protected BiConsumer<Throwable, Consumer<Component>> disconnectExceptionally(Class<?> clazz) {
        return (Throwable throwable, Consumer<Component> consumer) -> {
            String modName = ModContainer.getDisplayName(this.channelName.getNamespace());
            consumer.accept(Component.literal("Receiving %s from %s failed: %s".formatted(clazz.getSimpleName(), modName, throwable.getMessage())));
        };
    }

    protected Consumer<Consumer<Component>> disconnectWrongSide(Class<?> clazz) {
        return (Consumer<Component> consumer) -> {
            String modName = ModContainer.getDisplayName(this.channelName.getNamespace());
            consumer.accept(Component.literal("Receiving %s from %s on wrong side!".formatted(clazz.getSimpleName(), modName)));
        };
    }

    @SuppressWarnings("unchecked")
    protected <T> CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> registerMessageType(Class<T> clazz) {
        ResourceLocation resourceLocation = this.channelName.withPath((String path) -> path + "/" + this.discriminator.getAndIncrement());
        CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type = new CustomPacketPayload.Type<>(resourceLocation);
        this.messageNames.put(clazz, (CustomPacketPayload.Type<CustomPacketPayloadAdapter<?>>) (CustomPacketPayload.Type<?>) type);
        return type;
    }

    @SuppressWarnings("unchecked")
    protected <T1 extends MessageV3<T2, ?>, T2, L extends PacketListener> Packet<L> toPacket(Function<CustomPacketPayload, Packet<L>> packetFactory, T1 message) {
        Class<T2> clazz = (Class<T2>) message.unwrap().getClass();
        CustomPacketPayload.Type<CustomPacketPayloadAdapter<T2>> type = (CustomPacketPayload.Type<CustomPacketPayloadAdapter<T2>>) (CustomPacketPayload.Type<?>) this.messageNames.get(
                clazz);
        Objects.requireNonNull(type, "Unknown message of type " + clazz);
        return packetFactory.apply(new CustomPacketPayloadAdapterImpl<>(type, message.unwrap()));
    }

    protected abstract <T extends Record & ClientboundMessage<T>> void registerClientbound$Internal(Class<?> clazz);

    protected abstract <T extends Record & ServerboundMessage<T>> void registerServerbound$Internal(Class<?> clazz);

    protected abstract <T extends MessageV2<T>> void registerLegacyClientbound$Internal(Class<?> clazz, StreamDecoder<FriendlyByteBuf, ?> factory);

    protected abstract <T extends MessageV2<T>> void registerLegacyServerbound$Internal(Class<?> clazz, StreamDecoder<FriendlyByteBuf, ?> factory);
}
