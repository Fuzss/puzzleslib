package fuzs.puzzleslib.api.container.v1;

import com.google.common.base.Predicates;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A utility for handling {@link AbstractContainerMenu#quickMoveStack(Player, int)} more conveniently.
 * <p>
 * Note that the order of individual matters, as the rules are tested using the original registration order.
 */
public final class QuickMoveRuleSet {
    private static final Predicate<Slot> IS_INVENTORY = (Slot slot) -> slot.container instanceof Inventory;
    private static final Predicate<Slot> IS_INVENTORY_ITEMS = (Slot slot) -> IS_INVENTORY.test(slot) &&
            slot.getContainerSlot() < Inventory.INVENTORY_SIZE;
    private static final Predicate<Slot> IS_INVENTORY_ARMOR = (Slot slot) -> IS_INVENTORY.test(slot) &&
            slot.getContainerSlot() >= Inventory.INVENTORY_SIZE && slot.getContainerSlot() < Inventory.SLOT_OFFHAND;
    private static final Predicate<Slot> IS_HOTBAR = (Slot slot) -> IS_INVENTORY_ITEMS.test(slot) &&
            Inventory.isHotbarSlot(slot.getContainerSlot());
    private static final Predicate<Slot> IS_NOT_HOTBAR = (Slot slot) -> IS_INVENTORY_ITEMS.test(slot) &&
            !IS_HOTBAR.test(slot);

    private final List<Rule> rules = new ArrayList<>();
    private final List<Slot> slots;
    private final Action action;
    private final Type type;

    private QuickMoveRuleSet(AbstractContainerMenu menu, Action action, Type type) {
        this.slots = menu.slots;
        this.action = action;
        this.type = type;
    }

    /**
     * Creates a new rule set instance.
     *
     * @param menu   the container menu
     * @param action access to the protected {@link AbstractContainerMenu#moveItemStackTo(ItemStack, int, int, boolean)}
     *               method
     * @return the rule set
     */
    public static QuickMoveRuleSet of(AbstractContainerMenu menu, Action action) {
        return of(menu, action, Type.RELAXED);
    }

    /**
     * Creates a new rule set instance.
     *
     * @param menu   the container menu
     * @param action access to the protected {@link AbstractContainerMenu#moveItemStackTo(ItemStack, int, int, boolean)}
     *               method
     * @param type   should rules not stop applying after a match has been found
     * @return the rule set
     */
    public static QuickMoveRuleSet of(AbstractContainerMenu menu, Action action, Type type) {
        return new QuickMoveRuleSet(menu, action, type);
    }

    /**
     * Applies the rule set, functions the same as {@link AbstractContainerMenu#quickMoveStack(Player, int)}.
     *
     * @param player the player entity
     * @param index  the clicked slot index
     * @return the original item stack or empty to prevent further quick move attempts
     */
    public ItemStack quickMoveStack(Player player, int index) {

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {

            ItemStack itemInSlot = slot.getItem();
            itemStack = itemInSlot.copy();

            for (Rule rule : this.rules) {
                if (rule.isValid() && rule.filter().test(slot)) {
                    if (!this.action.moveItemStackTo(itemInSlot,
                            rule.startIndex(),
                            rule.endIndex(),
                            rule.reverseDirection())) {
                        if (this.type == Type.STRICT) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        break;
                    }
                }
            }

            if (itemInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemInSlot.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemInSlot);
        }

        return this.type == Type.RELAXED ? ItemStack.EMPTY : itemStack;
    }

    /**
     * Move an item from anywhere to a specific slot.
     *
     * @param indices the slot indices
     * @return the rule set
     */
    public QuickMoveRuleSet addContainerSlotRule(int... indices) {
        for (int index : indices) {
            this.addContainerSlotRule(index);
        }
        return this;
    }

    /**
     * Move an item from anywhere to a specific slot.
     *
     * @param index the slot index
     * @return the rule set
     */
    public QuickMoveRuleSet addContainerSlotRule(int index) {
        return this.addContainerSlotRule(index, Predicates.alwaysTrue());
    }

    /**
     * Move an item from anywhere to a specific slot.
     *
     * @param index  the slot index
     * @param filter an additional filter for testing if the item can be placed into the slot at this index
     * @return the rule set
     */
    public QuickMoveRuleSet addContainerSlotRule(int index, Predicate<Slot> filter) {
        return this.addContainerSlotRule(index, false, filter);
    }

    /**
     * Move an item from anywhere to a specific slot.
     *
     * @param index            the slot index
     * @param reverseDirection iterate backwards when placing the item
     * @param filter           an additional filter for testing if the item can be placed into the slot at this index
     * @return the rule set
     */
    public QuickMoveRuleSet addContainerSlotRule(int index, boolean reverseDirection, Predicate<Slot> filter) {
        return this.addRule(new Rule(Rule.Type.CONTAINER_SLOT, index, index + 1, reverseDirection, (Slot slot) -> {
            return IS_INVENTORY.test(slot) && this.slots.get(index).mayPlace(slot.getItem()) && filter.test(slot);
        }));
    }

    /**
     * Move an item from the player inventory to a container.
     * <p>
     * Useful for containers that only have general purpose storage slots (e.g. chest).
     *
     * @param container the container
     * @return the rule set
     */
    public QuickMoveRuleSet addContainerRule(Container container) {
        return this.addContainerRule(this.getInclusiveStartIndex((Slot slot) -> slot.container == container),
                this.getExclusiveEndIndex((Slot slot) -> slot.container == container));
    }

    /**
     * Move an item from the player inventory to a set of indices.
     * <p>
     * Useful for containers that only have general purpose storage slots (e.g. chest).
     *
     * @param startIndex the first index (inclusive)
     * @param endIndex   the last index (exclusive)
     * @return the rule set
     */
    public QuickMoveRuleSet addContainerRule(int startIndex, int endIndex) {
        return this.addRule(new Rule(Rule.Type.CONTAINER, startIndex, endIndex, false, (Slot slot) -> {
            return slot.index >= this.getInclusiveStartIndex(IS_INVENTORY_ITEMS) &&
                    slot.index < this.getExclusiveEndIndex(IS_INVENTORY_ITEMS);
        }));
    }

    /**
     * Move an item to the player inventory.
     *
     * @return the rule set
     */
    public QuickMoveRuleSet addInventoryRules() {
        return this.addInventoryRules(true);
    }

    /**
     * Move an item to the player inventory.
     *
     * @param reverseDirection iterate backwards when placing the item
     * @return the rule set
     */
    public QuickMoveRuleSet addInventoryRules(boolean reverseDirection) {
        this.addInventoryRule(Rule.Type.INVENTORY_ARMOR, reverseDirection, IS_INVENTORY_ARMOR);
        return this.addInventoryRule(Rule.Type.INVENTORY_ITEMS, reverseDirection, IS_INVENTORY_ITEMS);
    }

    private QuickMoveRuleSet addInventoryRule(Rule.Type type, boolean reverseDirection, Predicate<Slot> filter) {
        return this.addRule(new Rule(type,
                this.getInclusiveStartIndex(filter),
                this.getExclusiveEndIndex(filter),
                reverseDirection,
                IS_INVENTORY.negate()));
    }

    /**
     * Move an item from the hotbar to the rest of player inventory, and the other way around.
     * <p>
     * Useful for containers that only have functional slots and no general purpose storage slots (e.g. enchanting
     * table).
     *
     * @return the rule set
     */
    public QuickMoveRuleSet addInventoryCompartmentRules() {
        this.addInventoryCompartmentRule(Rule.Type.TO_ARMOR,
                Rule.Type.FROM_ARMOR,
                IS_INVENTORY_ARMOR,
                IS_INVENTORY_ITEMS);
        return this.addInventoryCompartmentRule(Rule.Type.TO_HOTBAR, Rule.Type.FROM_HOTBAR, IS_HOTBAR, IS_NOT_HOTBAR);
    }

    private QuickMoveRuleSet addInventoryCompartmentRule(Rule.Type to, Rule.Type from, Predicate<Slot> toFilter, Predicate<Slot> fromFilter) {
        this.addRule(new Rule(to,
                this.getInclusiveStartIndex(toFilter),
                this.getExclusiveEndIndex(toFilter),
                false,
                (Slot slot) -> {
                    return slot.index >= this.getInclusiveStartIndex(fromFilter) &&
                            slot.index < this.getExclusiveEndIndex(fromFilter);
                }));
        return this.addRule(new Rule(from,
                this.getInclusiveStartIndex(fromFilter),
                this.getExclusiveEndIndex(fromFilter),
                false,
                (Slot slot) -> {
                    return slot.index >= this.getInclusiveStartIndex(toFilter) &&
                            slot.index < this.getExclusiveEndIndex(toFilter);
                }));
    }

    private QuickMoveRuleSet addRule(Rule rule) {
        this.rules.add(rule);
        return this;
    }

    private int getInclusiveStartIndex(Predicate<Slot> predicate) {
        for (int i = 0; i < this.slots.size(); i++) {
            if (predicate.test(this.slots.get(i))) {
                return i;
            }
        }

        return -1;
    }

    private int getExclusiveEndIndex(Predicate<Slot> predicate) {
        for (int i = this.slots.size() - 1; i >= 0; i--) {
            if (predicate.test(this.slots.get(i))) {
                return i + 1;
            }
        }

        return -1;
    }

    /**
     * An abstraction for {@link AbstractContainerMenu#moveItemStackTo(ItemStack, int, int, boolean)}.
     */
    @FunctionalInterface
    public interface Action {

        boolean moveItemStackTo(ItemStack itemStack, int startIndex, int endIndex, boolean reverseDirection);
    }

    /**
     * Controls how further attempts at moving remaining items in an item stack are made after a rule has already been
     * applied.
     */
    public enum Type {
        /**
         * Does not skip full slots matched by a rule.
         * <p>
         * No further quick move attempts are made after a slot allowing the item has been found (even when there are
         * valid slots with remaining capacity available that would be matched by later rules).
         */
        STRICT,
        /**
         * Skips full slots matched by a rule in the next quick move attempt.
         * <p>
         * Items can only go to slots handled by a single rule at once. The next attempt will be allowed to process
         * slots from different rules, if the previous slots are all filled.
         */
        RELAXED,
        /**
         * Skips full slots matched by a rule in the current quick move attempt.
         * <p>
         * Items can be split across slots handled by multiple rules.
         */
        LENIENT
    }

    private record Rule(Type type, int startIndex, int endIndex, boolean reverseDirection, Predicate<Slot> filter) {

        public boolean isValid() {
            return this.startIndex != -1 && this.endIndex != -1;
        }

        enum Type {
            CONTAINER_SLOT,
            CONTAINER,
            INVENTORY_ITEMS,
            INVENTORY_ARMOR,
            TO_HOTBAR,
            FROM_HOTBAR,
            TO_ARMOR,
            FROM_ARMOR
        }
    }
}
