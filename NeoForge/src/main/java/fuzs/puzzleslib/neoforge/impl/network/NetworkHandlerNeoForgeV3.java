package fuzs.puzzleslib.neoforge.impl.network;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializers;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistryImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.core.NeoForgeProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPlayPayloadHandler;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.registration.IDirectionAwarePayloadHandlerBuilder;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class NetworkHandlerNeoForgeV3 extends NetworkHandlerRegistryImpl {
    @Nullable
    private IPayloadRegistrar channel;

    public NetworkHandlerNeoForgeV3(ResourceLocation channelName) {
        super(channelName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Record & ClientboundMessage<T>> void registerClientbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz, ((NeoForgeProxy) Proxy.INSTANCE)::registerClientReceiverV2, IDirectionAwarePayloadHandlerBuilder::client, IDirectionAwarePayloadHandlerBuilder::server);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Record & ServerboundMessage<T>> void registerServerbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz, ((NeoForgeProxy) Proxy.INSTANCE)::registerServerReceiverV2, IDirectionAwarePayloadHandlerBuilder::server, IDirectionAwarePayloadHandlerBuilder::client);
    }

    private <T> void register(Class<T> clazz, BiFunction<T, PlayPayloadContext, CompletableFuture<Void>> handle, GenericPayloadHandler<T> receiverHandler, GenericPayloadHandler<T> senderHandler) {
        Objects.requireNonNull(this.channel, "channel is null");
        Function<FriendlyByteBuf, T> factory = MessageSerializers.findByType(clazz)::read;
        this.channel.play(this.registerMessageType(clazz), (FriendlyByteBuf friendlyByteBuf) -> {
            return new MessageHolder<>(factory.apply(friendlyByteBuf));
        }, (IDirectionAwarePayloadHandlerBuilder<MessageHolder<T>, IPlayPayloadHandler<MessageHolder<T>>> builder) -> {
            receiverHandler.accept(builder, (MessageHolder<T> payload, PlayPayloadContext context) -> {
                handle.apply(payload.message(), context).exceptionally(throwable -> {
                    context.packetHandler().disconnect(Component.literal("Receiving %s from %s failed: %s".formatted(clazz.getSimpleName(), this.channelName.getNamespace(), throwable.getMessage())));
                    return null;
                });
            });
            senderHandler.accept(builder, (MessageHolder<T> payload, PlayPayloadContext context) -> {
                context.packetHandler().disconnect(Component.literal("Receiving %s from %s on wrong side!".formatted(clazz.getSimpleName(), this.channelName.getNamespace())));
            });
        });
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> Packet<ClientCommonPacketListener> toClientboundPacket(T message) {
        Objects.requireNonNull(message, "message is null");
        return this.toPacket(ClientboundCustomPayloadPacket::new, message);
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> Packet<ServerCommonPacketListener> toServerboundPacket(T message) {
        Objects.requireNonNull(message, "message is null");
        return this.toPacket(ServerboundCustomPayloadPacket::new, message);
    }

    private <T extends Record, S extends PacketListener> Packet<S> toPacket(Function<CustomPacketPayload, Packet<S>> packetFactory, T message) {
        return this.toPacket(message, (ResourceLocation resourceLocation, Consumer<FriendlyByteBuf> consumer) -> {
            return packetFactory.apply(new CustomPacketPayload() {

                @Override
                public void write(FriendlyByteBuf buffer) {
                    consumer.accept(buffer);
                }

                @Override
                public ResourceLocation id() {
                    return resourceLocation;
                }
            });
        });
    }

    @Override
    public void build() {
        NeoForgeModContainerHelper.getOptionalModEventBus(this.channelName.getNamespace()).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final RegisterPayloadHandlerEvent evt) -> {
                if (this.channel != null) throw new IllegalStateException("channel is already built");
                IPayloadRegistrar registrar = evt.registrar(this.channelName.toLanguageKey());
                if (this.optional) {
                    this.channel = registrar.optional();
                } else {
                    int protocolVersion = getModProtocolVersion(this.channelName.getNamespace());
                    this.channel = registrar.versioned(String.valueOf(protocolVersion));
                }
                super.build();
                this.channel = null;
            });
        });
    }

    private record MessageHolder<T>(T message) implements CustomPacketPayload {

        @Override
        public void write(FriendlyByteBuf buffer) {
            // this is only used for the read message on the receiving side being passed on to being handled
            // since we do not support sending a response directly this method can never be called in that scenario
            throw new RuntimeException();
        }

        @Override
        public ResourceLocation id() {
            // this is only used for the read message on the receiving side being passed on to being handled
            // since we do not support sending a response directly this method can never be called in that scenario
            throw new RuntimeException();
        }
    }

    private interface GenericPayloadHandler<T> extends BiConsumer<IDirectionAwarePayloadHandlerBuilder<MessageHolder<T>, IPlayPayloadHandler<MessageHolder<T>>>, IPlayPayloadHandler<MessageHolder<T>>> {

    }
}
