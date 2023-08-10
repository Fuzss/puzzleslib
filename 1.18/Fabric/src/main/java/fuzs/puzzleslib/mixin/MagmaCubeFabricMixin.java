package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.impl.event.LivingJumpHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagmaCube.class)
abstract class MagmaCubeFabricMixin extends Slime {

    public MagmaCubeFabricMixin(EntityType<? extends Slime> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    protected void jumpFromGround(CallbackInfo callback) {
        LivingJumpHelper.onLivingJump(FabricLivingEvents.LIVING_JUMP.invoker(), this);
    }
}
