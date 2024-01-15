package fuzs.puzzleslib.fabric.impl.network;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.fabric.impl.core.FabricProxy;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NetworkHandlerFabricV2 implements NetworkHandlerV2 {
    private final Map<Class<? extends MessageV2<?>>, Map.Entry<ResourceLocation, PacketFlow>> messageNames = Maps.newIdentityHashMap();
    private final AtomicInteger discriminator = new AtomicInteger();
    private final ResourceLocation channelName;

    public NetworkHandlerFabricV2(ResourceLocation channelName) {
        this.channelName = channelName;
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerClientbound(Class<T> clazz, Function<FriendlyByteBuf, T> factory) {
        ResourceLocation channelName = this.registerMessageType(clazz, PacketFlow.CLIENTBOUND);
        ((FabricProxy) Proxy.INSTANCE).registerLegacyClientReceiver(channelName, factory);
        return this;
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerServerbound(Class<T> clazz, Function<FriendlyByteBuf, T> factory) {
        ResourceLocation channelName = this.registerMessageType(clazz, PacketFlow.SERVERBOUND);
        ((FabricProxy) Proxy.INSTANCE).registerLegacyServerReceiver(channelName, factory);
        return this;
    }

    private ResourceLocation registerMessageType(Class<? extends MessageV2<?>> clazz, PacketFlow packetFlow) {
        ResourceLocation messageName = new ResourceLocation(this.channelName.getNamespace(), this.channelName.getPath() + "/" + this.discriminator.getAndIncrement());
        this.messageNames.put(clazz, Map.entry(messageName, packetFlow));
        return messageName;
    }

    @Override
    public Packet<ClientCommonPacketListener> toClientboundPacket(MessageV2<?> message) {
        if (this.messageNames.get(message.getClass()).getValue() == PacketFlow.SERVERBOUND) {
            throw new IllegalStateException("Attempted sending clientbound message to server side");
        } else {
            return this.toPacket(ServerPlayNetworking::createS2CPacket, message);
        }
    }

    @Override
    public Packet<ServerCommonPacketListener> toServerboundPacket(MessageV2<?> message) {
        if (this.messageNames.get(message.getClass()).getValue() == PacketFlow.CLIENTBOUND) {
            throw new IllegalStateException("Attempted sending serverbound message to client side");
        } else {
            return this.toPacket(ClientPlayNetworking::createC2SPacket, message);
        }
    }

    private <T extends PacketListener> Packet<T> toPacket(BiFunction<ResourceLocation, FriendlyByteBuf, Packet<T>> packetFactory, MessageV2<?> message) {
        ResourceLocation identifier = this.messageNames.get(message.getClass()).getKey();
        FriendlyByteBuf byteBuf = PacketByteBufs.create();
        message.write(byteBuf);
        return packetFactory.apply(identifier, byteBuf);
    }
}
