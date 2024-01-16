package fuzs.puzzleslib.forge.impl.network;

import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class NetworkHandlerForgeV2 implements NetworkHandlerV2 {
    private final AtomicInteger discriminator = new AtomicInteger();
    private final ResourceLocation channelName;
    private final SimpleChannel channel;

    public NetworkHandlerForgeV2(ResourceLocation channelName, boolean optional) {
        this.channelName = channelName;
        this.channel = NetworkHandlerForgeV3.buildSimpleChannel(channelName, optional);
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerClientbound(Class<T> clazz, Function<FriendlyByteBuf, T> factory) {
        this.register(clazz, factory, LogicalSide.CLIENT);
        return this;
    }

    @Override
    public <T extends MessageV2<T>> NetworkHandlerV2 registerServerbound(Class<T> clazz, Function<FriendlyByteBuf, T> factory) {
        this.register(clazz, factory, LogicalSide.SERVER);
        return this;
    }

    private <T extends MessageV2<T>> void register(Class<T> clazz, Function<FriendlyByteBuf, T> factory, LogicalSide receptionSide) {
        BiConsumer<T, CustomPayloadEvent.Context> handle = (T message, CustomPayloadEvent.Context context) -> {
            LogicalSide expectedReceptionSide = context.getDirection().getReceptionSide();
            if (expectedReceptionSide != receptionSide) {
                String modName = ModLoaderEnvironment.INSTANCE.getModContainer(this.channelName.getNamespace()).map(ModContainer::getDisplayName).orElse(this.channelName.getNamespace());
                throw new IllegalStateException("Receiving %s from %s on wrong side!".formatted(clazz.getSimpleName(), modName));
            }
            // this needs to happen in here, otherwise Minecraft#player might still be null for events fired on login/entity creation
            Player player;
            if (receptionSide.isClient()) {
                player = Proxy.INSTANCE.getClientPlayer();
            } else {
                player = context.getSender();
            }
            Objects.requireNonNull(player, "player is null");
            message.makeHandler().handle(message, player, LogicalSidedProvider.WORKQUEUE.get(receptionSide));
        };
        NetworkDirection networkDirection = receptionSide.isClient() ? NetworkDirection.PLAY_TO_CLIENT : NetworkDirection.PLAY_TO_SERVER;
        this.channel.messageBuilder(clazz, this.discriminator.getAndIncrement(), networkDirection).encoder(MessageV2::write).decoder(factory).consumerMainThread(handle).add();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Packet<ClientCommonPacketListener> toClientboundPacket(MessageV2<?> message) {
        return (Packet<ClientCommonPacketListener>) NetworkDirection.PLAY_TO_CLIENT.buildPacket(this.channel.toBuffer(message), this.channel.getName()).getThis();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Packet<ServerCommonPacketListener> toServerboundPacket(MessageV2<?> message) {
        return (Packet<ServerCommonPacketListener>) NetworkDirection.PLAY_TO_SERVER.buildPacket(this.channel.toBuffer(message), this.channel.getName()).getThis();
    }
}
