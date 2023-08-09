package fuzs.puzzleslib.mixin;

import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
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

    public ServerPlayerFabricMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(DamageSource damageSource, CallbackInfo callback) {
        if (FabricLivingEvents.LIVING_DEATH.invoker().onLivingDeath(this, damageSource).isInterrupt()) {
            callback.cancel();
        }
    }
}
