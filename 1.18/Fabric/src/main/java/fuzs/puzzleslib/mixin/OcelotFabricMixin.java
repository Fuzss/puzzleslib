package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ocelot.class)
abstract class OcelotFabricMixin extends Animal {

    protected OcelotFabricMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Ocelot;setTrusting(Z)V"), cancellable = true)
    public void mobInteract(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> callback) {
        if (FabricLivingEvents.ANIMAL_TAME.invoker().onAnimalTame(this, player).isInterrupt()) {
            this.spawnTrustingParticles(false);
            this.level.broadcastEntityEvent(this, EntityEvent.TRUSTING_FAILED);
            callback.setReturnValue(InteractionResult.CONSUME);
        }
    }

    @Shadow
    private void spawnTrustingParticles(boolean success) {
        throw new RuntimeException();
    }
}
