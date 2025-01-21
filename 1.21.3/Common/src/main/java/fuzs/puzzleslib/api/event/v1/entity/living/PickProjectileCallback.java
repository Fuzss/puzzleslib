package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface PickProjectileCallback {
    EventInvoker<PickProjectileCallback> EVENT = EventInvoker.lookup(PickProjectileCallback.class);

    /**
     * Fired when an entity attempts to find a valid projectile via {@link LivingEntity#getProjectile(ItemStack)}.
     *
     * @param livingEntity        the living entity
     * @param weaponItemStack     the ranged weapon item stack
     * @param projectileItemStack the ammo item stack, possibly empty
     */
    void onPickProjectile(LivingEntity livingEntity, ItemStack weaponItemStack, MutableValue<ItemStack> projectileItemStack);
}
