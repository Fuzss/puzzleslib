package fuzs.puzzleslib.mixin;

import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.OptionalBox;
import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(StartAttacking.class)
abstract class StartAttackingFabricMixin {
    @Unique
    private static boolean puzzleslib$cancelLivingChangeTarget;

    @ModifyVariable(method = "lambda$create$1(Ljava/util/function/Predicate;Ljava/util/function/Function;Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;J)Z", at = @At(value = "LOAD", ordinal = 1), ordinal = 0)
    private static <E extends Mob> LivingEntity lambda$create$1$0(LivingEntity entity, Predicate<E> canAttack, Function<E, Optional<? extends LivingEntity>> targetFinder, MemoryAccessor<Const.Mu<Unit>, LivingEntity> memoryAccessor, MemoryAccessor<OptionalBox.Mu, Long> memoryAccessor2, ServerLevel level, Mob mob, long l) {
        DefaultedValue<LivingEntity> target = DefaultedValue.fromValue(entity);
        puzzleslib$cancelLivingChangeTarget = FabricLivingEvents.LIVING_CHANGE_TARGET.invoker().onLivingChangeTarget(mob, target).isInterrupt();
        return target.getAsOptional().orElse(entity);
    }

    @Inject(method = "lambda$create$1(Ljava/util/function/Predicate;Ljava/util/function/Function;Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;J)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;set(Ljava/lang/Object;)V"), cancellable = true)
    private static <E extends Mob> void lambda$create$1$1(Predicate<E> canAttack, Function<E, Optional<? extends LivingEntity>> targetFinder, MemoryAccessor<Const.Mu<Unit>, LivingEntity> memoryAccessor, MemoryAccessor<OptionalBox.Mu, Long> memoryAccessor2, ServerLevel level, Mob mob, long l, CallbackInfoReturnable<Boolean> callback) {
        if (puzzleslib$cancelLivingChangeTarget) {
            puzzleslib$cancelLivingChangeTarget = false;
            callback.setReturnValue(false);
        }
    }
}
