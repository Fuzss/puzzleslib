package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface PickupExperienceCallback {
    EventInvoker<PickupExperienceCallback> EVENT = EventInvoker.lookup(PickupExperienceCallback.class);

    /**
     * Called when a {@link Player} collides with an {@link ExperienceOrb} entity, just before it is added to the player.
     *
     * @param player the player colliding with the orb
     * @param orb    the orb that's being collided with
     * @return {@link EventResult#INTERRUPT} to prevent the player from collecting orbs, they will remain unchanged in the world,
     * {@link EventResult#PASS} to proceed with the player picking up the orbs
     */
    EventResult onPickupExperience(Player player, ExperienceOrb orb);
}
