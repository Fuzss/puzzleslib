package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ChatMessageReceivedCallback {
    EventInvoker<ChatMessageReceivedCallback> EVENT = EventInvoker.lookup(ChatMessageReceivedCallback.class);

    /**
     * Runs in {@link net.minecraft.client.multiplayer.chat.ChatListener} when a new chat message is about to be added
     * to the client chat. Allows for filtering out or adjusting received messages.
     * <p>
     * Does not directly distinguish between system, player, and unsigned chat message types, although non-system chat
     * messages will usually have a {@link ChatType.Bound} and for player messages an additional
     * {@link PlayerChatMessage} component associated with them. Also, player messages can never be set as an overlay.
     * <p>
     * Note that some client-side messages such as messages originating from debug actions via {@code F3} are directly
     * forwarded to {@link net.minecraft.client.gui.components.ChatComponent} and therefore are not passed through this
     * event.
     *
     * @param chatMessage       the message component
     * @param chatTypeBound     the chat message information, used for decoration
     * @param playerChatMessage the player signing information
     * @param isOverlay         is this added above the hotbar instead of to the chat
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the message from being added to the client chat</li>
     *         <li>{@link EventResult#PASS PASS} to allow the message to be added</li>
     *         </ul>
     */
    EventResult onChatMessageReceived(MutableValue<Component> chatMessage, ChatType.@Nullable Bound chatTypeBound, @Nullable PlayerChatMessage playerChatMessage, boolean isOverlay);
}
