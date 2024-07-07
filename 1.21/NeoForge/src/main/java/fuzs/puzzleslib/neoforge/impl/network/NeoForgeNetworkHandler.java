package fuzs.puzzleslib.neoforge.impl.network;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.MessageV3;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistryImpl;
import fuzs.puzzleslib.impl.network.codec.CustomPacketPayloadAdapter;
import fuzs.puzzleslib.impl.network.codec.StreamCodecRegistryImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.core.NeoForgeProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
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

public class NeoForgeNetworkHandler extends NetworkHandlerRegistryImpl {
    @Nullable
    private PayloadRegistrar channel;

    public NeoForgeNetworkHandler(ResourceLocation channelName) {
        super(channelName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz,
                PacketFlow.CLIENTBOUND,
                (CustomPacketPayloadAdapter<T> payload, IPayloadContext context) -> {
                    return ((NeoForgeProxy) Proxy.INSTANCE).registerClientReceiver(payload, context, MessageV3::unwrap);
                }
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Record & ServerboundMessage<T>> void registerServerbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz,
                PacketFlow.SERVERBOUND,
                (CustomPacketPayloadAdapter<T> payload, IPayloadContext context) -> {
                    return ((NeoForgeProxy) Proxy.INSTANCE).registerServerReceiver(payload, context, MessageV3::unwrap);
                }
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends MessageV2<T>> void registerLegacyClientbound$Internal(Class<?> clazz, StreamDecoder<FriendlyByteBuf, ?> factory) {
        this.registerLegacy((Class<T>) clazz,
                (StreamDecoder<FriendlyByteBuf, T>) factory,
                PacketFlow.CLIENTBOUND,
                (CustomPacketPayloadAdapter<T> payload, IPayloadContext context) -> {
                    return ((NeoForgeProxy) Proxy.INSTANCE).registerClientReceiver(payload,
                            context,
                            MessageV2::toClientboundMessage
                    );
                }
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends MessageV2<T>> void registerLegacyServerbound$Internal(Class<?> clazz, StreamDecoder<FriendlyByteBuf, ?> factory) {
        this.registerLegacy((Class<T>) clazz,
                (StreamDecoder<FriendlyByteBuf, T>) factory,
                PacketFlow.SERVERBOUND,
                (CustomPacketPayloadAdapter<T> payload, IPayloadContext context) -> {
                    return ((NeoForgeProxy) Proxy.INSTANCE).registerServerReceiver(payload,
                            context,
                            MessageV2::toServerboundMessage
                    );
                }
        );
    }

    private <T extends MessageV2<T>> void registerLegacy(Class<T> clazz, StreamDecoder<FriendlyByteBuf, T> decoder, PacketFlow packetFlow, BiFunction<CustomPacketPayloadAdapter<T>, IPayloadContext, CompletableFuture<Void>> receiverRegistrar) {
        this.registerSerializer(clazz, (FriendlyByteBuf buf, T message) -> {
            message.write(buf);
        }, decoder);
        this.register(clazz, packetFlow, receiverRegistrar);
    }

    private <T> void register(Class<T> clazz, PacketFlow packetFlow, BiFunction<CustomPacketPayloadAdapter<T>, IPayloadContext, CompletableFuture<Void>> receiverRegistrar) {
        Objects.requireNonNull(this.channel, "channel is null");
        CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type = this.registerMessageType(clazz);
        StreamCodec<? super RegistryFriendlyByteBuf, CustomPacketPayloadAdapter<T>> streamCodec = CustomPacketPayloadAdapter.streamCodec(
                type,
                StreamCodecRegistryImpl.fromType(clazz)
        );
        this.channel.playBidirectional(type,
                streamCodec,
                (CustomPacketPayloadAdapter<T> payload, IPayloadContext context) -> {
                    if (context.flow() == packetFlow) {
                        receiverRegistrar.apply(payload, context).exceptionally((Throwable throwable) -> {
                            this.disconnectExceptionally(clazz).accept(throwable, context::disconnect);
                            return null;
                        });
                    } else {
                        context.enqueueWork(() -> {
                            this.disconnectWrongSide(clazz).accept(context::disconnect);
                        });
                    }
                }
        );
    }

    @Override
    public <T> Packet<ClientCommonPacketListener> toClientboundPacket(ClientboundMessage<T> message) {
        Objects.requireNonNull(message, "message is null");
        return this.toPacket(CustomPacketPayload::toVanillaClientbound, message);
    }

    @Override
    public <T> Packet<ServerCommonPacketListener> toServerboundPacket(ServerboundMessage<T> message) {
        Objects.requireNonNull(message, "message is null");
        return this.toPacket(CustomPacketPayload::toVanillaServerbound, message);
    }

    @Override
    public void build() {
        NeoForgeModContainerHelper.getOptionalModEventBus(this.channelName.getNamespace())
                .ifPresent((IEventBus eventBus) -> {
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
