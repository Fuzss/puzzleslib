package fuzs.puzzleslib.fabric.impl.network;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializers;
import fuzs.puzzleslib.fabric.impl.core.FabricProxy;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistryImpl;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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
        register.accept(this.registerMessageType(clazz), MessageSerializers.findByType(clazz)::read);
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

    private <T extends Record, S extends PacketListener> Packet<S> toPacket(BiFunction<ResourceLocation, FriendlyByteBuf, Packet<S>> packetFactory, T message) {
        return this.toPacket(message, (ResourceLocation resourceLocation, Consumer<FriendlyByteBuf> consumer) -> {
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
