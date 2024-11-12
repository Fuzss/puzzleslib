package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Wolf.class)
abstract class WolfFabricMixin extends TamableAnimal {

    protected WolfFabricMixin(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
            method = "tryToTame",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
    )
    public int tryToTame(int intValue, Player player) {
        return FabricEventImplHelper.onAnimalTame(this, player, intValue);
    }
}
