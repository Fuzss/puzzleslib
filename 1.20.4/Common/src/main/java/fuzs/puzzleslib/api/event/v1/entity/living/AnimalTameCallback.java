package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface AnimalTameCallback {
    EventInvoker<AnimalTameCallback> EVENT = EventInvoker.lookup(AnimalTameCallback.class);

    /**
     * Called when a player is about to tame an animal, allows for preventing taming.
     *
     * @param animal the animal that is being tamed
     * @param player the player taming the animal
     * @return {@link EventResult#INTERRUPT} to prevent the animal from being tamed,
     * {@link EventResult#PASS} for taming to happen normally
     */
    EventResult onAnimalTame(Animal animal, Player player);
}
