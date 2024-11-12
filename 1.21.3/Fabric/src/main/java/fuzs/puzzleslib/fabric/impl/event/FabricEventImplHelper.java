package fuzs.puzzleslib.fabric.impl.event;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.OptionalInt;

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

    public static int onComputeEnchantedLootBonus(Holder<Enchantment> enchantment, int enchantmentLevel, LootContext lootContext) {
        Entity entity = lootContext.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof LivingEntity livingEntity)) return enchantmentLevel;
        DamageSource damageSource = lootContext.getOptionalParameter(LootContextParams.DAMAGE_SOURCE);
        return onComputeEnchantedLootBonus(enchantment, enchantmentLevel, livingEntity, damageSource);
    }

    public static int onComputeEnchantedLootBonus(Holder<Enchantment> enchantment, int enchantmentLevel, LivingEntity livingEntity, @Nullable DamageSource damageSource) {
        MutableInt mutableInt = MutableInt.fromValue(enchantmentLevel);
        FabricLivingEvents.COMPUTE_ENCHANTED_LOOT_BONUS.invoker().onComputeEnchantedLootBonus(livingEntity,
                damageSource, enchantment, mutableInt
        );
        return mutableInt.getAsInt();
    }

    public static boolean tryOnLivingDrops(LivingEntity entity, DamageSource damageSource, int lastHurtByPlayerTime) {
        Collection<ItemEntity> capturedDrops = ((CapturedDropsEntity) entity).puzzleslib$acceptCapturedDrops(null);
        if (capturedDrops != null) {
            EventResult result = FabricLivingEvents.LIVING_DROPS.invoker().onLivingDrops(entity, damageSource,
                    capturedDrops, lastHurtByPlayerTime > 0
            );
            if (result.isPass()) capturedDrops.forEach(itemEntity -> entity.level().addFreshEntity(itemEntity));
            return true;
        } else {
            return false;
        }
    }

    public static void tickAirSupply(LivingEntity entity, int originalAirSupply, boolean canRefillAir, boolean tryDrown) {
        boolean canLoseAir = !entity.canBreatheUnderwater() && !MobEffectUtil.hasWaterBreathing(entity) &&
                (!(entity instanceof Player) || !((Player) entity).getAbilities().invulnerable);
        tickAirSupply(entity, originalAirSupply, canRefillAir, canLoseAir, tryDrown);
    }

    public static void tickAirSupply(LivingEntity entity, int originalAirSupply, boolean canRefillAir, boolean canLoseAir, boolean tryDrown) {
        DefaultedInt airAmount = DefaultedInt.fromValue(entity.getAirSupply() - originalAirSupply);
        EventResult result = FabricLivingEvents.LIVING_BREATHE.invoker().onLivingBreathe(entity, airAmount,
                canRefillAir, canLoseAir
        );
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
        EventResult result = FabricLivingEvents.LIVING_DROWN.invoker().onLivingDrown(entity, entity.getAirSupply(),
                isDrowning
        );
        if (result.isInterrupt()) isDrowning = result.getAsBoolean();
        if (isDrowning) {
            entity.setAirSupply(0);
            Vec3 deltaMovement = entity.getDeltaMovement();
            for (int i = 0; i < 8; ++i) {
                double offsetX = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                double offsetY = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                double offsetZ = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                entity.level().addParticle(ParticleTypes.BUBBLE, entity.getX() + offsetX, entity.getY() + offsetY,
                        entity.getZ() + offsetZ, deltaMovement.x, deltaMovement.y, deltaMovement.z
                );
            }
            entity.hurt(entity.damageSources().drown(), 2.0F);
        }
    }
}
