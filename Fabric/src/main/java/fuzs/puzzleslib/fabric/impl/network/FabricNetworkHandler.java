package fuzs.puzzleslib.fabric.impl.network;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.MessageV3;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.fabric.impl.core.FabricProxy;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistryImpl;
import fuzs.puzzleslib.impl.network.codec.CustomPacketPayloadAdapter;
import fuzs.puzzleslib.impl.network.codec.StreamCodecRegistryImpl;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FabricNetworkHandler extends NetworkHandlerRegistryImpl {
    private boolean building = true;

    public FabricNetworkHandler(ResourceLocation channelName) {
        super(channelName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz, PayloadTypeRegistry.playS2C(),
                (CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type, BiConsumer<Throwable, Consumer<Component>> disconnectExceptionally) -> {
                    ((FabricProxy) Proxy.INSTANCE).registerClientReceiver(type, disconnectExceptionally,
                            MessageV3::unwrap
                    );
                }
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Record & ServerboundMessage<T>> void registerServerbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz, PayloadTypeRegistry.playC2S(),
                (CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type, BiConsumer<Throwable, Consumer<Component>> disconnectExceptionally) -> {
                    ((FabricProxy) Proxy.INSTANCE).registerServerReceiver(type, disconnectExceptionally,
                            MessageV3::unwrap
                    );
                }
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends MessageV2<T>> void registerLegacyClientbound$Internal(Class<?> clazz, StreamDecoder<FriendlyByteBuf, ?> factory) {
        this.registerLegacy((Class<T>) clazz, (StreamDecoder<FriendlyByteBuf, T>) factory,
                PayloadTypeRegistry.playS2C(),
                (CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type, BiConsumer<Throwable, Consumer<Component>> disconnectExceptionally) -> {
                    ((FabricProxy) Proxy.INSTANCE).registerClientReceiver(type, disconnectExceptionally,
                            MessageV2::toClientboundMessage
                    );
                }
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends MessageV2<T>> void registerLegacyServerbound$Internal(Class<?> clazz, StreamDecoder<FriendlyByteBuf, ?> factory) {
        this.registerLegacy((Class<T>) clazz, (StreamDecoder<FriendlyByteBuf, T>) factory,
                PayloadTypeRegistry.playC2S(),
                (CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type, BiConsumer<Throwable, Consumer<Component>> disconnectExceptionally) -> {
                    ((FabricProxy) Proxy.INSTANCE).registerServerReceiver(type, disconnectExceptionally,
                            MessageV2::toServerboundMessage
                    );
                }
        );
    }

    private <T extends MessageV2<T>> void registerLegacy(Class<T> clazz, StreamDecoder<FriendlyByteBuf, T> factory, PayloadTypeRegistry<RegistryFriendlyByteBuf> registry, BiConsumer<CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>>, BiConsumer<Throwable, Consumer<Component>>> receiverRegistrar) {
        this.registerSerializer(clazz, (FriendlyByteBuf buf, T message) -> {
            message.write(buf);
        }, factory);
        this.register(clazz, registry, receiverRegistrar);
    }

    private <T> void register(Class<T> clazz, PayloadTypeRegistry<RegistryFriendlyByteBuf> registry, BiConsumer<CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>>, BiConsumer<Throwable, Consumer<Component>>> receiverRegistrar) {
        if (this.building) throw new IllegalStateException("channel is null");
        CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type = this.registerMessageType(clazz);
        StreamCodec<? super RegistryFriendlyByteBuf, CustomPacketPayloadAdapter<T>> streamCodec = CustomPacketPayloadAdapter.streamCodec(
                type, StreamCodecRegistryImpl.fromType(clazz));
        registry.register(type, streamCodec);
        receiverRegistrar.accept(type, this.disconnectExceptionally(clazz));
    }

    @Override
    public <T> Packet<ClientCommonPacketListener> toClientboundPacket(ClientboundMessage<T> message) {
        if (this.building) throw new IllegalStateException("channel is null");
        Objects.requireNonNull(message, "message is null");
        return this.toPacket(ServerPlayNetworking::createS2CPacket, message);
    }

    @Override
    public <T> Packet<ServerCommonPacketListener> toServerboundPacket(ServerboundMessage<T> message) {
        if (this.building) throw new IllegalStateException("channel is null");
        Objects.requireNonNull(message, "message is null");
        return this.toPacket(ClientPlayNetworking::createC2SPacket, message);
    }

    @Override
    public void build() {
        this.building = false;
        super.build();
    }
}
