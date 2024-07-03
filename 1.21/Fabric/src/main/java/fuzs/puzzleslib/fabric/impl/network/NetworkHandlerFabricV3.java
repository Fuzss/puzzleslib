package fuzs.puzzleslib.fabric.impl.network;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializer;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializers;
import fuzs.puzzleslib.fabric.impl.core.FabricProxy;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistryImpl;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class NetworkHandlerFabricV3 extends NetworkHandlerRegistryImpl {
    private boolean building = true;

    public NetworkHandlerFabricV3(ResourceLocation channelName) {
        super(channelName);
    }

    @SuppressWarnings("unchecked")
    public <T extends Record & ClientboundMessage<T>> void registerClientbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz, ((FabricProxy) Proxy.INSTANCE)::registerClientReceiver);
    }

    @SuppressWarnings("unchecked")
    public <T extends Record & ServerboundMessage<T>> void registerServerbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz, ((FabricProxy) Proxy.INSTANCE)::registerServerReceiver);
    }

    private <T> void register(Class<T> clazz, BiConsumer<ResourceLocation, Function<FriendlyByteBuf, T>> register) {
        if (this.building) throw new IllegalStateException("channel is null");
        ResourceLocation resourceLocation = this.registerMessageType(clazz);
        register.accept(resourceLocation, MessageSerializers.findByType(clazz)::read);

        CustomPacketPayload.Type<CustomPacketPayload> id = new CustomPacketPayload.Type<>(resourceLocation);
        MessageSerializer<T> byType = MessageSerializers.findByType(clazz);
        StreamCodec<? super RegistryFriendlyByteBuf, ClientboundCustomPayloadPacket> codec = CustomPacketPayload.codec((ClientboundCustomPayloadPacket object, RegistryFriendlyByteBuf object2) -> {
            byType.write(object2, object.payload());
        }, (RegistryFriendlyByteBuf object) -> {
            CustomPacketPayloadAdapter<T> customPacketPayload = new CustomPacketPayloadAdapter<>(id,
                    byType.read(object)
            );
            return customPacketPayload;
        });
        PayloadTypeRegistry<RegistryFriendlyByteBuf> registry = PayloadTypeRegistry.playC2S();
        registry.register(id, codec);
    }

    record CustomPacketPayloadAdapter<T>(Type<? extends CustomPacketPayload> type, T message) implements CustomPacketPayload {

    }

    @Override
    public <T extends Record & ClientboundMessage<T>> Packet<ClientCommonPacketListener> toClientboundPacket(T message) {
        if (this.building) throw new IllegalStateException("channel is null");
        Objects.requireNonNull(message, "message is null");
        return this.toPacket(ServerPlayNetworking::createS2CPacket, message);
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> Packet<ServerCommonPacketListener> toServerboundPacket(T message) {
        if (this.building) throw new IllegalStateException("channel is null");
        Objects.requireNonNull(message, "message is null");
        return this.toPacket(ClientPlayNetworking::createC2SPacket, message);
    }

    private <T extends Record, S extends PacketListener> Packet<S> toPacket(Function<CustomPacketPayload, Packet<S>> packetFactory, T message) {
        return this.toPacket(message, (ResourceLocation resourceLocation, Consumer<FriendlyByteBuf> consumer) -> {
            PayloadTypeRegistry.playC2S().register(new CustomPacketPayload.Type<>(resourceLocation), )
            FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
            consumer.accept(friendlyByteBuf);
            return packetFactory.apply(resourceLocation, friendlyByteBuf);
        });
    }

    @Override
    public void build() {
        this.building = false;
        super.build();
    }
}
