package fuzs.puzzleslib.api.core.v1.context;

import com.google.common.base.Preconditions;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.Objects;

/**
 * Allows for registering a new {@link DispenseItemBehavior} for multiple items at once.
 */
public final class DispenseBehaviorsContext {

    /**
     * Registers a new dispense behavior for a set of items.
     *
     * @param behavior the dispense behavior to register
     * @param items    the items to register <code>behavior</code> for
     */
    public void registerBehavior(DispenseItemBehavior behavior, ItemLike... items) {
        Objects.requireNonNull(behavior, "behavior is null");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkPositionIndex(0, items.length - 1, "items is empty");
        for (ItemLike item : items) {
            Objects.requireNonNull(item, "item is null");
            DispenserBlock.registerBehavior(item, behavior);
        }
    }
}
