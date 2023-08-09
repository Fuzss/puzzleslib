package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.event.v1.FabricClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientPacketListener.class)
abstract class ClientPacketListenerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "handleLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetPos()V", shift = At.Shift.AFTER))
    public void handleLogin(ClientboundLoginPacket packet, CallbackInfo callback) {
        Objects.requireNonNull(this.minecraft.player, "player is null");
        Objects.requireNonNull(this.minecraft.gameMode, "game mode is null");
        Objects.requireNonNull(this.minecraft.getConnection(), "connection is null");
        Objects.requireNonNull(this.minecraft.getConnection().getConnection(), "connection is null");
        FabricClientEvents.PLAYER_LOGGED_IN.invoker().onLoggedIn(this.minecraft.player, this.minecraft.gameMode, this.minecraft.getConnection().getConnection());
    }

    @ModifyVariable(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addPlayer(ILnet/minecraft/client/player/AbstractClientPlayer;)V", shift = At.Shift.AFTER), ordinal = 0)
    public LocalPlayer handleRespawn(LocalPlayer oldPlayer) {
        Objects.requireNonNull(oldPlayer, "old player is null");
        Objects.requireNonNull(this.minecraft.player, "new player is null");
        Objects.requireNonNull(this.minecraft.gameMode, "game mode is null");
        Objects.requireNonNull(this.minecraft.player.connection, "connection is null");
        Objects.requireNonNull(this.minecraft.player.connection.getConnection(), "connection is null");
        FabricClientEvents.PLAYER_COPY.invoker().onCopy(oldPlayer, this.minecraft.player, this.minecraft.gameMode, this.minecraft.player.connection.getConnection());
        return oldPlayer;
    }
}
