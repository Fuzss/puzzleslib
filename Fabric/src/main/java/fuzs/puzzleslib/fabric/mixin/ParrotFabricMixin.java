package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.parrot.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Parrot.class)
abstract class ParrotFabricMixin extends ShoulderRidingEntity {

    protected ParrotFabricMixin(EntityType<? extends ShoulderRidingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(method = "mobInteract",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    public int mobInteract(int intValue, Player player, InteractionHand interactionHand) {
        return FabricEventImplHelper.onAnimalTame(this, player, intValue);
    }
}
