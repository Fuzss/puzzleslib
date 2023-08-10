package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractHurtingProjectile.class)
abstract class AbstractHurtingProjectileFabricMixin extends Entity {
    @Unique
    @Nullable
    private HitResult puzzleslib$originalHitResult;

    public AbstractHurtingProjectileFabricMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(method = "tick", at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    public HitResult tick$0(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.MISS) return hitResult;
        if (FabricEntityEvents.PROJECTILE_IMPACT.invoker().onProjectileImpact(Projectile.class.cast(this), hitResult).isInterrupt()) {
            this.puzzleslib$originalHitResult = hitResult;
            // just a dummy to prevent Projectile::onHit from being called
            // we don't implement this by overriding Projectile::onHit as this is an abstract class where Projectile::onHit will likely be overridden by subclasses
            return BlockHitResult.miss(hitResult.getLocation(), Direction.NORTH, new BlockPos(hitResult.getLocation()));
        } else {
            this.puzzleslib$originalHitResult = null;
            return hitResult;
        }
    }

    @ModifyVariable(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractHurtingProjectile;checkInsideBlocks()V", shift = At.Shift.BEFORE), ordinal = 0)
    public HitResult tick$1(HitResult hitResult) {
        // reset this to original, don't want to mess with other mixins expecting this to be correct here
        if (this.puzzleslib$originalHitResult != null) hitResult = this.puzzleslib$originalHitResult;
        this.puzzleslib$originalHitResult = null;
        return hitResult;
    }
}
