package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Pig.class)
abstract class PigFabricMixin extends Animal {

    protected PigFabricMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "thunderHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public void thunderHit(ServerLevel level, LightningBolt lightning, CallbackInfo callback, @Local ZombifiedPiglin zombifiedPiglin) {
        FabricLivingEvents.LIVING_CONVERSION.invoker().onLivingConversion(this, zombifiedPiglin);
    }
}
