package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.fabric.api.event.v1.FabricEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ThrownEnderpearl.class)
abstract class ThrownEnderpearlFabricMixin extends ThrowableItemProjectile {

    public ThrownEnderpearlFabricMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(method = "onHit",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;isAcceptingMessages()Z"))
    protected boolean onHit(boolean isAcceptingMessages, HitResult hitResult, @Share("damageAmount") LocalRef<DefaultedFloat> damageAmountRef) {
        if (isAcceptingMessages && this.getOwner() instanceof ServerPlayer serverPlayer) {
            damageAmountRef.set(DefaultedFloat.fromValue(5.0F));
            EventResult result = FabricEntityEvents.ENDER_PEARL_TELEPORT.invoker()
                    .onEnderPearlTeleport(serverPlayer,
                            this.position(),
                            ThrownEnderpearl.class.cast(this),
                            damageAmountRef.get(),
                            hitResult);
            if (result.isInterrupt()) {
                return false;
            }
        }

        return isAcceptingMessages;
    }

    @ModifyArg(method = "onHit",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    protected float onHit(float damageAmount, @Share("damageAmount") LocalRef<DefaultedFloat> damageAmountRef) {
        return damageAmountRef.get().getAsOptionalFloat().orElse(damageAmount);
    }
}
