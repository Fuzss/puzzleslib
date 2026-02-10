package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface EntityDamageImmunityCallback {
    EventInvoker<EntityDamageImmunityCallback> EVENT = EventInvoker.lookup(EntityDamageImmunityCallback.class);

    /**
     * Runs in {@link Entity#isInvulnerableToBase(DamageSource)} when an entity is attacked to determine if said entity
     * is invulnerable to the specific damage source.
     *
     * @param entity         the entity
     * @param damageSource   the damage source
     * @param isInvulnerable is the entity invulnerable to the damage source
     */
    void onEntityDamageImmunity(Entity entity, DamageSource damageSource, MutableBoolean isInvulnerable);
}
