package fuzs.puzzleslib.api.network.v4;

import fuzs.puzzleslib.api.network.v4.message.Message;
import fuzs.puzzleslib.impl.core.context.PayloadTypesContextImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.network.ConfigurationTask;

import java.util.Objects;

public final class NetworkingHelper {

    private NetworkingHelper() {
        // NO-OP
    }

    /**
     * Get the custom packet payload type for a registered message class.
     * <p>
     * The type can also be obtained from the instance itself via {@link Message#type()}.
     *
     * @param payloadClazz the message clazz type
     * @param <T>          the message type
     * @return the custom packet payload type
     */
    public static <T extends Message<?>> CustomPacketPayload.Type<T> getPayloadType(Class<T> payloadClazz) {
        Objects.requireNonNull(payloadClazz, "payload class is null");
        return PayloadTypesContextImpl.getPayloadType(payloadClazz);
    }

    /**
     * Creates a packet that can be sent to clients from the provided payload.
     *
     * @param payload the custom packet payload
     * @return the packet
     */
    public static Packet<ClientCommonPacketListener> toClientboundPacket(CustomPacketPayload payload) {
        Objects.requireNonNull(payload, "payload is null");
        return ProxyImpl.get().toClientboundPacket(payload);
    }

    /**
     * Creates a packet that can be sent to the server from the provided payload.
     *
     * @param payload the custom packet payload
     * @return the packet
     */
    public static Packet<ServerCommonPacketListener> toServerboundPacket(CustomPacketPayload payload) {
        Objects.requireNonNull(payload, "payload is null");
        return ProxyImpl.get().toServerboundPacket(payload);
    }

    /**
     * Reports a configuration task as finished, so that the {@link ServerConfigurationPacketListener} may move on to
     * the next task.
     * <p>
     * Usually called after completing the task on the server, or after receiving the client response packet.
     *
     * @param packetListener        the packet listener
     * @param configurationTaskType the configuration task type
     */
    public static void finishConfigurationTask(ServerConfigurationPacketListener packetListener, ConfigurationTask.Type configurationTaskType) {
        Objects.requireNonNull(packetListener, "packet listener is null");
        Objects.requireNonNull(configurationTaskType, "configuration task type is null");
        ProxyImpl.get().finishConfigurationTask(packetListener, configurationTaskType);
    }

    /**
     * Checks if the packet listener has declared the ability to receive a specific custom packet payload type.
     *
     * @param packetListener the packet listener
     * @param payloadType    the packet type
     * @return can the custom packet payload type be received
     */
    public static boolean hasChannel(PacketListener packetListener, CustomPacketPayload.Type<?> payloadType) {
        Objects.requireNonNull(packetListener, "packet listener is null");
        Objects.requireNonNull(payloadType, "custom packet payload type is null");
        return ProxyImpl.get().hasChannel(packetListener, payloadType);
    }

    /**
     * @return the connection to the server on the physical client
     */
    public static ClientGamePacketListener getClientPacketListener() {
        return ProxyImpl.get().getClientPacketListener();
    }

    /**
     * Get the {@link Connection} from a packet listener.
     *
     * @param packetListener the packet listener
     * @return the connection
     */
    public static Connection getConnection(PacketListener packetListener) {
        Objects.requireNonNull(packetListener, "packet listener is null");
        Connection connection = ProxyImpl.get().getConnection(packetListener);
        Objects.requireNonNull(connection, "connection is null");
        return connection;
    }
}
