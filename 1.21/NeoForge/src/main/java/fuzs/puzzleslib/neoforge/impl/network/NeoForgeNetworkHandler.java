package fuzs.puzzleslib.neoforge.impl.network;

import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.MessageV3;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.CustomPacketPayloadAdapter;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializers;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistryImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.core.NeoForgeProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NeoForgeNetworkHandler extends NetworkHandlerRegistryImpl {
    @Nullable
    private PayloadRegistrar channel;

    public NeoForgeNetworkHandler(ResourceLocation channelName) {
        super(channelName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz, PacketFlow.CLIENTBOUND,
                (CustomPacketPayloadAdapter<T> payload, IPayloadContext context) -> {
                    return ((NeoForgeProxy) Proxy.INSTANCE).registerClientReceiver(payload, context, MessageV3::unwrap);
                }
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Record & ServerboundMessage<T>> void registerServerbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz, PacketFlow.SERVERBOUND,
                (CustomPacketPayloadAdapter<T> payload, IPayloadContext context) -> {
                    return ((NeoForgeProxy) Proxy.INSTANCE).registerServerReceiver(payload, context, MessageV3::unwrap);
                }
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends MessageV2<T>> void registerLegacyClientbound$Internal(Class<?> clazz, Function<FriendlyByteBuf, ?> factory) {
        this.registerLegacy((Class<T>) clazz, (Function<FriendlyByteBuf, T>) factory, PacketFlow.CLIENTBOUND,
                (CustomPacketPayloadAdapter<T> payload, IPayloadContext context) -> {
                    return ((NeoForgeProxy) Proxy.INSTANCE).registerClientReceiver(payload, context, MessageV2::toClientboundMessage);
                });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends MessageV2<T>> void registerLegacyServerbound$Internal(Class<?> clazz, Function<FriendlyByteBuf, ?> factory) {
        this.registerLegacy((Class<T>) clazz, (Function<FriendlyByteBuf, T>) factory, PacketFlow.SERVERBOUND,
                (CustomPacketPayloadAdapter<T> payload, IPayloadContext context) -> {
            return ((NeoForgeProxy) Proxy.INSTANCE).registerServerReceiver(payload, context, MessageV2::toServerboundMessage);
        });
    }

    private <T extends MessageV2<T>> void registerLegacy(Class<T> clazz, Function<FriendlyByteBuf, T> factory, PacketFlow packetFlow, BiFunction<CustomPacketPayloadAdapter<T>, IPayloadContext, CompletableFuture<Void>> receiverRegistrar) {
        this.registerSerializer(clazz, (FriendlyByteBuf buf, T message) -> {
            message.write(buf);
        }, factory);
        this.register(clazz, packetFlow, receiverRegistrar);
    }

    private <T> void register(Class<T> clazz, PacketFlow packetFlow, BiFunction<CustomPacketPayloadAdapter<T>, IPayloadContext, CompletableFuture<Void>> receiverRegistrar) {
        Objects.requireNonNull(this.channel, "channel is null");
        CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type = this.registerMessageType(clazz);
        StreamCodec<? super RegistryFriendlyByteBuf, CustomPacketPayloadAdapter<T>> streamCodec = MessageSerializers.findByType(
                clazz).streamCodec(type);
        this.channel.playBidirectional(type, streamCodec, (CustomPacketPayloadAdapter<T> payload, IPayloadContext context) -> {
            if (context.flow() == packetFlow) {
                receiverRegistrar.apply(payload, context).exceptionally((Throwable throwable) -> {
                    String modName = ModContainer.getDisplayName(this.channelName.getNamespace());
                    context.disconnect(Component.literal("Receiving %s from %s failed: %s".formatted(clazz.getSimpleName(), modName, throwable.getMessage())));
                    return null;
                });
            } else {
                String modName = ModContainer.getDisplayName(this.channelName.getNamespace());
                context.disconnect(Component.literal("Receiving %s from %s on wrong side!".formatted(clazz.getSimpleName(), modName)));
            }
        });
    }

    @Override
    public <T> Packet<ClientCommonPacketListener> toClientboundPacket(ClientboundMessage<T> message) {
        Objects.requireNonNull(message, "message is null");
        return this.toPacket(ClientboundCustomPayloadPacket::new, message);
    }

    @Override
    public <T> Packet<ServerCommonPacketListener> toServerboundPacket(ServerboundMessage<T> message) {
        Objects.requireNonNull(message, "message is null");
        return this.toPacket(ServerboundCustomPayloadPacket::new, message);
    }

    @Override
    public void build() {
        NeoForgeModContainerHelper.getOptionalModEventBus(this.channelName.getNamespace()).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final RegisterPayloadHandlersEvent evt) -> {
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
}
