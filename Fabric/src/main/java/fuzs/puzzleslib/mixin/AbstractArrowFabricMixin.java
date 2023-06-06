package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricEntityEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AbstractArrow.class)
abstract class AbstractArrowFabricMixin extends Projectile {
    @Unique
    private boolean puzzleslib$interruptHitEntity;

    public AbstractArrowFabricMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(method = "tick", at = @At(value = "LOAD", ordinal = 0), ordinal = 0, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canHarmPlayer(Lnet/minecraft/world/entity/player/Player;)Z")))
    public HitResult tick$0(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.MISS) return hitResult;
        if (FabricEntityEvents.PROJECTILE_IMPACT.invoker().onProjectileImpact(this, hitResult).isInterrupt()) {
            this.puzzleslib$interruptHitEntity = true;
            // null is desired here as the implementation doesn't check for HitResult$Type#MISS as most others
            // we don't implement this by overriding Projectile::onHit as this is an abstract class where Projectile::onHit will likely be overridden by subclasses
            return null;
        } else {
            this.puzzleslib$interruptHitEntity = false;
            return hitResult;
        }
    }

    @ModifyVariable(method = "tick", at = @At(value = "LOAD", ordinal = 0), ordinal = 0, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canHarmPlayer(Lnet/minecraft/world/entity/player/Player;)Z")))
    public EntityHitResult tick$1(EntityHitResult hitResult) {
        if (this.puzzleslib$interruptHitEntity) {
            hitResult = null;
            this.puzzleslib$interruptHitEntity = false;
        }
        return hitResult;
    }
}
