package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LootItemRandomChanceWithLootingCondition.class)
public abstract class LootItemRandomChanceWithLootingConditionFabricMixin {

    @ModifyVariable(method = "test", at = @At("STORE"), ordinal = 0)
    public int test(int lootingLevel, LootContext lootContext) {
        Entity target = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(target instanceof LivingEntity livingEntity)) return lootingLevel;
        DamageSource damageSource = lootContext.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        MutableInt mutableLootingLevel = MutableInt.fromValue(lootingLevel);
        FabricLivingEvents.LOOTING_LEVEL.invoker().onLootingLevel(LivingEntity.class.cast(this), damageSource, mutableLootingLevel);
        return mutableLootingLevel.getAsInt();
    }
}
