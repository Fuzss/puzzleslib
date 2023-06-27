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
     * <p>This event is fired whenever an Entity dies and drops items in {@link LivingEntity#die(DamageSource)}.
     * <p><code>drops</code> can be modified to change the loot that is dropped.
     *
     * @param entity       the entity that has been killed
     * @param source       damage source that killed the entity
     * @param drops        all drops, including equipment, so not just drops from the entity's loot table; this can be modified
     * @param lootingLevel looting level of killer weapon
     * @param recentlyHit  does this count as a player kill, meaning <code>lastHurtByPlayerTime</code> in {@link LivingEntity} is not zero
     * @return {@link EventResult#INTERRUPT} to prevent any drops from spawning,
     * {@link EventResult#PASS} to allow drops to be spawned in the world; will have the same result as {@link EventResult#INTERRUPT} if <code>drops</code> is empty
     */
    EventResult onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit);
}
