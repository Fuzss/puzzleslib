package fuzs.puzzleslib.impl.network;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializers;
import fuzs.puzzleslib.impl.core.FabricProxy;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NetworkHandlerFabricV3 extends NetworkHandlerRegistryImpl {
    private final Map<Class<?>, ResourceLocation> messageChannelNames = Maps.newIdentityHashMap();
    private final AtomicInteger discriminator = new AtomicInteger();
    private boolean building = true;

    public NetworkHandlerFabricV3(String modId) {
        super(modId);
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
        if (!clazz.isRecord()) throw new IllegalArgumentException("Message of type %s is not a record".formatted(clazz));
        if (this.building) throw new IllegalStateException("channel is null");
        ResourceLocation channelName = this.nextIdentifier();
        if (this.messageChannelNames.put(clazz, channelName) != null) throw new IllegalStateException("Duplicate message of type %s".formatted(clazz));
        register.accept(channelName, MessageSerializers.findByType(clazz)::read);
    }

    private ResourceLocation nextIdentifier() {
        return new ResourceLocation(this.modId, "play/" + this.discriminator.getAndIncrement());
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> Packet<?> toClientboundPacket(T message) {
        if (this.building) throw new IllegalStateException("channel is null");
        return this.toPacket(ServerPlayNetworking::createS2CPacket, message);
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> Packet<?> toServerboundPacket(T message) {
        if (this.building) throw new IllegalStateException("channel is null");
        return this.toPacket(ClientPlayNetworking::createC2SPacket, message);
    }

    @SuppressWarnings("unchecked")
    private <T extends Record> Packet<?> toPacket(BiFunction<ResourceLocation, FriendlyByteBuf, Packet<?>> packetFactory, T message) {
        Class<T> clazz = (Class<T>) message.getClass();
        if (!clazz.isRecord()) throw new IllegalArgumentException("Message of type %s is not a record".formatted(clazz));
        FriendlyByteBuf byteBuf = PacketByteBufs.create();
        MessageSerializers.findByType(clazz).write(byteBuf, message);
        ResourceLocation channelName = this.messageChannelNames.get(clazz);
        Objects.requireNonNull(channelName, "Unknown message of type %s".formatted(clazz));
        return packetFactory.apply(channelName, byteBuf);
    }

    @Override
    public void build() {
        this.building = false;
        super.build();
    }
}
