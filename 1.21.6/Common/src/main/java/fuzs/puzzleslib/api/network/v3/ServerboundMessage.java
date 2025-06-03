package fuzs.puzzleslib.api.network.v3;

import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;

/**
 * Template for a message sent by clients and received on the server.
 *
 * @param <T> the message type
 */
public interface ServerboundMessage<T> extends LegacyMessage<ServerMessageListener<T>, ServerboundPlayMessage.Context>, ServerboundPlayMessage {

    @Override
    default MessageListener<ServerboundPlayMessage.Context> getListener() {
        return new MessageListener<>() {
            @Override
            public void accept(ServerboundPlayMessage.Context context) {
                ServerboundMessage.this.getHandler()
                        .handle((T) ServerboundMessage.this,
                                context.server(),
                                context.packetListener(),
                                context.player(),
                                context.level());
            }
        };
    }
}
