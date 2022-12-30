package fuzs.puzzleslib.impl.networking;

import fuzs.puzzleslib.api.networking.v3.ClientboundMessage;
import fuzs.puzzleslib.api.networking.v3.ServerboundMessage;
import fuzs.puzzleslib.api.networking.v3.serialization.MessageSerializers;
import fuzs.puzzleslib.proxy.ForgeProxy;
import fuzs.puzzleslib.proxy.Proxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ForgeNetworkHandler implements NetworkHandlerRegistry {
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    private final SimpleChannel channel;
    private final AtomicInteger discriminator = new AtomicInteger();

    private ForgeNetworkHandler(SimpleChannel channel) {
        this.channel = channel;
    }

    @SuppressWarnings("unchecked")
    public <T extends Record & ClientboundMessage<T>> void registerClientbound(Class<?> clazz) {
        this.register((Class<T>) clazz, ((ForgeProxy) Proxy.INSTANCE)::registerClientReceiverV2);
    }

    @SuppressWarnings("unchecked")
    public <T extends Record & ServerboundMessage<T>> void registerServerbound(Class<?> clazz) {
        this.register((Class<T>) clazz, ((ForgeProxy) Proxy.INSTANCE)::registerServerReceiverV2);
    }

    private <T> void register(Class<T> clazz, BiConsumer<T, Supplier<NetworkEvent.Context>> handle) {
        if (!clazz.isRecord()) throw new IllegalArgumentException("Message of type %s is not a record".formatted(clazz));
        BiConsumer<T, FriendlyByteBuf> encode = (t, friendlyByteBuf) -> {
            MessageSerializers.findByType(clazz).write(friendlyByteBuf, t);
        };
        Function<FriendlyByteBuf, T> decode = MessageSerializers.findByType(clazz)::read;
        this.channel.registerMessage(this.discriminator.getAndIncrement(), clazz, encode, decode, handle);
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> Packet<?> toClientboundPacket(T message) {
        return this.channel.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> Packet<?> toServerboundPacket(T message) {
        return this.channel.toVanillaPacket(message, NetworkDirection.PLAY_TO_SERVER);
    }

    public static class ForgeBuilderImpl extends BuilderImpl {

        public ForgeBuilderImpl(String modId) {
            super(modId);
        }

        @Override
        protected NetworkHandlerRegistry getHandler() {
            return new ForgeNetworkHandler(buildSimpleChannel(this.modId, this.clientAcceptsVanillaOrMissing, this.serverAcceptsVanillaOrMissing));
        }

        private static SimpleChannel buildSimpleChannel(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
            return NetworkRegistry.ChannelBuilder
                    .named(new ResourceLocation(modId, "play"))
                    .networkProtocolVersion(() -> PROTOCOL_VERSION)
                    .clientAcceptedVersions(clientAcceptsVanillaOrMissing ? NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION) : PROTOCOL_VERSION::equals)
                    .serverAcceptedVersions(serverAcceptsVanillaOrMissing ? NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION) : PROTOCOL_VERSION::equals)
                    .simpleChannel();
        }
    }
}
