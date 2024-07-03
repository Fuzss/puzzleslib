package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EnchantedCountIncreaseFunction.class)
abstract class EnchantedCountIncreaseFunctionFabricMixin {
    @Shadow
    @Final
    private Holder<Enchantment> enchantment;

    @ModifyVariable(method = "run", at = @At("STORE"), ordinal = 0)
    public int run(int enchantmentLevel, ItemStack itemStack, LootContext lootContext) {
        return FabricEventImplHelper.onComputeLootingLevel(this.enchantment, enchantmentLevel, lootContext);
    }
}
