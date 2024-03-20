package fuzs.puzzleslib.mixin;

import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerFabricMixin extends Player {

    public ServerPlayerFabricMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(
            method = "die",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    public void die(DamageSource damageSource, CallbackInfo callback) {
        EventResult result = FabricLivingEvents.LIVING_DEATH.invoker().onLivingDeath(this, damageSource);
        if (result.isInterrupt()) callback.cancel();
    }
}
