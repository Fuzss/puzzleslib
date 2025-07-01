package fuzs.puzzleslib.fabric.impl.core.context;

import fuzs.puzzleslib.api.network.v4.message.Message;
import fuzs.puzzleslib.api.network.v4.message.configuration.ClientboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.configuration.ServerboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import fuzs.puzzleslib.fabric.impl.network.MessageContextFabricImpl;
import fuzs.puzzleslib.impl.core.context.PayloadTypesContextImpl;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class PayloadTypesContextFabricImpl extends PayloadTypesContextImpl {

    PayloadTypesContextFabricImpl(String modId) {
        super(modId);
    }

    @Override
    public <T extends ServerboundPlayMessage> void playToServer(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        this.playToServer(this.registerPayloadType(clazz), streamCodec, clazz::getSimpleName);
    }

    @Override
    public <T extends ServerboundConfigurationMessage> void configurationToServer(Class<T> clazz, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
        this.configurationToServer(this.registerPayloadType(clazz), streamCodec, clazz::getSimpleName);
    }

    @Override
    public <T extends ServerboundPlayMessage> void playToServer(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        this.playToServer(payloadType, streamCodec, payloadType.id()::toString);
    }

    @Override
    public <T extends ServerboundConfigurationMessage> void configurationToServer(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
        this.configurationToServer(payloadType, streamCodec, payloadType.id()::toString);
    }

    @Override
    public void optional() {
        // NO-OP
    }

    <T extends ServerboundPlayMessage> void playToServer(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Supplier<String> payloadContextSupplier) {
        this.register(payloadType,
                streamCodec,
                PayloadTypeRegistry.playC2S(),
                (CustomPacketPayload.Type<T> type, BiConsumer<T, ServerPlayNetworking.Context> consumer) -> {
                    ServerPlayNetworking.registerGlobalReceiver(type, consumer::accept);
                },
                MessageContextFabricImpl.ServerboundPlay::new,
                payloadContextSupplier);
    }

    <T extends ServerboundConfigurationMessage> void configurationToServer(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super FriendlyByteBuf, T> streamCodec, Supplier<String> payloadContextSupplier) {
        this.register(payloadType,
                streamCodec,
                PayloadTypeRegistry.configurationC2S(),
                (CustomPacketPayload.Type<T> type, BiConsumer<T, ServerConfigurationNetworking.Context> consumer) -> {
                    ServerConfigurationNetworking.registerGlobalReceiver(type, consumer::accept);
                },
                MessageContextFabricImpl.ServerboundConfiguration::new,
                payloadContextSupplier);
    }

    <B extends FriendlyByteBuf, T extends Message<C2>, C2 extends Message.Context<?>, C1> void register(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super B, T> streamCodec, PayloadTypeRegistry<B> registrar, PayloadRegistrar<T, C1> receiverRegistrar, Function<C1, C2> contextFactory, Supplier<String> payloadContextSupplier) {
        registrar.register(payloadType, streamCodec);
        receiverRegistrar.apply(payloadType, (T payload, C1 context) -> {
            C2 c2 = contextFactory.apply(context);
            try {
                payload.getListener().accept(c2);
            } catch (Throwable throwable) {
                this.disconnectExceptionally(payloadContextSupplier.get()).accept(throwable, c2::disconnect);
            }
        });
    }

    @FunctionalInterface
    interface PayloadRegistrar<T extends CustomPacketPayload, C> {

        void apply(CustomPacketPayload.Type<T> type, BiConsumer<T, C> consumer);
    }

    public static class ServerImpl extends PayloadTypesContextFabricImpl {

        public ServerImpl(String modId) {
            super(modId);
        }

        @Override
        public <T extends ClientboundPlayMessage> void playToClient(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
            this.registerWithoutReceiver(this.registerPayloadType(clazz), streamCodec, PayloadTypeRegistry.playS2C());
        }

        @Override
        public <T extends ClientboundConfigurationMessage> void configurationToClient(Class<T> clazz, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
            this.registerWithoutReceiver(this.registerPayloadType(clazz),
                    streamCodec,
                    PayloadTypeRegistry.configurationS2C());
        }

        @Override
        public <T extends ClientboundPlayMessage> void playToClient(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
            this.registerWithoutReceiver(payloadType, streamCodec, PayloadTypeRegistry.playS2C());
        }

        @Override
        public <T extends ClientboundConfigurationMessage> void configurationToClient(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
            this.registerWithoutReceiver(payloadType, streamCodec, PayloadTypeRegistry.configurationS2C());
        }

        <B extends FriendlyByteBuf, T extends Message<C>, C extends Message.Context<?>> void registerWithoutReceiver(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super B, T> streamCodec, PayloadTypeRegistry<B> registrar) {
            this.register(payloadType,
                    streamCodec,
                    registrar,
                    (CustomPacketPayload.Type<T> type, BiConsumer<T, Object> consumer) -> {
                        // NO-OP
                    },
                    (Object o) -> null,
                    () -> "");
        }
    }

    public static class ClientImpl extends PayloadTypesContextFabricImpl {

        public ClientImpl(String modId) {
            super(modId);
        }

        @Override
        public <T extends ClientboundPlayMessage> void playToClient(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
            this.playToClient(this.registerPayloadType(clazz), streamCodec, clazz::getSimpleName);
        }

        @Override
        public <T extends ClientboundConfigurationMessage> void configurationToClient(Class<T> clazz, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
            this.configurationToClient(this.registerPayloadType(clazz), streamCodec, clazz::getSimpleName);
        }

        @Override
        public <T extends ClientboundPlayMessage> void playToClient(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
            this.playToClient(payloadType, streamCodec, payloadType.id()::toString);
        }

        @Override
        public <T extends ClientboundConfigurationMessage> void configurationToClient(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
            this.configurationToClient(payloadType, streamCodec, payloadType.id()::toString);
        }

        <T extends ClientboundPlayMessage> void playToClient(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Supplier<String> payloadContextSupplier) {
            this.register(payloadType,
                    streamCodec,
                    PayloadTypeRegistry.playS2C(),
                    (CustomPacketPayload.Type<T> type, BiConsumer<T, ClientPlayNetworking.Context> consumer) -> {
                        ClientPlayNetworking.registerGlobalReceiver(type, consumer::accept);
                    },
                    MessageContextFabricImpl.ClientboundPlay::new,
                    payloadContextSupplier);
        }

        <T extends ClientboundConfigurationMessage> void configurationToClient(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super FriendlyByteBuf, T> streamCodec, Supplier<String> payloadContextSupplier) {
            this.register(payloadType,
                    streamCodec,
                    PayloadTypeRegistry.configurationS2C(),
                    (CustomPacketPayload.Type<T> type, BiConsumer<T, ClientConfigurationNetworking.Context> consumer) -> {
                        ClientConfigurationNetworking.registerGlobalReceiver(type, consumer::accept);
                    },
                    MessageContextFabricImpl.ClientboundConfiguration::new,
                    payloadContextSupplier);
        }
    }
}
