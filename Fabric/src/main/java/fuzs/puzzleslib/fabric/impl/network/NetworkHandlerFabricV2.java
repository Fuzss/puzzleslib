package fuzs.puzzleslib.fabric.impl.network;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.fabric.impl.core.FabricProxy;
import fuzs.puzzleslib.impl.network.NetworkHandlerImplHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NetworkHandlerFabricV2 implements NetworkHandlerV2 {
    private final Map<Class<? extends MessageV2<?>>, MessageData> messageNames = Maps.newIdentityHashMap();
    private final ResourceLocation channelName;
    private final AtomicInteger discriminator = new AtomicInteger();

    public NetworkHandlerFabricV2(ResourceLocation channelName) {
        this.channelName = channelName;
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
        ResourceLocation messageName = this.nextIdentifier();
        this.messageNames.put(clazz, new MessageData(clazz, messageName, toClient));
        BiConsumer<ResourceLocation, Function<FriendlyByteBuf, T>> registrar;
        if (toClient) {
            registrar = ((FabricProxy) Proxy.INSTANCE)::registerLegacyClientReceiver;
        } else {
            registrar = ((FabricProxy) Proxy.INSTANCE)::registerLegacyServerReceiver;
        }
        registrar.accept(messageName, decode);
    }

    private ResourceLocation nextIdentifier() {
        return new ResourceLocation(this.channelName.getNamespace(), this.channelName.getPath() + "/" + this.discriminator.getAndIncrement());
    }

    @Override
    public Packet<ServerCommonPacketListener> toServerboundPacket(MessageV2<?> message) {
        if (this.messageNames.get(message.getClass()).toClient()) throw new IllegalStateException("Attempted sending serverbound message to client side");
        return this.toPacket(ClientPlayNetworking::createC2SPacket, message);
    }

    @Override
    public Packet<ClientCommonPacketListener> toClientboundPacket(MessageV2<?> message) {
        if (!this.messageNames.get(message.getClass()).toClient()) throw new IllegalStateException("Attempted sending clientbound message to server side");
        return this.toPacket(ServerPlayNetworking::createS2CPacket, message);
    }

    private <T extends PacketListener> Packet<T> toPacket(BiFunction<ResourceLocation, FriendlyByteBuf, Packet<T>> packetFactory, MessageV2<?> message) {
        ResourceLocation identifier = this.messageNames.get(message.getClass()).identifier();
        FriendlyByteBuf byteBuf = PacketByteBufs.create();
        message.write(byteBuf);
        return packetFactory.apply(identifier, byteBuf);
    }

    private record MessageData(Class<? extends MessageV2<?>> clazz, ResourceLocation identifier, boolean toClient) {

    }
}
