package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
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
abstract class EnchantmentHelperFabrixMixin {

    @ModifyVariable(method = "lambda$processEquipmentDropChance$24", at = @At("HEAD"), argsOnly = true)
    private static int processEquipmentDropChance$0(int enchantmentValue, ServerLevel level, LivingEntity entity, DamageSource damageSource, MutableFloat mutableFloat, RandomSource randomSource, Holder<Enchantment> enchantment, int _enchantmentLevel, EnchantedItemInUse enchantedItemInUse) {
        return FabricEventImplHelper.onComputeLootingLevel(enchantment, enchantmentValue, entity, damageSource);
    }

    @ModifyVariable(method = "lambda$processEquipmentDropChance$26", at = @At("HEAD"), argsOnly = true)
    private static int processEquipmentDropChance$1(int enchantmentValue, ServerLevel level, LivingEntity entity, DamageSource damageSource, MutableFloat mutableFloat, RandomSource randomSource, Holder<Enchantment> enchantment, int _enchantmentLevel, EnchantedItemInUse enchantedItemInUse) {
        return FabricEventImplHelper.onComputeLootingLevel(enchantment, enchantmentValue, entity, damageSource);
    }
}
