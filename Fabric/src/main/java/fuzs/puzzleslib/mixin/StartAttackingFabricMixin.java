package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(StartAttacking.class)
abstract class StartAttackingFabricMixin<E extends Mob> extends Behavior<E> {
    @Unique
    private boolean puzzleslib$cancelLivingChangeTarget;

    public StartAttackingFabricMixin(Map<MemoryModuleType<?>, MemoryStatus> map) {
        super(map);
    }

    @ModifyVariable(method = "setAttackTarget", at = @At("HEAD"), argsOnly = true)
    private LivingEntity setAttackTarget(LivingEntity attackTarget, E mob) {
        DefaultedValue<LivingEntity> target = DefaultedValue.fromValue(attackTarget);
        this.puzzleslib$cancelLivingChangeTarget = FabricLivingEvents.LIVING_CHANGE_TARGET.invoker().onLivingChangeTarget(mob, target).isInterrupt();
        return target.getAsOptional().orElse(attackTarget);
    }

    @Inject(method = "setAttackTarget", at = @At("HEAD"), cancellable = true)
    private void setAttackTarget(E mob, LivingEntity attackTarget, CallbackInfo callback) {
        if (this.puzzleslib$cancelLivingChangeTarget) {
            this.puzzleslib$cancelLivingChangeTarget = false;
            callback.cancel();
        }
    }
}
