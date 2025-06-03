package fuzs.puzzleslib.api.network.v3;

import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;

/**
 * Template for a message sent by the server and received by clients.
 *
 * @param <T> the message type
 */
public interface ClientboundMessage<T> extends LegacyMessage<ClientMessageListener<T>, ClientboundPlayMessage.Context>, ClientboundPlayMessage {

    @Override
    default MessageListener<ClientboundPlayMessage.Context> getListener() {
        return new MessageListener<>() {
            @Override
            public void accept(ClientboundPlayMessage.Context context) {
                ClientboundMessage.this.getHandler()
                        .handle((T) ClientboundMessage.this,
                                context.client(),
                                context.packetListener(),
                                context.player(),
                                context.level());
            }
        };
    }
}
