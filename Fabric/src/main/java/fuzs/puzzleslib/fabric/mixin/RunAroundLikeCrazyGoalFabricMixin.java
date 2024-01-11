package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RunAroundLikeCrazyGoal.class)
public abstract class RunAroundLikeCrazyGoalFabricMixin extends Goal {
    @Shadow
    @Final
    private AbstractHorse horse;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;tameWithName(Lnet/minecraft/world/entity/player/Player;)Z"), cancellable = true)
    public void tick(CallbackInfo callback) {
        Player player = (Player) this.horse.getPassengers().get(0);
        if (FabricLivingEvents.ANIMAL_TAME.invoker().onAnimalTame(this.horse, player).isInterrupt()) {
            this.horse.modifyTemper(5);
            this.horse.ejectPassengers();
            this.horse.makeMad();
            this.horse.level().broadcastEntityEvent(this.horse, EntityEvent.TAMING_FAILED);
            callback.cancel();
        }
    }
}
