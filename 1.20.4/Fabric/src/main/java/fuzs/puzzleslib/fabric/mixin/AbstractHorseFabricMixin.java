package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.forge.impl.event.EventImplHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
abstract class AbstractHorseFabricMixin extends Animal {

    protected AbstractHorseFabricMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "executeRidersJump", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;hasImpulse:Z", shift = At.Shift.AFTER))
    public void executeRidersJump(float playerJumpPendingScale, Vec3 travelVector, CallbackInfo callback) {
        EventImplHelper.onLivingJump(FabricLivingEvents.LIVING_JUMP.invoker(), this);
    }
}
