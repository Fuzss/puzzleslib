package fuzs.puzzleslib.fabric.impl.network;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.api.network.v3.serialization.CustomPacketPayloadAdapter;
import fuzs.puzzleslib.fabric.impl.core.FabricProxy;
import fuzs.puzzleslib.impl.network.CustomPacketPayloadAdapterImpl;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class NetworkHandlerFabricV2 implements NetworkHandlerV2 {
    private final Map<Class<?>, Map.Entry<CustomPacketPayload.Type<CustomPacketPayloadAdapter<?>>, PacketFlow>> messageNames = Maps.newIdentityHashMap();
    private final AtomicInteger discriminator = new AtomicInteger();
    private final ResourceLocation channelName;

    public NetworkHandlerFabricV2(ResourceLocation channelName) {
        this.channelName = channelName;
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerClientbound(Class<T> clazz, Function<FriendlyByteBuf, T> factory) {
        this.register(clazz,
                factory,
                PacketFlow.CLIENTBOUND,
                PayloadTypeRegistry.playS2C(),
                ((FabricProxy) Proxy.INSTANCE)::registerLegacyClientReceiver
        );
        return this;
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerServerbound(Class<T> clazz, Function<FriendlyByteBuf, T> factory) {
        this.register(clazz,
                factory,
                PacketFlow.SERVERBOUND,
                PayloadTypeRegistry.playC2S(),
                ((FabricProxy) Proxy.INSTANCE)::registerLegacyServerReceiver
        );
        return this;
    }

    private <T extends MessageV2<T>> void register(Class<T> clazz, Function<FriendlyByteBuf, T> factory, PacketFlow packetFlow, PayloadTypeRegistry<RegistryFriendlyByteBuf> registry, Consumer<CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>>> receiverRegistrar) {
        CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type = this.registerMessageType(clazz, packetFlow);
        StreamCodec<? super RegistryFriendlyByteBuf, CustomPacketPayloadAdapter<T>> streamCodec = CustomPacketPayload.codec(
                (CustomPacketPayloadAdapter<T> payload, RegistryFriendlyByteBuf buf) -> {
                    payload.unwrap().write(buf);
                },
                (RegistryFriendlyByteBuf buf) -> {
                    return new CustomPacketPayloadAdapterImpl<>(type, factory.apply(buf));
                }
        );
        registry.register(type, streamCodec);
        receiverRegistrar.accept(type);
    }

    private <T> CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> registerMessageType(Class<T> clazz, PacketFlow packetFlow) {
        ResourceLocation messageName = ResourceLocationHelper.fromNamespaceAndPath(this.channelName.toLanguageKey(),
                String.valueOf(this.discriminator.getAndIncrement())
        );
        CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type = new CustomPacketPayload.Type<>(messageName);
        this.messageNames.put(clazz,
                Map.entry((CustomPacketPayload.Type<CustomPacketPayloadAdapter<?>>) (CustomPacketPayload.Type<?>) type,
                        packetFlow
                )
        );
        return type;
    }

    @Override
    public Packet<ClientCommonPacketListener> toClientboundPacket(MessageV2<?> message) {
        if (this.messageNames.get(message.getClass()).getValue() == PacketFlow.SERVERBOUND) {
            String modName = ModLoaderEnvironment.INSTANCE.getModContainer(this.channelName.getNamespace())
                    .map(ModContainer::getDisplayName)
                    .orElse(this.channelName.getNamespace());
            throw new IllegalStateException("Sending %s from %s on wrong side!".formatted(message.getClass()
                    .getSimpleName(), modName));
        } else {
            return this.toPacket(ServerPlayNetworking::createS2CPacket, message);
        }
    }

    @Override
    public Packet<ServerCommonPacketListener> toServerboundPacket(MessageV2<?> message) {
        if (this.messageNames.get(message.getClass()).getValue() == PacketFlow.CLIENTBOUND) {
            String modName = ModLoaderEnvironment.INSTANCE.getModContainer(this.channelName.getNamespace())
                    .map(ModContainer::getDisplayName)
                    .orElse(this.channelName.getNamespace());
            throw new IllegalStateException("Sending %s from %s on wrong side!".formatted(message.getClass()
                    .getSimpleName(), modName));
        } else {
            return this.toPacket(ClientPlayNetworking::createC2SPacket, message);
        }
    }

    @SuppressWarnings("unchecked")
    private <T, L extends PacketListener> Packet<L> toPacket(Function<CustomPacketPayload, Packet<L>> packetFactory, T message) {
        Class<T> clazz = (Class<T>) message.getClass();
        Map.Entry<CustomPacketPayload.Type<CustomPacketPayloadAdapter<?>>, PacketFlow> entry = this.messageNames.get(
                clazz);
        Objects.requireNonNull(entry, "Unknown message of type " + clazz);
        CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type = (CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>>) (CustomPacketPayload.Type<?>) entry.getKey();
        return packetFactory.apply(new CustomPacketPayloadAdapterImpl<>(type, message));
    }
}
