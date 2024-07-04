package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Wolf.class)
abstract class WolfFabricMixin extends TamableAnimal {

    protected WolfFabricMixin(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tryToTame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Wolf;tame(Lnet/minecraft/world/entity/player/Player;)V"), cancellable = true)
    public void tryToTame(Player player, CallbackInfo callback) {
        if (FabricLivingEvents.ANIMAL_TAME.invoker().onAnimalTame(this, player).isInterrupt()) {
            this.level().broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);
            callback.cancel();
        }
    }
}
