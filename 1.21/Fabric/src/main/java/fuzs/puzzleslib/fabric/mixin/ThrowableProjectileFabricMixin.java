package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.api.event.v1.FabricEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ThrowableProjectile.class)
abstract class ThrowableProjectileFabricMixin extends Projectile {
    @Unique
    @Nullable
    private HitResult puzzleslib$originalHitResult;

    public ThrowableProjectileFabricMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(method = "tick", at = @At(value = "LOAD", ordinal = 0), ordinal = 0, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TheEndGatewayBlockEntity;canEntityTeleport(Lnet/minecraft/world/entity/Entity;)Z")))
    public HitResult tick$0(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.MISS) return hitResult;
        if (FabricEntityEvents.PROJECTILE_IMPACT.invoker().onProjectileImpact(this, hitResult).isInterrupt()) {
            this.puzzleslib$originalHitResult = hitResult;
            // just a dummy to prevent Projectile::onHit from being called
            // we don't implement this by overriding Projectile::onHit as this is an abstract class where Projectile::onHit will likely be overridden by subclasses
            return BlockHitResult.miss(hitResult.getLocation(), Direction.NORTH, BlockPos.containing(hitResult.getLocation()));
        } else {
            this.puzzleslib$originalHitResult = null;
            return hitResult;
        }
    }

    @ModifyVariable(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrowableProjectile;checkInsideBlocks()V", shift = At.Shift.BEFORE), ordinal = 0)
    public HitResult tick$1(HitResult hitResult) {
        // reset this to original, don't want to mess with other mixins expecting this to be correct here
        if (this.puzzleslib$originalHitResult != null) hitResult = this.puzzleslib$originalHitResult;
        this.puzzleslib$originalHitResult = null;
        return hitResult;
    }
}
