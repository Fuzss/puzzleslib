package fuzs.puzzleslib.neoforge.mixin;

import fuzs.puzzleslib.neoforge.api.event.v1.entity.living.ComputeEnchantedLootBonusEvent;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EnchantmentHelper.class)
abstract class EnchantmentHelperNeoForgeMixin {

    @ModifyVariable(method = "lambda$processEquipmentDropChance$25", at = @At("HEAD"), argsOnly = true)
    private static int processEquipmentDropChance$0(int enchantmentLevel, ServerLevel level, LivingEntity entity, DamageSource damageSource, MutableFloat mutableFloat, RandomSource randomSource, Holder<Enchantment> enchantment, int enchantmentLevelX, EnchantedItemInUse enchantedItemInUse) {
        return ComputeEnchantedLootBonusEvent.onComputeEnchantedLootBonus(enchantment, enchantmentLevel, entity,
                damageSource
        );
    }

    @ModifyVariable(method = "lambda$processEquipmentDropChance$27", at = @At("HEAD"), argsOnly = true)
    private static int processEquipmentDropChance$1(int enchantmentLevel, ServerLevel level, LivingEntity entity, DamageSource damageSource, MutableFloat mutableFloat, RandomSource randomSource, Holder<Enchantment> enchantment, int enchantmentLevelX, EnchantedItemInUse enchantedItemInUse) {
        return ComputeEnchantedLootBonusEvent.onComputeEnchantedLootBonus(enchantment, enchantmentLevel, entity,
                damageSource
        );
    }
}
