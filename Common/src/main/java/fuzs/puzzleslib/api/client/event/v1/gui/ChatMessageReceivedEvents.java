package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.jetbrains.annotations.Nullable;

public final class ChatMessageReceivedEvents {
    public static final EventInvoker<System> SYSTEM = EventInvoker.lookup(System.class);
    public static final EventInvoker<Player> PLAYER = EventInvoker.lookup(Player.class);

    private ChatMessageReceivedEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface System {

        /**
         * Runs in {@link net.minecraft.client.multiplayer.chat.ChatListener} when a new system chat message is about to
         * be added to the client chat.
         * <p>
         * Allows for filtering out or adjusting received messages.
         * <p>
         * Note that is this is invoked twice on Fabric, once for cancelling receiving the message, and the second time
         * for modifying the component sent.
         *
         * @param message   the message component
         * @param isOverlay is this added above the hotbar instead of to the chat
         * @return {@link EventResult#INTERRUPT} to prevent the message from being added to the client chat,
         *         {@link EventResult#PASS} to allow the message to be added
         */
        EventResult onSystemMessageReceived(MutableValue<Component> message, boolean isOverlay);
    }

    @FunctionalInterface
    public interface Player {

        /**
         * Runs in {@link net.minecraft.client.multiplayer.chat.ChatListener} when a new player sent chat message is
         * about to be added to the client chat.
         * <p>
         * Allows for filtering out or adjusting received messages.
         * <p>
         * Note that is this is invoked twice on Fabric, once for cancelling receiving the message, and the second time
         * for modifying the component sent.
         *
         * @param chatTypeBound     chat message information, used for decoration
         * @param message           the message component
         * @param playerChatMessage player signing information
         * @return {@link EventResult#INTERRUPT} to prevent the message from being added to the client chat,
         *         {@link EventResult#PASS} to allow the message to be added
         */
        EventResult onPlayerMessageReceived(ChatType.Bound chatTypeBound, MutableValue<Component> message, @Nullable PlayerChatMessage playerChatMessage);
    }
}
