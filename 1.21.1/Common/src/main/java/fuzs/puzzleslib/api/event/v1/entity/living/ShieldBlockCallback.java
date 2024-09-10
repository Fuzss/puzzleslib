package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface ShieldBlockCallback {
    EventInvoker<ShieldBlockCallback> EVENT = EventInvoker.lookup(ShieldBlockCallback.class);

    /**
     * Called right before damage from an incoming attack is negated via blocking using a shield.
     * <p>The <code>shieldTakesDamage</code> parameter from Forge is not implemented.
     *
     * @param blocker           the entity that is using a shield to block an incoming attack
     * @param source            the damage source attacking the <code>blocker</code>
     * @param blockedDamage     the amount of damage that will be blocked, the original state is the full damage amount the <code>blocker</code> is attacked with
     * @return {@link EventResult#INTERRUPT} to prevent the shield block from happening, the attacked entity will receive the full damage,
     * {@link EventResult#PASS} to allow the block to happen with values from <code>blockedDamage</code> and <code>shieldTakesDamage</code>
     */
    EventResult onShieldBlock(LivingEntity blocker, DamageSource source, DefaultedFloat blockedDamage);
}
