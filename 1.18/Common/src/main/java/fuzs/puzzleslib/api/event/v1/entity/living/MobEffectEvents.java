package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public final class MobEffectEvents {
    public static final EventInvoker<Affects> AFFECTS = EventInvoker.lookup(Affects.class);
    public static final EventInvoker<Apply> APPLY = EventInvoker.lookup(Apply.class);
    public static final EventInvoker<Remove> REMOVE = EventInvoker.lookup(Remove.class);
    public static final EventInvoker<Expire> EXPIRE = EventInvoker.lookup(Expire.class);

    private MobEffectEvents() {

    }

    @FunctionalInterface
    public interface Affects {

        /**
         * Called when the game checks whether a new {@link MobEffectInstance} can be applied to a {@link LivingEntity} in {@link LivingEntity#canBeAffected(MobEffectInstance)}.
         * <p>This is used for mobs such as spiders to make them immune to poison, or wither mobs to make them immune to the wither effect.
         *
         * @param entity the entity the check is run for
         * @param effect the effect instance to check
         * @return {@link EventResult#ALLOW} to allow this effect to be applied to the entity,
         * {@link EventResult#DENY} to prevent this effect from being applied to the entity,
         * {@link EventResult#PASS} to let vanilla logic handle this case
         */
        EventResult onMobEffectAffects(LivingEntity entity, MobEffectInstance effect);
    }

    @FunctionalInterface
    public interface Apply {

        /**
         * Called when a new {@link MobEffectInstance} is added to a {@link LivingEntity} in {@link LivingEntity#addEffect(MobEffectInstance, Entity)}.
         * <p>If another effect with the same {@link MobEffect} is already present, the existing effect instance will be updated.
         *
         * @param entity       the living entity the effect instance is added to
         * @param effect       the effect instance being added
         * @param oldEffect    a potentially already existing effect instance with the same mob effect type
         * @param effectSource a potential other entity responsible for inflicting this new effect
         */
        void onMobEffectApply(LivingEntity entity, MobEffectInstance effect, @Nullable MobEffectInstance oldEffect, @Nullable Entity effectSource);
    }

    @FunctionalInterface
    public interface Remove {

        /**
         * Called when a {@link MobEffectInstance} is removed from a {@link LivingEntity} in {@link LivingEntity#removeEffect(MobEffect)}.
         * <p>Most notable this event runs when finishing drinking milk, and allows for preventing certain effects from being removed as a result.
         *
         * @param entity the entity the effect instance is to be removed from
         * @param effect the effect instance to remove
         * @return {@link EventResult#INTERRUPT} to prevent this effect instance from being removed,
         * {@link EventResult#PASS} to let vanilla logic handle this case
         */
        EventResult onMobEffectRemove(LivingEntity entity, MobEffectInstance effect);
    }

    @FunctionalInterface
    public interface Expire {

        /**
         * Called when a {@link MobEffectInstance} is removed from a {@link LivingEntity} in <code>net.minecraft.world.entity.LivingEntity#tickEffects</code> due to the instance duration having run out.
         * <p>This is the case when <code>net.minecraft.world.effect.MobEffectInstance#hasRemainingDuration()</code> returns <code>false</code>.
         *
         * @param entity the living entity the effect instance has run out on
         * @param effect the mob effect instance that has run out
         */
        void onMobEffectExpire(LivingEntity entity, MobEffectInstance effect);
    }
}
