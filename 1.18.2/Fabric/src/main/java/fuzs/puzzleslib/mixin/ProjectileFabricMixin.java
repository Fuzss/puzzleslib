package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricEntityEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
abstract class ProjectileFabricMixin extends Entity {

    public ProjectileFabricMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    protected void onHit(HitResult hitResult, CallbackInfo callback) {
        if (FireworkRocketEntity.class.isInstance(this) || LlamaSpit.class.isInstance(this)) {
            if (hitResult.getType() != HitResult.Type.MISS) {
                EventResult result = FabricEntityEvents.PROJECTILE_IMPACT.invoker().onProjectileImpact(Projectile.class.cast(this), hitResult);
                if (result.isInterrupt()) callback.cancel();
            }
        }
    }
}
