package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
abstract class ExperienceOrbFabricMixin extends Entity {

    public ExperienceOrbFabricMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "playerTouch",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/world/entity/player/Player;takeXpDelay:I",
                    opcode = Opcodes.PUTFIELD),
            cancellable = true)
    public void playerTouch(Player player, CallbackInfo callback) {
        if (FabricPlayerEvents.PICKUP_EXPERIENCE.invoker()
                .onPickupExperience(player, ExperienceOrb.class.cast(this))
                .isInterrupt()) {
            callback.cancel();
        }
    }
}
