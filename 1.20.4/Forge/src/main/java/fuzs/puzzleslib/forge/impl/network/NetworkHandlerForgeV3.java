package fuzs.puzzleslib.forge.impl.network;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializers;
import fuzs.puzzleslib.forge.impl.core.ForgeProxy;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistryImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.*;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class NetworkHandlerForgeV3 extends NetworkHandlerRegistryImpl {
    private static final int PROTOCOL_VERSION = 1;

    private SimpleChannel channel;
    private final AtomicInteger discriminator = new AtomicInteger();

    public NetworkHandlerForgeV3(ResourceLocation channelIdentifier) {
        super(channelIdentifier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Record & ClientboundMessage<T>> void registerClientbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz, ((ForgeProxy) Proxy.INSTANCE)::registerClientReceiverV2, NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Record & ServerboundMessage<T>> void registerServerbound$Internal(Class<?> clazz) {
        this.register((Class<T>) clazz, ((ForgeProxy) Proxy.INSTANCE)::registerServerReceiverV2, NetworkDirection.PLAY_TO_SERVER);
    }

    private <T> void register(Class<T> clazz, BiConsumer<T, CustomPayloadEvent.Context> handle, NetworkDirection networkDirection) {
        if (!clazz.isRecord()) throw new IllegalArgumentException("Message of type %s is not a record".formatted(clazz));
        Objects.requireNonNull(this.channel, "channel is null");
        BiConsumer<T, FriendlyByteBuf> encoder = (T t, FriendlyByteBuf friendlyByteBuf) -> {
            MessageSerializers.findByType(clazz).write(friendlyByteBuf, t);
        };
        Function<FriendlyByteBuf, T> decoder = MessageSerializers.findByType(clazz)::read;
        this.channel.messageBuilder(clazz, this.discriminator.getAndIncrement(), networkDirection)
                .encoder(encoder).decoder(decoder).consumerMainThread(handle).add();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Record & ClientboundMessage<T>> Packet<ClientCommonPacketListener> toClientboundPacket(T message) {
        Objects.requireNonNull(this.channel, "channel is null");
        Objects.requireNonNull(message, "message is null");
        return (Packet<ClientCommonPacketListener>) NetworkDirection.PLAY_TO_CLIENT.buildPacket(this.channel.toBuffer(message), this.channel.getName()).getThis();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Record & ServerboundMessage<T>> Packet<ServerCommonPacketListener> toServerboundPacket(T message) {
        Objects.requireNonNull(this.channel, "channel is null");
        Objects.requireNonNull(message, "message is null");
        return (Packet<ServerCommonPacketListener>) NetworkDirection.PLAY_TO_SERVER.buildPacket(this.channel.toBuffer(message), this.channel.getName()).getThis();
    }

    @Override
    public void build() {
        if (this.channel != null) throw new IllegalStateException("channel is already built");
        this.channel = buildSimpleChannel(this.channelIdentifier, this.clientAcceptsVanillaOrMissing, this.serverAcceptsVanillaOrMissing);
        super.build();
    }

    private static SimpleChannel buildSimpleChannel(ResourceLocation resourceLocation, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return ChannelBuilder
                .named(resourceLocation)
                .networkProtocolVersion(PROTOCOL_VERSION)
                .clientAcceptedVersions(clientAcceptsVanillaOrMissing ? Channel.VersionTest.ACCEPT_VANILLA : Channel.VersionTest.exact(1))
                .serverAcceptedVersions(serverAcceptsVanillaOrMissing ? Channel.VersionTest.ACCEPT_VANILLA : Channel.VersionTest.exact(1))
                .simpleChannel();
    }
}
