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
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class NetworkHandlerForgeV3 extends NetworkHandlerRegistryImpl {
    private SimpleChannel channel;

    public NetworkHandlerForgeV3(ResourceLocation channelName) {
        super(channelName);
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
        this.channel = buildSimpleChannel(this.channelName, this.optional);
        super.build();
    }

    private static SimpleChannel buildSimpleChannel(ResourceLocation resourceLocation, boolean optional) {
        int protocolVersion = getProtocolVersion(resourceLocation.getNamespace());
        return ChannelBuilder
                .named(resourceLocation)
                .networkProtocolVersion(protocolVersion)
                .clientAcceptedVersions(optional ? Channel.VersionTest.ACCEPT_VANILLA : Channel.VersionTest.exact(protocolVersion))
                .serverAcceptedVersions(optional ? Channel.VersionTest.ACCEPT_VANILLA : Channel.VersionTest.exact(protocolVersion))
                .simpleChannel();
    }
}
