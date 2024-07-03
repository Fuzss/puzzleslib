package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ComputeLootingLevelCallback {
    EventInvoker<ComputeLootingLevelCallback> EVENT = EventInvoker.lookup(ComputeLootingLevelCallback.class);

    /**
     * Called just before a {@link LivingEntity} drops all its loot for determining the level of
     * {@link net.minecraft.world.item.enchantment.Enchantments#LOOTING} that should be applied to the drops.
     *
     * @param entity       the entity that is about to drop all its loot
     * @param damageSource the damage source that caused the entity to drop its loot
     * @param lootingLevel the current looting level
     */
    void onComputeLootingLevel(LivingEntity entity, @Nullable DamageSource damageSource, MutableInt lootingLevel);
}
