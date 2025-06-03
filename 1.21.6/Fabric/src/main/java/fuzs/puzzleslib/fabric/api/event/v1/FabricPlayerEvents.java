package fuzs.puzzleslib.fabric.api.event.v1;

import fuzs.puzzleslib.api.event.v1.entity.player.*;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Events originally found on Forge in the <code>net.minecraftforge.event.entity.player</code> package.
 */
public final class FabricPlayerEvents {
    /**
     * Called when a {@link Player} collides with an {@link ExperienceOrb} entity, just before it is added to the
     * player.
     */
    public static final Event<PickupExperienceCallback> PICKUP_XP = FabricEventFactory.createResult(
            PickupExperienceCallback.class);
    /**
     * Called when a bone meal is used on a block by the player, a dispenser, or a farmer villager.
     */
    public static final Event<UseBoneMealCallback> USE_BONE_MEAL = FabricEventFactory.createResult(UseBoneMealCallback.class);
    /**
     * Called after a result item is created from the two input slots in an anvil via {@link AnvilMenu#createResult()}.
     */
    public static final Event<CreateAnvilResultCallback> CREATE_ANVIL_RESULT = FabricEventFactory.createResult(
            CreateAnvilResultCallback.class);
    /**
     * Called when a player touches an {@link ItemEntity} laying on the ground.
     */
    public static final Event<ItemEntityEvents.Touch> ITEM_TOUCH = FabricEventFactory.createResult(ItemEntityEvents.Touch.class);
    /**
     * Called when the player picks up an {@link ItemEntity} from the ground after the {@link ItemStack} has been added
     * to the player inventory.
     * <p>This events main purpose is to notify that the item pickup has happened.
     */
    public static final Event<ItemEntityEvents.Pickup> ITEM_PICKUP = FabricEventFactory.create(ItemEntityEvents.Pickup.class);
    /**
     * Called when the player stops using a bow, just before the arrow is fired.
     */
    public static final Event<ArrowLooseCallback> ARROW_LOOSE = FabricEventFactory.createResult(ArrowLooseCallback.class);
    /**
     * Called at the beginning of {@link Player#tick()}.
     */
    public static final Event<PlayerTickEvents.Start> PLAYER_TICK_START = FabricEventFactory.create(PlayerTickEvents.Start.class);
    /**
     * Called at the end of {@link Player#tick()}.
     */
    public static final Event<PlayerTickEvents.End> PLAYER_TICK_END = FabricEventFactory.create(PlayerTickEvents.End.class);
    /**
     * Called when an item is tossed from the player inventory, either by pressing 'Q' or by clicking an item stack
     * outside a container screen.
     */
    public static final Event<ItemEntityEvents.Toss> ITEM_TOSS = FabricEventFactory.createResult(ItemEntityEvents.Toss.class);
    /**
     * Called when the player attempts to harvest a block in {@link Player#getDestroySpeed(BlockState)}.
     */
    public static final Event<BreakSpeedCallback> BREAK_SPEED = FabricEventFactory.createResult(BreakSpeedCallback.class);
    /**
     * Called when the grindstone output slot is populated in {@link GrindstoneMenu#createResult()}.
     */
    public static final Event<GrindstoneEvents.Update> GRINDSTONE_UPDATE = FabricEventFactory.createResult(
            GrindstoneEvents.Update.class);
    /**
     * Called when the result item is taken from the output slot of a grindstone. This callback allows for handling
     * input items present in the corresponding slots.
     */
    public static final Event<GrindstoneEvents.Use> GRINDSTONE_USE = FabricEventFactory.create(GrindstoneEvents.Use.class);
    /**
     * Called after the player has opened a container.
     */
    public static final Event<ContainerEvents.Open> CONTAINER_OPEN = FabricEventFactory.create(ContainerEvents.Open.class);
    /**
     * Called when the player is closing an open container.
     */
    public static final Event<ContainerEvents.Close> CONTAINER_CLOSE = FabricEventFactory.create(ContainerEvents.Close.class);
    /**
     * Called before an entity starts being tracked by a player.
     * <p>
     * Fabric Api has this event as well via
     * {@link net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents#START_TRACKING}, but it fires too early before
     * the client is notified about the entity.
     */
    public static final Event<PlayerTrackingEvents.Start> START_TRACKING = FabricEventFactory.create(
            PlayerTrackingEvents.Start.class);

    private FabricPlayerEvents() {
        // NO-OP
    }
}
