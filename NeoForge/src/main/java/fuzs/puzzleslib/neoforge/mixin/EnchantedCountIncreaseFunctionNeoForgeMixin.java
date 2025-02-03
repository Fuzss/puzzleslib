package fuzs.puzzleslib.neoforge.mixin;

import fuzs.puzzleslib.neoforge.api.event.v1.entity.living.ComputeEnchantedLootBonusEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantedCountIncreaseFunction.class)
abstract class EnchantedCountIncreaseFunctionNeoForgeMixin {
    @Shadow
    @Final
    private Holder<Enchantment> enchantment;
    @Shadow
    @Final
    private NumberProvider value;
    @Shadow
    @Final
    private int limit;

    @Shadow
    private boolean hasLimit() {
        throw new RuntimeException();
    }

    @ModifyVariable(method = "run", at = @At("STORE"), ordinal = 0)
    public int run(int enchantmentLevel, ItemStack itemStack, LootContext lootContext) {
        return ComputeEnchantedLootBonusEvent.onComputeEnchantedLootBonus(this.enchantment,
                enchantmentLevel,
                lootContext);
    }

    @Inject(method = "run", at = @At("HEAD"))
    public void run(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> callback) {
        // dispatch this in addition to the other hook, so we do not depend on the attacking entity being a living entity
        // or being present at all (like when damage is coming from a block)
        // no need to cancel, vanilla does nothing but returning the item stack in the case of a non-living attacker
        if (!(context.getOptionalParameter(LootContextParams.ATTACKING_ENTITY) instanceof LivingEntity)) {
            int enchantmentLevel = ComputeEnchantedLootBonusEvent.onComputeEnchantedLootBonus(this.enchantment,
                    0,
                    context);
            if (enchantmentLevel != 0) {
                float f = (float) enchantmentLevel * this.value.getFloat(context);
                stack.grow(Math.round(f));
                if (this.hasLimit()) {
                    stack.limitSize(this.limit);
                }
            }
        }
    }
}
