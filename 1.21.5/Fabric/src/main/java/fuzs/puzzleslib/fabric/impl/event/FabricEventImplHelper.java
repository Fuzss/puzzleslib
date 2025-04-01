package fuzs.puzzleslib.fabric.impl.event;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Collection;

public final class FabricEventImplHelper {

    private FabricEventImplHelper() {
        // NO-OP
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

    public static int onComputeEnchantedLootBonus(Holder<Enchantment> enchantment, int enchantmentLevel, LootContext lootContext) {
        Entity entity = lootContext.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof LivingEntity livingEntity)) return enchantmentLevel;
        DamageSource damageSource = lootContext.getOptionalParameter(LootContextParams.DAMAGE_SOURCE);
        return onComputeEnchantedLootBonus(enchantment, enchantmentLevel, livingEntity, damageSource);
    }

    public static int onComputeEnchantedLootBonus(Holder<Enchantment> enchantment, int enchantmentLevel, LivingEntity livingEntity, @Nullable DamageSource damageSource) {
        MutableInt mutableInt = MutableInt.fromValue(enchantmentLevel);
        FabricLivingEvents.COMPUTE_ENCHANTED_LOOT_BONUS.invoker()
                .onComputeEnchantedLootBonus(livingEntity, damageSource, enchantment, mutableInt);
        return mutableInt.getAsInt();
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

    public static EventResult onPlaySound(LevelSoundEventInvoker eventInvoker, Args args, int soundEventOrdinal, int soundSourceOrdinal, int soundVolumeOrdinal, int soundPitchOrdinal) {
        MutableValue<Holder<SoundEvent>> soundEvent = MutableValue.fromEvent((Holder<SoundEvent> holder) -> args.set(
                soundEventOrdinal,
                holder), () -> args.get(soundEventOrdinal));
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
