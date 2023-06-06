package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricEntityEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FireworkRocketEntity.class)
abstract class FireworkRocketEntityFabricMixin extends Projectile {

    public FireworkRocketEntityFabricMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void onHit(HitResult hitResult) {
        // implement this in Projectile::onHit, it's unlikely a subclass will override this
        if (hitResult.getType() != HitResult.Type.MISS) {
            EventResult result = FabricEntityEvents.PROJECTILE_IMPACT.invoker().onProjectileImpact(this, hitResult);
            if (result.isInterrupt()) return;
        }
        super.onHit(hitResult);
    }
}
