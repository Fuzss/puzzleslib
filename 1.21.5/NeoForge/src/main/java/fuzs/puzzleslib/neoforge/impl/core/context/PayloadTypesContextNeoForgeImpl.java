package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.network.v4.message.Message;
import fuzs.puzzleslib.api.network.v4.message.configuration.ClientboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.configuration.ServerboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import fuzs.puzzleslib.impl.core.context.PayloadTypesContextImpl;
import fuzs.puzzleslib.neoforge.impl.network.MessageContextNeoForgeImpl;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.function.Function;

public abstract class PayloadTypesContextNeoForgeImpl extends PayloadTypesContextImpl {
    net.neoforged.neoforge.network.registration.PayloadRegistrar registrar;

    PayloadTypesContextNeoForgeImpl(String modId, RegisterPayloadHandlersEvent evt) {
        super(modId);
        this.registrar = evt.registrar(this.channelName.toString());
    }

    @Override
    public void optional() {
        this.registrar = this.registrar.optional();
    }

    @Override
    public <T extends ServerboundPlayMessage> void playToServer(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        this.register(clazz,
                streamCodec,
                this.registrar::playToServer,
                MessageContextNeoForgeImpl.ServerboundPlay::new);
    }

    @Override
    public <T extends ServerboundConfigurationMessage> void configurationToServer(Class<T> clazz, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
        this.register(clazz,
                streamCodec,
                this.registrar::configurationToServer,
                MessageContextNeoForgeImpl.ServerboundConfiguration::new);
    }

    <B extends FriendlyByteBuf, T extends Message<C>, C extends Message.Context<?>> void registerWithoutReceiver(Class<T> clazz, StreamCodec<? super B, T> streamCodec, PayloadRegistrar<B, T> registrar) {
        this.register(clazz, streamCodec, registrar, (IPayloadContext context) -> null);
    }

    <B extends FriendlyByteBuf, T extends Message<C>, C extends Message.Context<?>> void register(Class<T> clazz, StreamCodec<? super B, T> streamCodec, PayloadRegistrar<B, T> registrar, Function<IPayloadContext, C> contextFactory) {
        CustomPacketPayload.Type<T> type = this.registerPayloadType(clazz);
        registrar.apply(type, streamCodec, (T payload, IPayloadContext context) -> {
            context.enqueueWork(() -> {
                payload.getListener().accept(contextFactory.apply(context));
            }).exceptionally((Throwable throwable) -> {
                this.disconnectExceptionally(clazz).accept(throwable, context::disconnect);
                return null;
            });
        });
    }

    @FunctionalInterface
    interface PayloadRegistrar<B extends ByteBuf, T extends CustomPacketPayload> {

        void apply(CustomPacketPayload.Type<T> type, StreamCodec<? super B, T> streamCodec, IPayloadHandler<T> handler);
    }

    public static class ServerImpl extends PayloadTypesContextNeoForgeImpl {

        public ServerImpl(String modId, RegisterPayloadHandlersEvent evt) {
            super(modId, evt);
        }

        @Override
        public <T extends ClientboundPlayMessage> void playToClient(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
            this.registerWithoutReceiver(clazz, streamCodec, this.registrar::playToClient);
        }

        @Override
        public <T extends ClientboundConfigurationMessage> void configurationToClient(Class<T> clazz, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
            this.registerWithoutReceiver(clazz, streamCodec, this.registrar::configurationToClient);
        }
    }

    public static class ClientImpl extends PayloadTypesContextNeoForgeImpl {

        public ClientImpl(String modId, RegisterPayloadHandlersEvent evt) {
            super(modId, evt);
        }

        @Override
        public <T extends ClientboundPlayMessage> void playToClient(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
            this.register(clazz,
                    streamCodec,
                    this.registrar::playToClient,
                    MessageContextNeoForgeImpl.ClientboundPlay::new);
        }

        @Override
        public <T extends ClientboundConfigurationMessage> void configurationToClient(Class<T> clazz, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
            this.register(clazz,
                    streamCodec,
                    this.registrar::configurationToClient,
                    MessageContextNeoForgeImpl.ClientboundConfiguration::new);
        }
    }
}
