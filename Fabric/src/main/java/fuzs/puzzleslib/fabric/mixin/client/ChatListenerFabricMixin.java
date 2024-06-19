package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(ChatListener.class)
abstract class ChatListenerFabricMixin {
    @Unique
    private boolean puzzleslib$isCancelled;

    @ModifyVariable(
            method = "showMessageToPlayer", at = @At("LOAD"), ordinal = 0, slice = @Slice(
            from = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/PlayerChatMessage;filterMask()Lnet/minecraft/network/chat/FilterMask;"
            )
    )
    )
    private Component showMessageToPlayer$0(Component component, ChatType.Bound boundChatType, PlayerChatMessage chatMessage, Component decoratedServerContent, GameProfile gameProfile, boolean onlyShowSecureChat, Instant timestamp) {
        DefaultedValue<Component> message = DefaultedValue.fromValue(component);
        EventResult result = FabricGuiEvents.PLAYER_MESSAGE_RECEIVED.invoker()
                .onPlayerMessageReceived(boundChatType, message, chatMessage);
        this.puzzleslib$isCancelled = result.isInterrupt();
        return message.getAsOptional().orElse(component);
    }

    @Inject(
            method = "showMessageToPlayer", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
            ordinal = 0
    ), cancellable = true
    )
    private void showMessageToPlayer$0(ChatType.Bound boundChatType, PlayerChatMessage chatMessage, Component decoratedServerContent, GameProfile gameProfile, boolean onlyShowSecureChat, Instant timestamp, CallbackInfoReturnable<Boolean> callback) {
        if (this.puzzleslib$isCancelled) {
            this.puzzleslib$isCancelled = false;
            callback.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "showMessageToPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/ChatType$Bound;decorate(Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/Component;"
            )
    )
    private Component showMessageToPlayer$1(Component component, ChatType.Bound boundChatType, PlayerChatMessage chatMessage, Component decoratedServerContent, GameProfile gameProfile, boolean onlyShowSecureChat, Instant timestamp) {
        DefaultedValue<Component> message = DefaultedValue.fromValue(component);
        EventResult result = FabricGuiEvents.PLAYER_MESSAGE_RECEIVED.invoker()
                .onPlayerMessageReceived(boundChatType, message, chatMessage);
        this.puzzleslib$isCancelled = result.isInterrupt();
        return message.getAsOptional().orElse(component);
    }

    @Inject(
            method = "showMessageToPlayer", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
            ordinal = 1
    ), cancellable = true
    )
    private void showMessageToPlayer$1(ChatType.Bound boundChatType, PlayerChatMessage chatMessage, Component decoratedServerContent, GameProfile gameProfile, boolean onlyShowSecureChat, Instant timestamp, CallbackInfoReturnable<Boolean> callback) {
        if (this.puzzleslib$isCancelled) {
            this.puzzleslib$isCancelled = false;
            callback.cancel();
        }
    }

    @ModifyVariable(
            method = "handleSystemMessage", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;isBlocked(Ljava/util/UUID;)Z",
            shift = At.Shift.AFTER
    )
    )
    public Component handleSystemMessage(Component component, Component $, boolean isOverlay) {
        DefaultedValue<Component> message = DefaultedValue.fromValue(component);
        EventResult result = FabricGuiEvents.SYSTEM_MESSAGE_RECEIVED.invoker()
                .onSystemMessageReceived(message, isOverlay);
        this.puzzleslib$isCancelled = result.isInterrupt();
        return message.getAsOptional().orElse(component);
    }

    @Inject(
            method = "handleSystemMessage", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;isBlocked(Ljava/util/UUID;)Z",
            shift = At.Shift.AFTER
    ), cancellable = true
    )
    public void handleSystemMessage(Component component, boolean isOverlay, CallbackInfo callback) {
        if (this.puzzleslib$isCancelled) {
            this.puzzleslib$isCancelled = false;
            callback.cancel();
        }
    }
}
