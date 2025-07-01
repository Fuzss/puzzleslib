package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.event.v1.FabricEntityEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Projectile.class)
abstract class ProjectileFabricMixin extends Entity {

    public ProjectileFabricMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hitTargetOrDeflectSelf", at = @At("HEAD"), cancellable = true)
    protected void hitTargetOrDeflectSelf(HitResult hitResult, CallbackInfoReturnable<ProjectileDeflection> callback) {
        // simplified patch for this event, Projectile::hitTargetOrDeflectSelf might be overridden in mod classes,
        // but so might the NeoForge patches in e.g. Projectile::onHit
        EventResult result = FabricEntityEvents.PROJECTILE_IMPACT.invoker().onProjectileImpact(
                Projectile.class.cast(this), hitResult);
        if (result.isInterrupt()) {
            callback.setReturnValue(ProjectileDeflection.NONE);
        }
    }
}
