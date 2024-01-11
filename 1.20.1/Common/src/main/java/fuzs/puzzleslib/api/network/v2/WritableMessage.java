package fuzs.puzzleslib.api.network.v2;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus;

/**
 * A simplified message that does not require implementing {@link MessageV2#read(FriendlyByteBuf)}.
 * <p>Instead it is mandatory to include a constructor that takes a single {@link FriendlyByteBuf} argument.
 *
 * @param <T> the message type for the handler
 */
public interface WritableMessage<T extends MessageV2<T>> extends MessageV2<T> {

    @ApiStatus.NonExtendable
    @Override
    default void read(final FriendlyByteBuf buf) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default MessageHandler<T> makeHandler() {
        return this.getHandler();
    }

    /**
     * @return message handler for message on reception side
     */
    MessageHandler<T> getHandler();
}
