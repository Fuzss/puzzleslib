package fuzs.puzzleslib.fabric.impl.event;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.impl.event.data.DefaultedFloat;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public final class FabricEventImplHelper {

    private FabricEventImplHelper() {
        // NO-OP
    }

    public static InteractionResult processInteractionResult(EventResultHolder<InteractionResult> eventResult, Level level, Predicate<InteractionResult> sendPacketPredicate, Runnable packetSender) {
        Optional<InteractionResult> optional = eventResult.getInterrupt();

        if (optional.isPresent()) {
            InteractionResult interactionResult = optional.get();

            if (interactionResult == InteractionResult.PASS) {
                // this is done for parity with Forge where InteractionResult#PASS can be cancelled,
                // while on Fabric it will mark the event as having done nothing
                // unfortunately this will prevent the off-hand from being processed (if fired for the main hand),
                // but it's the best we can do
                interactionResult = InteractionResult.FAIL;
            }

            if (level.isClientSide && sendPacketPredicate.test(interactionResult)) {
                // this brings parity with Forge where the server is notified regardless of the returned InteractionResult,
                // as the Forge event runs after the server packet is sent
                packetSender.run();
            }

            return interactionResult;
        } else {
            return InteractionResult.PASS;
        }
    }

    public static int onAnimalTame(Animal animal, Player player, int intValue) {
        return onAnimalTame(animal, player, intValue, 1, intValue == 0);
    }

    public static int onAnimalTame(Animal animal, Player player, int intValue, int returnValue, boolean tameCondition) {
        if (tameCondition && FabricLivingEvents.ANIMAL_TAME.invoker().onAnimalTame(animal, player).isInterrupt()) {
            return returnValue;
        } else {
            return intValue;
        }
    }

    public static float onLivingHurt(LivingEntity livingEntity, ServerLevel serverLevel, DamageSource damageSource, float damageAmount, MutableBoolean cancelInjection) {
        if (!livingEntity.isInvulnerableTo(serverLevel, damageSource)) {
            DefaultedFloat damageAmountValue = DefaultedFloat.fromValue(damageAmount);
            EventResult eventResult = FabricLivingEvents.LIVING_HURT.invoker()
                    .onLivingHurt(livingEntity, damageSource, damageAmountValue);
            if (eventResult.isInterrupt()) {
                cancelInjection.setTrue();
            }
            return damageAmountValue.getAsOptionalFloat().orElse(damageAmount);
        } else {
            return damageAmount;
        }
    }

    public static boolean tryOnLivingDrops(LivingEntity entity, DamageSource damageSource, int lastHurtByPlayerMemoryTime) {
        Collection<ItemEntity> capturedDrops = ((CapturedDropsEntity) entity).puzzleslib$acceptCapturedDrops(null);
        if (capturedDrops != null) {
            EventResult eventResult = FabricLivingEvents.LIVING_DROPS.invoker()
                    .onLivingDrops(entity, damageSource, capturedDrops, lastHurtByPlayerMemoryTime > 0);
            if (eventResult.isPass()) {
                capturedDrops.forEach((ItemEntity itemEntity) -> {
                    entity.level().addFreshEntity(itemEntity);
                });
            }
            return true;
        } else {
            return false;
        }
    }

    public static EventResult onPlaySound(LevelSoundEventInvoker eventInvoker, Args args, MutableValue<Holder<SoundEvent>> soundEvent, int soundSourceOrdinal, int soundVolumeOrdinal, int soundPitchOrdinal) {
        Preconditions.checkArgument(args.get(soundSourceOrdinal) instanceof SoundSource, "sound source is wrong type");
        Preconditions.checkArgument(args.get(soundVolumeOrdinal) instanceof Float, "sound volume is wrong type");
        Preconditions.checkArgument(args.get(soundPitchOrdinal) instanceof Float, "sound pitch is wrong type");
        MutableValue<SoundSource> soundSource = MutableValue.fromEvent((SoundSource soundSourceX) -> args.set(
                soundSourceOrdinal,
                soundSourceX), () -> args.get(soundSourceOrdinal));
        MutableFloat soundVolume = MutableFloat.fromEvent((Float volume) -> args.set(soundVolumeOrdinal, volume),
                () -> args.get(soundVolumeOrdinal));
        MutableFloat soundPitch = MutableFloat.fromEvent((Float pitch) -> args.set(soundPitchOrdinal, pitch),
                () -> args.get(soundPitchOrdinal));
        return eventInvoker.onPlaySound(soundEvent, soundSource, soundVolume, soundPitch);
    }

    @FunctionalInterface
    public interface LevelSoundEventInvoker {

        EventResult onPlaySound(MutableValue<Holder<SoundEvent>> soundEvent, MutableValue<SoundSource> soundSource, MutableFloat soundVolume, MutableFloat soundPitch);
    }
}
