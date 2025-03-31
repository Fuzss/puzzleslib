package fuzs.puzzleslib.api.core.v1.context;

import fuzs.puzzleslib.api.network.v4.message.configuration.ClientboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.configuration.ServerboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Register custom messages that are compatible with vanilla {@link net.minecraft.network.protocol.Packet Packets}.
 */
public interface PayloadTypesContext {

    /**
     * Are clients &amp; servers without this mod or vanilla clients &amp; servers compatible.
     * <p>
     * Not supported on Fabric-like environments.
     */
    void optional();

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
}
