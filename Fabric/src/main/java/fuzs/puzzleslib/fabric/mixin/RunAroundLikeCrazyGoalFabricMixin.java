package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(RunAroundLikeCrazyGoal.class)
abstract class RunAroundLikeCrazyGoalFabricMixin extends Goal {
    @Shadow
    @Final
    private AbstractHorse horse;

    @ModifyExpressionValue(method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;getMaxTemper()I")))
    public int tick(int intValue, @Local Player player) {
        int horseTemper = this.horse.getTemper();
        return FabricEventImplHelper.onAnimalTame(this.horse, player, intValue, horseTemper, intValue < horseTemper);
    }
}
