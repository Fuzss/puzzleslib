package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.api.event.v1.entity.player.AnvilRepairCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.BonemealCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerXpEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;

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
}
