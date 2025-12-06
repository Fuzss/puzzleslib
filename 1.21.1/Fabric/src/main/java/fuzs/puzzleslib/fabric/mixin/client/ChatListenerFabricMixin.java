package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ChatListener.class)
abstract class ChatListenerFabricMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyArg(method = "lambda$handleDisguisedChatMessage$3",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;)V"))
    public Component handleDisguisedChatMessage(Component component, @Local(argsOnly = true) ChatType.Bound boundChatType, @Cancellable CallbackInfoReturnable<Boolean> callback) {
        MutableValue<Component> chatMessage = MutableValue.fromValue(component);
        EventResult result = FabricClientEvents.CHAT_MESSAGE_RECEIVED.invoker()
                .onChatMessageReceived(chatMessage, boundChatType, null, false);
        if (result.isInterrupt()) callback.setReturnValue(true);
        return chatMessage.get();
    }

    @ModifyArg(method = "showMessageToPlayer",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V"),
            require = 2)
    public Component showMessageToPlayer(Component component, @Local(argsOnly = true) ChatType.Bound boundChatType, @Local(
            argsOnly = true) PlayerChatMessage playerChatMessage, @Cancellable CallbackInfoReturnable<Boolean> callback) {
        MutableValue<Component> chatMessage = MutableValue.fromValue(component);
        EventResult result = FabricClientEvents.CHAT_MESSAGE_RECEIVED.invoker()
                .onChatMessageReceived(chatMessage, boundChatType, playerChatMessage, false);
        if (result.isInterrupt()) callback.setReturnValue(true);
        return chatMessage.get();
    }

    @ModifyVariable(method = "handleSystemMessage", at = @At("HEAD"), argsOnly = true)
    public Component handleSystemMessage(Component component, Component _component, boolean isOverlay, @Cancellable CallbackInfo callback) {
        if (!this.minecraft.options.hideMatchedNames().get()
                || !this.minecraft.isBlocked(this.guessChatUUID(component))) {
            MutableValue<Component> chatMessage = MutableValue.fromValue(component);
            EventResult result = FabricClientEvents.CHAT_MESSAGE_RECEIVED.invoker()
                    .onChatMessageReceived(chatMessage, null, null, isOverlay);
            if (result.isInterrupt()) callback.cancel();
            return chatMessage.get();
        } else {
            return component;
        }
    }

    @Shadow
    private UUID guessChatUUID(Component message) {
        throw new RuntimeException();
    }
}
