package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface ShieldBlockCallback {
    EventInvoker<ShieldBlockCallback> EVENT = EventInvoker.lookup(ShieldBlockCallback.class);

    /**
     * Called right before damage from an incoming attack is negated via blocking using an item providing
     * {@link net.minecraft.core.component.DataComponents#BLOCKS_ATTACKS}.
     *
     * @param blockingEntity the entity that is using a shield to block an incoming attack
     * @param damageSource   the damage source attacking the blocking entity
     * @param blockedDamage  the amount of damage that will be blocked, usually the full damage amount the entity is
     *                       attacked with
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the shield block from happening, the attacked entity will receive the full damage amount</li>
     *         <li>{@link EventResult#PASS PASS} to allow the block to happen with the entity taking no damage</li>
     *         </ul>
     */
    EventResult onShieldBlock(LivingEntity blockingEntity, DamageSource damageSource, MutableFloat blockedDamage);
}
