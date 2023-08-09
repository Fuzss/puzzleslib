package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.api.event.v1.entity.player.*;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

/**
 * Events originally found on Forge in the <code>net.minecraftforge.event.entity.player</code> package.
 */
public final class FabricPlayerEvents {
    /**
     * Called when a {@link Player} collides with an {@link ExperienceOrb} entity, just before it is added to the player.
     */
    public static final Event<PlayerXpEvents.PickupXp> PICKUP_XP = FabricEventFactory.createResult(PlayerXpEvents.PickupXp.class);
    /**
     * Called when a bone meal is used on a block by the player, a dispenser, or a farmer villager.
     * <p>Useful for adding custom bone meal behavior to blocks, or for cancelling vanilla interactions.
     */
    public static final Event<BonemealCallback> BONEMEAL = FabricEventFactory.createResult(BonemealCallback.class);
    /**
     * Called when the player takes the output item from an anvil, used to determine the chance by which the anvil will break down one stage.
     */
    public static final Event<AnvilRepairCallback> ANVIL_REPAIR = FabricEventFactory.create(AnvilRepairCallback.class);
    /**
     * Called when a player touches an {@link ItemEntity} laying on the ground.
     */
    public static final Event<ItemTouchCallback> ITEM_TOUCH = FabricEventFactory.createResult(ItemTouchCallback.class);
    /**
     * Called when the player picks up an {@link ItemEntity} from the ground after the {@link ItemStack} has been added to the player inventory.
     * <p>This events main purpose is to notify that the item pickup has happened.
     */
    public static final Event<PlayerEvents.ItemPickup> ITEM_PICKUP = FabricEventFactory.create(PlayerEvents.ItemPickup.class);
    /**
     * Called when the player stops using a bow, just before the arrow is fired.
     */
    public static final Event<ArrowLooseCallback> ARROW_LOOSE = FabricEventFactory.createResult(ArrowLooseCallback.class);
    /**
     * Fires at the beginning of {@link Player#tick()}.
     */
    public static final Event<PlayerTickEvents.Start> PLAYER_TICK_START = FabricEventFactory.create(PlayerTickEvents.Start.class);
    /**
     * Fires at the end of {@link Player#tick()}.
     */
    public static final Event<PlayerTickEvents.End> PLAYER_TICK_END = FabricEventFactory.create(PlayerTickEvents.End.class);
    /**
     * Called before a result item is generated from the two input slots in an anvil in {@link AnvilMenu#createResult()}.
     */
    public static final Event<AnvilUpdateCallback> ANVIL_UPDATE = FabricEventFactory.createResult(AnvilUpdateCallback.class);
    /**
     * Called when an item is tossed from the player inventory, either by pressing 'Q' or by clicking an item stack outside a container screen.
     */
    public static final Event<ItemTossCallback> ITEM_TOSS = FabricEventFactory.createResult(ItemTossCallback.class);
}
