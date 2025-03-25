package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@FunctionalInterface
public interface ArrowLooseCallback {
    EventInvoker<ArrowLooseCallback> EVENT = EventInvoker.lookup(ArrowLooseCallback.class);

    /**
     * Called when the player stops using a bow or crossbow, just before the arrow is fired.
     *
     * @param player          the player firing the bow
     * @param weaponItemStack the weapon item stack
     * @param level           the level
     * @param chargeAmount    charge of the bow, can be changed to adjust the power the arrow is fired with
     * @param hasProjectile   does the player have ammo, is in creative, or has the infinity enchantment
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent an arrow from being fired</li>
     *         <li>{@link EventResult#PASS PASS} to let vanilla behavior run, which means an arrow is fired with all bow enchantments applied</li>
     *         </ul>
     */
    EventResult onArrowLoose(Player player, ItemStack weaponItemStack, Level level, MutableInt chargeAmount, boolean hasProjectile);
}
