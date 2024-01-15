package fuzs.puzzleslib.neoforge.impl.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistryImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
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
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPlayPayloadHandler;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.registration.IDirectionAwarePayloadHandlerBuilder;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class NetworkHandlerNeoForgeV2 implements NetworkHandlerV2 {
    private final Map<Class<?>, ResourceLocation> messageNames = Maps.newIdentityHashMap();
    private final AtomicInteger discriminator = new AtomicInteger();
    private final ResourceLocation channelName;
    private final Queue<Consumer<IPayloadRegistrar>> messageRegisters = Lists.newLinkedList();

    public NetworkHandlerNeoForgeV2(ResourceLocation channelName, boolean optional) {
        this.channelName = channelName;
        NeoForgeModContainerHelper.getOptionalModEventBus(this.channelName.getNamespace()).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final RegisterPayloadHandlerEvent evt) -> {
                IPayloadRegistrar registrar = evt.registrar(this.channelName.toLanguageKey());
                if (optional) {
                    registrar = registrar.optional();
                } else {
                    int protocolVersion = NetworkHandlerRegistryImpl.getProtocolVersion(this.channelName.getNamespace());
                    registrar = registrar.versioned(String.valueOf(protocolVersion));
                }
                while (!this.messageRegisters.isEmpty()) {
                    this.messageRegisters.poll().accept(registrar);
                }
            });
        });
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerClientbound(Class<T> clazz, Function<FriendlyByteBuf, T> factory) {
        this.register(clazz, factory, LogicalSide.CLIENT, IDirectionAwarePayloadHandlerBuilder::client, IDirectionAwarePayloadHandlerBuilder::server);
        return this;
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerServerbound(Class<T> clazz, Function<FriendlyByteBuf, T> factory) {
        this.register(clazz, factory, LogicalSide.SERVER, IDirectionAwarePayloadHandlerBuilder::server, IDirectionAwarePayloadHandlerBuilder::client);
        return this;
    }

    private <T extends MessageV2<T>> void register(Class<T> clazz, Function<FriendlyByteBuf, T> factory, LogicalSide receptionSide, NetworkHandlerNeoForgeV3.GenericPayloadHandler<T> receiverHandler, NetworkHandlerNeoForgeV3.GenericPayloadHandler<T> senderHandler) {
        this.messageRegisters.offer((IPayloadRegistrar registrar) -> {
            registrar.play(this.registerMessageType(clazz), (FriendlyByteBuf friendlyByteBuf) -> {
                return new NetworkHandlerNeoForgeV3.MessageHolder<>(factory.apply(friendlyByteBuf));
            }, (IDirectionAwarePayloadHandlerBuilder<NetworkHandlerNeoForgeV3.MessageHolder<T>, IPlayPayloadHandler<NetworkHandlerNeoForgeV3.MessageHolder<T>>> builder) -> {
                receiverHandler.accept(builder, (NetworkHandlerNeoForgeV3.MessageHolder<T> payload, PlayPayloadContext context) -> {
                    context.workHandler().submitAsync(() -> {
                        Player player = context.player().orElseThrow(() -> new NullPointerException("player is null"));
                        payload.message().makeHandler().handle(payload.message(), player, LogicalSidedProvider.WORKQUEUE.get(receptionSide));
                    }).exceptionally(throwable -> {
                        context.packetHandler().disconnect(Component.literal("Receiving %s from %s failed: %s".formatted(clazz.getSimpleName(), this.channelName.getNamespace(), throwable.getMessage())));
                        return null;
                    });
                });
                senderHandler.accept(builder, (NetworkHandlerNeoForgeV3.MessageHolder<T> payload, PlayPayloadContext context) -> {
                    context.packetHandler().disconnect(Component.literal("Receiving %s from %s on wrong side!".formatted(clazz.getSimpleName(), this.channelName.getNamespace())));
                });
            });
        });
    }

    protected ResourceLocation registerMessageType(Class<?> clazz) {
        ResourceLocation messageName = new ResourceLocation(this.channelName.getNamespace(), this.channelName.getPath() + "/" + this.discriminator.getAndIncrement());
        if (this.messageNames.put(clazz, messageName) != null) {
            throw new IllegalStateException("Duplicate message of type %s".formatted(clazz));
        }
        return messageName;
    }

    @Override
    public Packet<ClientCommonPacketListener> toClientboundPacket(MessageV2<?> message) {
        Objects.requireNonNull(message, "message is null");
        return this.toPacket(ClientboundCustomPayloadPacket::new, message);
    }

    @Override
    public Packet<ServerCommonPacketListener> toServerboundPacket(MessageV2<?> message) {
        Objects.requireNonNull(message, "message is null");
        return this.toPacket(ServerboundCustomPayloadPacket::new, message);
    }

    private <S extends PacketListener> Packet<S> toPacket(Function<CustomPacketPayload, Packet<S>> packetFactory, MessageV2<?> message) {
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

    protected <S extends PacketListener> Packet<S> toPacket(MessageV2<?> message, BiFunction<ResourceLocation, Consumer<FriendlyByteBuf>, Packet<S>> packetFactory) {
        ResourceLocation channelName = this.messageNames.get(message.getClass());
        Objects.requireNonNull(channelName, "Unknown message of type %s".formatted(message.getClass()));
        return packetFactory.apply(channelName, message::write);
    }
}
