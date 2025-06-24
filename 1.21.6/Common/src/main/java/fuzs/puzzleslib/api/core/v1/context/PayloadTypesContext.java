package fuzs.puzzleslib.api.core.v1.context;

import fuzs.puzzleslib.api.network.v4.message.configuration.ClientboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.configuration.ServerboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Register custom messages that are compatible with vanilla {@link net.minecraft.network.protocol.Packet Packets}.
 */
public interface PayloadTypesContext {

    /**
     * Register a message that will be sent to clients during the play phase.
     *
     * @param clazz       the message class type
     * @param streamCodec the message stream codec
     * @param <T>         the message type
     */
    <T extends ClientboundPlayMessage> void playToClient(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec);

    /**
     * Register a message that will be sent to the server during the play phase.
     *
     * @param clazz       the message class type
     * @param streamCodec the message stream codec
     * @param <T>         the message type
     */
    <T extends ServerboundPlayMessage> void playToServer(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec);

    /**
     * Register a message that will be sent to clients during the configuration phase.
     *
     * @param clazz       the message class type
     * @param streamCodec the message stream codec
     * @param <T>         the message type
     */
    <T extends ClientboundConfigurationMessage> void configurationToClient(Class<T> clazz, StreamCodec<? super FriendlyByteBuf, T> streamCodec);

    /**
     * Register a message that will be sent to the server during the configuration phase.
     *
     * @param clazz       the message class type
     * @param streamCodec the message stream codec
     * @param <T>         the message type
     */
    <T extends ServerboundConfigurationMessage> void configurationToServer(Class<T> clazz, StreamCodec<? super FriendlyByteBuf, T> streamCodec);

    /**
     * Register a message that will be sent to clients during the play phase.
     *
     * @param payloadType the custom packet payload type
     * @param streamCodec the message stream codec
     * @param <T>         the message type
     */
    <T extends ClientboundPlayMessage> void playToClient(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec);

    /**
     * Register a message that will be sent to the server during the play phase.
     *
     * @param payloadType the custom packet payload type
     * @param streamCodec the message stream codec
     * @param <T>         the message type
     */
    <T extends ServerboundPlayMessage> void playToServer(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec);

    /**
     * Register a message that will be sent to clients during the configuration phase.
     *
     * @param payloadType the custom packet payload type
     * @param streamCodec the message stream codec
     * @param <T>         the message type
     */
    <T extends ClientboundConfigurationMessage> void configurationToClient(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super FriendlyByteBuf, T> streamCodec);

    /**
     * Register a message that will be sent to the server during the configuration phase.
     *
     * @param payloadType the custom packet payload type
     * @param streamCodec the message stream codec
     * @param <T>         the message type
     */
    <T extends ServerboundConfigurationMessage> void configurationToServer(CustomPacketPayload.Type<T> payloadType, StreamCodec<? super FriendlyByteBuf, T> streamCodec);

    /**
     * Are clients &amp; servers without this mod or vanilla clients &amp; servers compatible.
     * <p>
     * Not supported in Fabric-like environments.
     */
    void optional();
}
