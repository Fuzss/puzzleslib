package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface CreateAnvilResultCallback {
    EventInvoker<CreateAnvilResultCallback> EVENT = EventInvoker.lookup(CreateAnvilResultCallback.class);

    /**
     * Called after a result item is created from the two input slots in an anvil via {@link AnvilMenu#createResult()}.
     *
     * @param player               the player using the anvil
     * @param primaryItemStack     the item stack placed in the left input slot
     * @param secondaryItemStack   the item stack placed in the right input slot
     * @param outputItemStack      the item computed by vanilla for the result slot
     * @param itemName             the item name entered into the name text box
     * @param enchantmentLevelCost the level cost for this operation
     * @param repairMaterialCost   the material repair cost for this operation
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent vanilla logic from running, the output item stack will not be updated</li>
     *         <li>{@link EventResult#PASS PASS} to set a custom result from the specified output item stack with the values specified for enchantment level cost and material cost</li>
     *         </ul>
     */
    EventResult onCreateAnvilResult(Player player, ItemStack primaryItemStack, ItemStack secondaryItemStack, MutableValue<ItemStack> outputItemStack, @Nullable String itemName, MutableInt enchantmentLevelCost, MutableInt repairMaterialCost);
}
