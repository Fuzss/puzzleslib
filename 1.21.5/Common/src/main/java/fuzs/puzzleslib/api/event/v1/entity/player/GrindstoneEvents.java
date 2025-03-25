package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;

public final class GrindstoneEvents {
    public static final EventInvoker<Update> UPDATE = EventInvoker.lookup(Update.class);
    public static final EventInvoker<Use> USE = EventInvoker.lookup(Use.class);

    private GrindstoneEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Update {

        /**
         * Called when the grindstone output slot is populated in {@link GrindstoneMenu#createResult()}.
         *
         * @param primaryItemStack   the top input item stack
         * @param secondaryItemStack the bottom input item stack
         * @param outputItemStack    the output item stack, which is still empty by default as the callback runs before
         *                           any vanilla code
         * @param experienceReward   the experience to gain from this operation, spawned as experience entities in the
         *                           world
         * @param player             the player using the grindstone
         * @return <ul>
         *         <li>{@link EventResult#ALLOW ALLOW} to interrupt vanilla and set the new values from this event</li>
         *         <li>{@link EventResult#DENY DENY} to prevent the output slot from updating, it will remain empty or be cleared</li>
         *         <li>{@link EventResult#PASS PASS} to only listen to the event without applying any changes</li>
         *         </ul>
         */
        EventResult onGrindstoneUpdate(ItemStack primaryItemStack, ItemStack secondaryItemStack, MutableValue<ItemStack> outputItemStack, MutableInt experienceReward, Player player);
    }

    @FunctionalInterface
    public interface Use {

        /**
         * Called when the result item is taken from the output slot of a grindstone. This callback allows for handling
         * input items present in the corresponding slots.
         *
         * @param primaryItemStack   the top input item stack with the default value being the current item stack before
         *                           it is cleared during the operation, when a new value is set it will be set to the
         *                           top slot instead of the slot being cleared
         * @param secondaryItemStack the bottom input item stack with the default value being the current item stack
         *                           before it is cleared during the operation, when a new value is set it will be set
         *                           to the bottom slot instead of the slot being cleared
         * @param player             the player using the grindstone
         */
        void onGrindstoneUse(DefaultedValue<ItemStack> primaryItemStack, DefaultedValue<ItemStack> secondaryItemStack, Player player);
    }
}
