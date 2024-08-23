package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

public final class AnvilEvents {
    public static final EventInvoker<Update> UPDATE = EventInvoker.lookup(Update.class);
    public static final EventInvoker<Use> USE = EventInvoker.lookup(Use.class);

    private AnvilEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Update {

        /**
         * Called before a result item is generated from the two input slots in an anvil in
         * {@link AnvilMenu#createResult()}.
         *
         * @param primaryItem     the item stack placed in the left anvil input slot
         * @param secondaryItem   the item stack placed in the right anvil input slot
         * @param outputItem      access to the item that will be placed in the result slot, always empty by default,
         *                        vanilla logic will be cancelled when this is no longer empty
         * @param itemName        item name entered into the anvil name text box
         * @param enchantmentCost level cost for this operation
         * @param materialCost    material repair cost for this operation
         * @param player          the player interacting with the menu
         * @return {@link EventResult#ALLOW} to set a custom result from the specified output item stack with the values
         *         specified for enchantment cost and material cost, {@link EventResult#DENY} to prevent vanilla logic
         *         from running, the output item stack will not be updated, {@link EventResult#PASS} to only listen to
         *         the event without applying any changes made to mutable value provided by the event
         */
        EventResult onAnvilUpdate(ItemStack primaryItem, ItemStack secondaryItem, MutableValue<ItemStack> outputItem, String itemName, MutableInt enchantmentCost, MutableInt materialCost, Player player);
    }

    @FunctionalInterface
    public interface Use {

        /**
         * Called when the player takes the output item from an anvil, used to determine the chance by which the anvil
         * will break down one stage.
         *
         * @param player        the player interacting with the anvil
         * @param primaryItem   left input item stack
         * @param secondaryItem right input item stack
         * @param outputItem    the output stack the player is about to take
         * @param breakChance   chance for the anvil to break down one stage
         */
        void onAnvilUse(Player player, ItemStack primaryItem, ItemStack secondaryItem, ItemStack outputItem, MutableFloat breakChance);
    }
}
