package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.Objects;

@Mixin(ServerExplosion.class)
abstract class ServerExplosionFabricMixin {
    @Shadow
    @Final
    private ServerLevel level;
    @Unique
    private List<BlockPos> puzzleslib$explodedPositions;

    @ModifyVariable(method = "explode", at = @At("STORE"), ordinal = 0)
    public List<BlockPos> explode(List<BlockPos> explodedPositions) {
        return this.puzzleslib$explodedPositions = explodedPositions;
    }

    @ModifyVariable(method = "hurtEntities", at = @At("STORE"), ordinal = 0, require = 0)
    public List<Entity> hurtEntities(List<Entity> hurtEntities) {
        Objects.requireNonNull(this.puzzleslib$explodedPositions, "exploded positions is null");
        FabricLevelEvents.EXPLOSION_DETONATE.invoker().onExplosionDetonate(this.level, ServerExplosion.class.cast(this),
                this.puzzleslib$explodedPositions, hurtEntities
        );
        return hurtEntities;
    }
}
