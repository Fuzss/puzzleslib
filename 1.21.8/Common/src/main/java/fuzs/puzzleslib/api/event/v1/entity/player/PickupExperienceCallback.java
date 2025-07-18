package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface PickupExperienceCallback {
    EventInvoker<PickupExperienceCallback> EVENT = EventInvoker.lookup(PickupExperienceCallback.class);

    /**
     * Called when a {@link Player} collides with an {@link ExperienceOrb} entity, just before it is added to the
     * player.
     *
     * @param player        the player colliding with the orb
     * @param experienceOrb the orb that's being collided with
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the player from collecting experience orbs, they will remain unchanged in the level</li>
     *         <li>{@link EventResult#PASS PASS} to proceed with the player picking up the experience orbs</li>
     *         </ul>
     */
    EventResult onPickupExperience(Player player, ExperienceOrb experienceOrb);
}
