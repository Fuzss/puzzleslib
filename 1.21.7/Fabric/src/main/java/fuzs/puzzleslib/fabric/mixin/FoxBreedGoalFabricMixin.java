package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.world.entity.animal.Fox$FoxBreedGoal")
abstract class FoxBreedGoalFabricMixin extends BreedGoal {

    public FoxBreedGoalFabricMixin(Animal animal, double d) {
        super(animal, d);
    }

    @ModifyVariable(method = "breed", at = @At("STORE"))
    protected Fox breed(Fox fox) {
        MutableValue<AgeableMob> child = MutableValue.fromValue(fox);
        if (FabricLivingEvents.BABY_ENTITY_SPAWN.invoker().onBabyEntitySpawn(this.animal, this.partner, child).isInterrupt()) {
            this.animal.setAge(6000);
            this.partner.setAge(6000);
            this.animal.resetLove();
            this.partner.resetLove();
            return null;
        } else {
            return (Fox) child.get();
        }
    }
}
