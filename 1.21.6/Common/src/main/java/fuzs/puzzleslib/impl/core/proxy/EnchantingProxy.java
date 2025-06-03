package fuzs.puzzleslib.impl.core.proxy;

import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface EnchantingProxy {

    float getEnchantPowerBonus(BlockState blockState, Level level, BlockPos blockPos);

    boolean canApplyAtEnchantingTable(Holder<Enchantment> enchantment, ItemStack itemStack);

    @Deprecated(forRemoval = true)
    default boolean isBookEnchantable(ItemStack inputItemStack, ItemStack bookItemStack) {
        return true;
    }

    default int getMobLootingLevel(Entity target, @Nullable Entity attacker, @Nullable DamageSource damageSource) {
        if (attacker instanceof LivingEntity livingEntity) {
            Holder<Enchantment> enchantment = LookupHelper.lookupEnchantment(target, Enchantments.LOOTING);
            return EnchantmentHelper.getEnchantmentLevel(enchantment, livingEntity);
        } else {
            return 0;
        }
    }
}
