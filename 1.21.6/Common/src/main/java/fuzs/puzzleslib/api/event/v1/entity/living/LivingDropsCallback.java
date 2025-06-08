package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;

public interface LivingDropsCallback {
    EventInvoker<LivingDropsCallback> EVENT = EventInvoker.lookup(LivingDropsCallback.class);

    /**
     * Called right before drops from a killed entity are spawned in the world.
     * <p>
     * This allows changing the loot that is dropped.
     *
     * @param livingEntity the entity that has been killed
     * @param damageSource the damage source that killed the entity
     * @param itemDrops    the modifiable drops; including both loot table items and equipment
     * @param recentlyHit  does this count as a player kill, meaning
     *                     {@link LivingEntity#getLastHurtByPlayerMemoryTime()} is not zero
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent any drops from spawning, equal to clearing the list of drops</li>
     *         <li>{@link EventResult#PASS PASS} to allow drops to be spawned in the world</li>
     *         </ul>
     */
    EventResult onLivingDrops(LivingEntity livingEntity, DamageSource damageSource, Collection<ItemEntity> itemDrops, boolean recentlyHit);
}
