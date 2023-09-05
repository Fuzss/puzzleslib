package fuzs.puzzleslib.impl.network;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v2.MessageDirection;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.impl.core.FabricProxy;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkHandlerFabricV2 implements NetworkHandlerV2 {
    private final Map<Class<? extends MessageV2<?>>, MessageData> messages = Maps.newIdentityHashMap();
    private final String modId;
    private final AtomicInteger discriminator = new AtomicInteger();

    public NetworkHandlerFabricV2(String modId) {
        this.modId = modId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends MessageV2<T>> void register(Class<? extends T> clazz, Supplier<T> supplier, MessageDirection direction) {
        this.register((Class<T>) clazz, direction == MessageDirection.TO_CLIENT, NetworkHandlerImplHelper.getDirectMessageDecoder(supplier));
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerClientbound(Class<T> clazz) {
        this.register(clazz, true, NetworkHandlerImplHelper.getMessageDecoder(clazz));
        return this;
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerServerbound(Class<T> clazz) {
        this.register(clazz, false, NetworkHandlerImplHelper.getMessageDecoder(clazz));
        return this;
    }

    private <T extends MessageV2<T>> void register(Class<T> clazz, boolean toClient, Function<FriendlyByteBuf, T> decode) {
        ResourceLocation channelName = this.nextIdentifier();
        this.messages.put(clazz, new MessageData(clazz, channelName, toClient));
        BiConsumer<ResourceLocation, Function<FriendlyByteBuf, T>> registrar;
        if (toClient) {
            registrar = ((FabricProxy) Proxy.INSTANCE)::registerLegacyClientReceiver;
        } else {
            registrar = ((FabricProxy) Proxy.INSTANCE)::registerLegacyServerReceiver;
        }
        registrar.accept(channelName, decode);
    }

    private ResourceLocation nextIdentifier() {
        return new ResourceLocation(this.modId, "play/" + this.discriminator.getAndIncrement());
    }

    @Override
    public Packet<ServerGamePacketListener> toServerboundPacket(MessageV2<?> message) {
        if (this.messages.get(message.getClass()).toClient()) throw new IllegalStateException("Attempted sending serverbound message to client side");
        return this.toPacket(ClientPlayNetworking::createC2SPacket, message);
    }

    @Override
    public Packet<ClientGamePacketListener> toClientboundPacket(MessageV2<?> message) {
        if (!this.messages.get(message.getClass()).toClient()) throw new IllegalStateException("Attempted sending clientbound message to server side");
        return this.toPacket(ServerPlayNetworking::createS2CPacket, message);
    }

    private <T extends PacketListener> Packet<T> toPacket(BiFunction<ResourceLocation, FriendlyByteBuf, Packet<T>> packetFactory, MessageV2<?> message) {
        ResourceLocation identifier = this.messages.get(message.getClass()).identifier();
        FriendlyByteBuf byteBuf = PacketByteBufs.create();
        message.write(byteBuf);
        return packetFactory.apply(identifier, byteBuf);
    }

    private record MessageData(Class<? extends MessageV2<?>> clazz, ResourceLocation identifier, boolean toClient) {

    }
}
