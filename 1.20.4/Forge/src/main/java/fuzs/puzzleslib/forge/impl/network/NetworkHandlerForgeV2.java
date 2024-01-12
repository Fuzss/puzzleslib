package fuzs.puzzleslib.forge.impl.network;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import fuzs.puzzleslib.impl.network.NetworkHandlerImplHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class NetworkHandlerForgeV2 implements NetworkHandlerV2 {
    private static final int PROTOCOL_VERSION = 1;

    private final AtomicInteger discriminator = new AtomicInteger();
    private final SimpleChannel channel;
    public final boolean clientAcceptsVanillaOrMissing;
    public final boolean serverAcceptsVanillaOrMissing;

    public NetworkHandlerForgeV2(ResourceLocation channelIdentifier, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        this.channel = buildSimpleChannel(channelIdentifier, clientAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing);
        this.clientAcceptsVanillaOrMissing = clientAcceptsVanillaOrMissing;
        this.serverAcceptsVanillaOrMissing = serverAcceptsVanillaOrMissing;
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerClientbound(Class<T> clazz) {
        this.register(clazz, NetworkHandlerImplHelper.getMessageDecoder(clazz), LogicalSide.CLIENT);
        return this;
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerServerbound(Class<T> clazz) {
        this.register(clazz, NetworkHandlerImplHelper.getMessageDecoder(clazz), LogicalSide.SERVER);
        return this;
    }

    private <T extends MessageV2<T>> void register(Class<T> clazz, Function<FriendlyByteBuf, T> decoder, LogicalSide receptionSide) {
        BiConsumer<T, CustomPayloadEvent.Context> handle = (T message, CustomPayloadEvent.Context context) -> {
            LogicalSide expectedReceptionSide = context.getDirection().getReceptionSide();
            if (expectedReceptionSide != receptionSide) {
                throw new IllegalStateException(String.format("Received message on wrong side, expected %s, was %s", receptionSide, expectedReceptionSide));
            }
            // this needs to happen in here, otherwise Minecraft#player might still be null for events fired on login/entity creation
            Player player;
            if (receptionSide.isClient()) {
                player = Proxy.INSTANCE.getClientPlayer();
            } else {
                player = context.getSender();
            }
            message.makeHandler().handle(message, player, LogicalSidedProvider.WORKQUEUE.get(receptionSide));
        };
        this.channel.messageBuilder(clazz, this.discriminator.getAndIncrement(), receptionSide.isClient() ? NetworkDirection.PLAY_TO_CLIENT : NetworkDirection.PLAY_TO_SERVER)
                .encoder(MessageV2::write).decoder(decoder).consumerMainThread(handle).add();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Packet<ServerCommonPacketListener> toServerboundPacket(MessageV2<?> message) {
        return (Packet<ServerCommonPacketListener>) NetworkDirection.PLAY_TO_SERVER.buildPacket(this.channel.toBuffer(message), this.channel.getName()).getThis();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Packet<ClientCommonPacketListener> toClientboundPacket(MessageV2<?> message) {
        return (Packet<ClientCommonPacketListener>) NetworkDirection.PLAY_TO_CLIENT.buildPacket(this.channel.toBuffer(message), this.channel.getName()).getThis();
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
