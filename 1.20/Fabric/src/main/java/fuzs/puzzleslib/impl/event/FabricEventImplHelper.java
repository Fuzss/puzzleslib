package fuzs.puzzleslib.impl.event;

import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Supplier;

public final class FabricEventImplHelper {

    private FabricEventImplHelper() {

    }

    public static MutableInt onLootingLevel(LivingEntity entity, DamageSource damageSource, int lootingLevel) {
        MutableInt mutableInt = MutableInt.fromValue(lootingLevel);
        FabricLivingEvents.LOOTING_LEVEL.invoker().onLootingLevel(entity, damageSource, mutableInt);
        return mutableInt;
    }

    public static boolean tryOnLivingDrops(LivingEntity entity, DamageSource damageSource, int lastHurtByPlayerTime, Supplier<MutableInt> supplier) {
        Collection<ItemEntity> capturedDrops = ((CapturedDropsEntity) entity).puzzleslib$acceptCapturedDrops(null);
        if (capturedDrops != null) {
            MutableInt lootingLevel = supplier.get();
            Objects.requireNonNull(lootingLevel, "looting level is null");
            EventResult result = FabricLivingEvents.LIVING_DROPS.invoker().onLivingDrops(entity, damageSource, capturedDrops, lootingLevel.getAsInt(), lastHurtByPlayerTime > 0);
            if (result.isPass()) capturedDrops.forEach(itemEntity -> entity.level().addFreshEntity(itemEntity));
            return true;
        } else {
            return false;
        }
    }

    public static void tickAirSupply(LivingEntity entity, int originalAirSupply, boolean canRefillAir, boolean tryDrown) {
        boolean canLoseAir = !entity.canBreatheUnderwater() && !MobEffectUtil.hasWaterBreathing(entity) && (!(entity instanceof Player) || !((Player) entity).getAbilities().invulnerable);
        tickAirSupply(entity, originalAirSupply, canRefillAir, canLoseAir, tryDrown);
    }

    public static void tickAirSupply(LivingEntity entity, int originalAirSupply, boolean canRefillAir, boolean canLoseAir, boolean tryDrown) {
        DefaultedInt airAmount = DefaultedInt.fromValue(entity.getAirSupply() - originalAirSupply);
        EventResult result = FabricLivingEvents.LIVING_BREATHE.invoker().onLivingBreathe(entity, airAmount, canRefillAir, canLoseAir);
        if (result.isInterrupt()) {
            entity.setAirSupply(originalAirSupply);
        } else {
            OptionalInt optional = airAmount.getAsOptionalInt();
            if (optional.isPresent()) {
                entity.setAirSupply(Math.min(originalAirSupply + optional.getAsInt(), entity.getMaxAirSupply()));
            }
        }
        if (tryDrown) tryDrownEntity(entity);
    }

    private static void tryDrownEntity(LivingEntity entity) {
        if (entity.getAirSupply() > 0) return;
        boolean isDrowning = entity.getAirSupply() <= -20;
        EventResult result = FabricLivingEvents.LIVING_DROWN.invoker().onLivingDrown(entity, entity.getAirSupply(), isDrowning);
        if (result.isInterrupt()) isDrowning = result.getAsBoolean();
        if (isDrowning) {
            entity.setAirSupply(0);
            Vec3 deltaMovement = entity.getDeltaMovement();
            for (int i = 0; i < 8; ++i) {
                double offsetX = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                double offsetY = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                double offsetZ = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                entity.level().addParticle(ParticleTypes.BUBBLE, entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ, deltaMovement.x, deltaMovement.y, deltaMovement.z);
            }
            entity.hurt(entity.damageSources().drown(), 2.0F);
        }
    }
}
