package fuzs.puzzleslib.fabric.impl.network;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.CustomPacketPayloadAdapter;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializers;
import fuzs.puzzleslib.fabric.impl.core.FabricProxy;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistryImpl;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class NetworkHandlerFabricV3 extends NetworkHandlerRegistryImpl {
    private boolean building = true;

    public NetworkHandlerFabricV3(ResourceLocation channelName) {
        super(channelName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz, PayloadTypeRegistry.playS2C(), ((FabricProxy) Proxy.INSTANCE)::registerClientReceiver);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Record & ServerboundMessage<T>> void registerServerbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz, PayloadTypeRegistry.playC2S(), ((FabricProxy) Proxy.INSTANCE)::registerServerReceiver);
    }

    private <T> void register(Class<T> clazz, PayloadTypeRegistry<RegistryFriendlyByteBuf> registry, Consumer<CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>>> receiverRegistrar) {
        if (this.building) throw new IllegalStateException("channel is null");
        CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type = this.registerMessageType(clazz);
        registry.register(type, MessageSerializers.findByType(clazz).streamCodec(type));
        receiverRegistrar.accept(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends MessageV2<T>> void registerLegacyClientbound$Internal(Class<?> clazz, Function<FriendlyByteBuf, ?> factory) {
        this.registerLegacy((Class<T>) clazz, (Function<FriendlyByteBuf, T>) factory, PayloadTypeRegistry.playS2C(), ((FabricProxy) Proxy.INSTANCE)::registerLegacyClientReceiver);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends MessageV2<T>> void registerLegacyServerbound$Internal(Class<?> clazz, Function<FriendlyByteBuf, ?> factory) {
        this.registerLegacy((Class<T>) clazz, (Function<FriendlyByteBuf, T>) factory, PayloadTypeRegistry.playC2S(), ((FabricProxy) Proxy.INSTANCE)::registerLegacyServerReceiver);
    }

    private <T extends MessageV2<T>> void registerLegacy(Class<T> clazz, Function<FriendlyByteBuf, T> factory, PayloadTypeRegistry<RegistryFriendlyByteBuf> registry, Consumer<CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>>> receiverRegistrar) {
        this.registerSerializer(clazz, (FriendlyByteBuf buf, T message) -> {
            message.write(buf);
        }, factory);
        this.register(clazz, registry, receiverRegistrar);
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
