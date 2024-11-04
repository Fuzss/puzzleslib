package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface LivingExperienceDropCallback {
    EventInvoker<LivingExperienceDropCallback> EVENT = EventInvoker.lookup(LivingExperienceDropCallback.class);

    /**
     * Called right before xp drops are spawned in the world. Allows for cancelling drops or changing the amount.
     *
     * @param entity            the entity that died
     * @param attackingPlayer   the player that killed the entity
     * @param droppedExperience amount of xp dropped by vanilla, allows for setting a custom amount
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent any xp from dropping</li>
     *         <li>{@link EventResult#PASS PASS} to drop the experience amount set via the event</li>
     *         </ul>
     */
    EventResult onLivingExperienceDrop(LivingEntity entity, @Nullable Player attackingPlayer, DefaultedInt droppedExperience);
}
