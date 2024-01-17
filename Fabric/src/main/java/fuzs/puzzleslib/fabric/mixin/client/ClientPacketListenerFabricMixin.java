package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientPlayerEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
abstract class ClientPacketListenerFabricMixin extends ClientCommonPacketListenerImpl {

    protected ClientPacketListenerFabricMixin(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(method = "handleLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetPos()V", shift = At.Shift.AFTER))
    public void handleLogin(ClientboundLoginPacket packet, CallbackInfo callback) {
        FabricClientPlayerEvents.PLAYER_LOGGED_IN.invoker().onLoggedIn(this.minecraft.player, this.minecraft.gameMode, this.minecraft.getConnection().getConnection());
    }

    @Inject(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetPos()V", shift = At.Shift.AFTER))
    public LocalPlayer handleRespawn(ClientboundRespawnPacket packet, CallbackInfo callback, @Local(ordinal = 0) LocalPlayer oldPlayer) {
        FabricClientPlayerEvents.PLAYER_COPY.invoker().onCopy(oldPlayer, this.minecraft.player, this.minecraft.gameMode, this.minecraft.player.connection.getConnection());
        return oldPlayer;
    }
}
