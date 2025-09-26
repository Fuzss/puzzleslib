package fuzs.puzzleslib.fabric.impl.event;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.event.data.DefaultedFloat;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.impl.resource.v1.ResourceLoaderImpl;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;

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

    public static BiConsumer<ResourceLocation, PreparableReloadListener> getReloadListenerConsumer(MutableObject<List<PreparableReloadListener>> mutableObject) {
        // we do not use the proper method for registering our reload listener exposed by the api as we must create a fresh instance on every resource reload with updates registries
        // the api method does not allow for replacing existing reload listeners; by directly accessing the field we are able to do so
        return getRegisteredReloadListeners(PackType.SERVER_DATA).<BiConsumer<ResourceLocation, PreparableReloadListener>>map(
                        (Map<ResourceLocation, PreparableReloadListener> registeredReloadListeners) -> registeredReloadListeners::put)
                .orElse((ResourceLocation resourceLocation, PreparableReloadListener reloadListener) -> {
                    if (!(mutableObject.getValue() instanceof ArrayList<PreparableReloadListener>)) {
                        mutableObject.setValue(new ArrayList<>(mutableObject.getValue()));
                    }

                    mutableObject.getValue().add(reloadListener);
                });
    }

    private static Optional<Map<ResourceLocation, PreparableReloadListener>> getRegisteredReloadListeners(PackType packType) {
        try {
            Field field = ResourceLoaderImpl.class.getDeclaredField("addedReloaders");
            field.setAccessible(true);
            Object o = MethodHandles.lookup().unreflectGetter(field).invoke(ResourceLoader.get(packType));
            return Optional.of((Map<ResourceLocation, PreparableReloadListener>) o);
        } catch (Throwable throwable) {
            PuzzlesLib.LOGGER.warn("Unable to access Fabric registered reload listeners: {}", throwable.getMessage());
        }

        return Optional.empty();
    }

    @FunctionalInterface
    public interface LevelSoundEventInvoker {

        EventResult onPlaySound(MutableValue<Holder<SoundEvent>> soundEvent, MutableValue<SoundSource> soundSource, MutableFloat soundVolume, MutableFloat soundPitch);
    }
}
