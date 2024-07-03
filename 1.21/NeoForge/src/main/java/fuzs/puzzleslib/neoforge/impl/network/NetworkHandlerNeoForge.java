package fuzs.puzzleslib.neoforge.impl.network;

import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
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

public class NetworkHandlerNeoForge extends NetworkHandlerRegistryImpl {
    @Nullable
    private IPayloadRegistrar channel;

    public NetworkHandlerNeoForge(ResourceLocation channelName) {
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
        Function<FriendlyByteBuf, T> factory = MessageSerializers.findByType(clazz)::decode;
        ResourceLocation messageName = this.registerMessageType(clazz);
        this.channel.play(messageName, (FriendlyByteBuf friendlyByteBuf) -> {
            return new CustomPacketPayloadAdapter<>(factory.apply(friendlyByteBuf), messageName);
        }, (IDirectionAwarePayloadHandlerBuilder<CustomPacketPayloadAdapter<T>, IPlayPayloadHandler<CustomPacketPayloadAdapter<T>>> builder) -> {
            receiverHandler.accept(builder, (CustomPacketPayloadAdapter<T> payload, PlayPayloadContext context) -> {
                handle.apply(payload.message(), context).exceptionally(throwable -> {
                    String modName = ModLoaderEnvironment.INSTANCE.getModContainer(this.channelName.getNamespace()).map(ModContainer::getDisplayName).orElse(this.channelName.getNamespace());
                    context.packetHandler().disconnect(Component.literal("Receiving %s from %s failed: %s".formatted(clazz.getSimpleName(), modName, throwable.getMessage())));
                    return null;
                });
            });
            senderHandler.accept(builder, (CustomPacketPayloadAdapter<T> payload, PlayPayloadContext context) -> {
                String modName = ModLoaderEnvironment.INSTANCE.getModContainer(this.channelName.getNamespace()).map(ModContainer::getDisplayName).orElse(this.channelName.getNamespace());
                context.packetHandler().disconnect(Component.literal("Receiving %s from %s on wrong side!".formatted(clazz.getSimpleName(), modName)));
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
            return packetFactory.apply(new CustomPacketPayloadAdapter<>(message, resourceLocation, consumer));
        });
    }

    @Override
    public void build() {
        NeoForgeModContainerHelper.getOptionalModEventBus(this.channelName.getNamespace()).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final RegisterPayloadHandlerEvent evt) -> {
                if (this.channel != null) throw new IllegalStateException("channel is already built");
                this.channel = evt.registrar(this.channelName.toLanguageKey());
                if (this.optional) {
                    this.channel = this.channel.optional();
                }
                super.build();
                this.channel = null;
            });
        });
    }

    record CustomPacketPayloadAdapter<T>(T message, ResourceLocation messageName, Consumer<FriendlyByteBuf> writer) implements CustomPacketPayload {

        CustomPacketPayloadAdapter(T message, ResourceLocation messageName) {
            this(message, messageName, (FriendlyByteBuf friendlyByteBuf) -> {
                throw new UnsupportedOperationException();
            });
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            this.writer.accept(buffer);
        }

        @Override
        public ResourceLocation id() {
            return this.messageName;
        }
    }

    interface GenericPayloadHandler<T> extends BiConsumer<IDirectionAwarePayloadHandlerBuilder<CustomPacketPayloadAdapter<T>, IPlayPayloadHandler<CustomPacketPayloadAdapter<T>>>, IPlayPayloadHandler<CustomPacketPayloadAdapter<T>>> {

    }
}
