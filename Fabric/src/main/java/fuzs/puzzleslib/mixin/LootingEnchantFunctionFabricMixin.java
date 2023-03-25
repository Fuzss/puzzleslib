package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LootingEnchantFunction.class)
public abstract class LootingEnchantFunctionFabricMixin {

    @ModifyVariable(method = "run", at = @At("STORE"), ordinal = 0)
    public int run(int lootingLevel, ItemStack itemStack, LootContext lootContext) {
        Entity target = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(target instanceof LivingEntity livingEntity)) return lootingLevel;
        DamageSource damageSource = lootContext.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        MutableInt mutableLootingLevel = MutableInt.fromValue(lootingLevel);
        FabricLivingEvents.LOOTING_LEVEL.invoker().onLootingLevel(livingEntity, damageSource, mutableLootingLevel);
        return mutableLootingLevel.getAsInt();
    }
}
