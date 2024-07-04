package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import fuzs.puzzleslib.fabric.api.event.v1.FabricEntityEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FishingHook.class)
abstract class FishingHookFabricMixin extends Projectile {

    public FishingHookFabricMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @WrapWithCondition(
            method = "checkCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/FishingHook;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"
            )
    )
    private boolean checkCollision(FishingHook fishingHook, HitResult hitResult) {
        return FabricEntityEvents.PROJECTILE_IMPACT.invoker().onProjectileImpact(fishingHook, hitResult).isPass();
    }
}
