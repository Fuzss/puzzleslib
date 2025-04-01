package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;

public interface LivingDropsCallback {
    EventInvoker<LivingDropsCallback> EVENT = EventInvoker.lookup(LivingDropsCallback.class);

    /**
     * Called right before drops from a killed entity are spawned in the world.
     * <p>
     * This allows to change the loot that is dropped.
     * <p>
     * The looting level can be obtained via
     * {@link fuzs.puzzleslib.api.item.v2.EnchantingHelper#getMobLootingLevel(Entity, Entity, DamageSource)}.
     *
     * @param livingEntity the entity that has been killed
     * @param damageSource damage source that killed the entity
     * @param itemDrops    all drops, including equipment, so not just drops from the entity's loot table; this can be
     *                     modified
     * @param recentlyHit  does this count as a player kill, meaning
     *                     {@link LivingEntity#getLastHurtByPlayerMemoryTime()} is not zero
     * @return {@link EventResult#INTERRUPT} to prevent any drops from spawning, {@link EventResult#PASS} to allow drops
     *         to be spawned in the world; will have the same result as {@link EventResult#INTERRUPT} if drops are
     *         empty
     */
    EventResult onLivingDrops(LivingEntity livingEntity, DamageSource damageSource, Collection<ItemEntity> itemDrops, boolean recentlyHit);
}
