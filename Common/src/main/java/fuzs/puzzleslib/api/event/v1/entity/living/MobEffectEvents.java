package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

public final class MobEffectEvents {
    public static final EventInvoker<Affects> AFFECTS = EventInvoker.lookup(Affects.class);
    public static final EventInvoker<Apply> APPLY = EventInvoker.lookup(Apply.class);
    public static final EventInvoker<Remove> REMOVE = EventInvoker.lookup(Remove.class);
    public static final EventInvoker<Expire> EXPIRE = EventInvoker.lookup(Expire.class);

    private MobEffectEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Affects {

        /**
         * Called when the game checks whether a new {@link MobEffectInstance} can be applied to a {@link LivingEntity}
         * in {@link LivingEntity#canBeAffected(MobEffectInstance)}.
         * <p>
         * This is used for mobs such as spiders to make them immune to
         * {@link net.minecraft.world.effect.MobEffects#POISON}, or wither mobs to make them immune to the
         * {@link net.minecraft.world.effect.MobEffects#WITHER} effect.
         *
         * @param livingEntity the entity the check is run for
         * @param mobEffect    the effect instance to check
         * @return <ul>
         *         <li>{@link EventResult#ALLOW ALLOW} to allow this effect to be applied to the entity</li>
         *         <li>{@link EventResult#DENY DENY} to prevent this effect from being applied to the entity</li>
         *         <li>{@link EventResult#PASS PASS} to let vanilla logic handle this case</li>
         *         </ul>
         */
        EventResult onMobEffectAffects(LivingEntity livingEntity, MobEffectInstance mobEffect);
    }

    @FunctionalInterface
    public interface Apply {

        /**
         * Called when a new {@link MobEffectInstance} is added to a {@link LivingEntity} in
         * {@link LivingEntity#addEffect(MobEffectInstance, Entity)}.
         * <p>
         * If another effect with the same {@link net.minecraft.world.effect.MobEffect} is already present, the existing
         * effect instance will be updated.
         *
         * @param livingEntity the living entity the effect instance is added to
         * @param newMobEffect the effect instance being added
         * @param oldMobEffect the potentially already existing effect instance with the same mob effect type
         * @param sourceEntity the potential other entity responsible for inflicting this new effect
         */
        void onMobEffectApply(LivingEntity livingEntity, MobEffectInstance newMobEffect, @Nullable MobEffectInstance oldMobEffect, @Nullable Entity sourceEntity);
    }

    @FunctionalInterface
    public interface Remove {

        /**
         * Called when a {@link MobEffectInstance} is removed from a {@link LivingEntity} in
         * {@link LivingEntity#removeEffect(Holder)}.
         * <p>
         * Most importantly, this event runs when finishing drinking milk and allows for preventing certain effects from
         * being removed as a result.
         *
         * @param livingEntity the entity the effect instance is to be removed from
         * @param mobEffect    the effect instance to remove
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent this effect instance from being removed</li>
         *         <li>{@link EventResult#PASS PASS} to let vanilla logic handle this case</li>
         *         </ul>
         */
        EventResult onMobEffectRemove(LivingEntity livingEntity, MobEffectInstance mobEffect);
    }

    @FunctionalInterface
    public interface Expire {

        /**
         * Called when a {@link MobEffectInstance} is removed in {@link LivingEntity#tickEffects()} due to the instance
         * duration having run out.
         * <p>
         * This is the case when {@link MobEffectInstance#hasRemainingDuration()} returns {@code false}.
         *
         * @param livingEntity the living entity the effect instance has run out on
         * @param mobEffect    the mob effect instance that has run out
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent this effect instance from being removed</li>
         *         <li>{@link EventResult#PASS PASS} to let vanilla logic handle this case</li>
         *         </ul>
         */
        EventResult onMobEffectExpire(LivingEntity livingEntity, MobEffectInstance mobEffect);
    }
}
