package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricEntityEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FishingHook.class)
abstract class FishingHookFabricMixin extends Entity {

    public FishingHookFabricMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "checkCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;onHit(Lnet/minecraft/world/phys/HitResult;)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void checkCollision(CallbackInfo callback, HitResult hitResult) {
        // implement this in Projectile::onHit, it's unlikely a subclass will override this
        if (hitResult.getType() == HitResult.Type.MISS) return;
        EventResult result = FabricEntityEvents.PROJECTILE_IMPACT.invoker().onProjectileImpact(Projectile.class.cast(this), hitResult);
        if (result.isInterrupt()) callback.cancel();
    }
}
