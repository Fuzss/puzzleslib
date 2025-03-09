package fuzs.puzzleslib.neoforge.mixin;

import fuzs.puzzleslib.neoforge.api.event.v1.entity.living.SetupMobGoalsEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
abstract class MobNeoForgeMixin extends LivingEntity {

    protected MobNeoForgeMixin(EntityType<? extends LivingEntity> arg, Level arg2) {
        super(arg, arg2);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    protected void init(EntityType<? extends Mob> entityType, Level level, CallbackInfo callback) {
        if (level instanceof ServerLevel) {
            NeoForge.EVENT_BUS.post(new SetupMobGoalsEvent(Mob.class.cast(this)));
        }
    }
}
