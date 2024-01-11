package fuzs.puzzleslib.impl.event;

import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.OptionalDouble;

public final class LivingJumpHelper {

    private LivingJumpHelper() {

    }

    public static void onLivingJump(LivingEvents.Jump callback, LivingEntity entity) {
        Vec3 deltaMovement = entity.getDeltaMovement();
        DefaultedDouble jumpPower = DefaultedDouble.fromValue(deltaMovement.y);
        OptionalDouble newJumpPower;
        if (callback.onLivingJump(entity, jumpPower).isInterrupt()) {
            newJumpPower = OptionalDouble.of(0.0);
        } else {
            newJumpPower = jumpPower.getAsOptionalDouble();
        }
        if (newJumpPower.isPresent()) {
            entity.setDeltaMovement(deltaMovement.x, newJumpPower.getAsDouble(), deltaMovement.z);
        }
    }
}
