package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;

@FunctionalInterface
public interface SetupMobGoalsCallback {
    EventInvoker<SetupMobGoalsCallback> EVENT = EventInvoker.lookup(SetupMobGoalsCallback.class);

    /**
     * Called after goals have been registered for a mob. Useful for adding new goals and modifying existing ones.
     *
     * @param mob            the mob
     * @param goalSelector   the goal selector for general mob behavior
     * @param targetSelector the target selector for targeting behavior of neutral and aggressive mobs
     */
    void onSetupMobGoals(Mob mob, GoalSelector goalSelector, GoalSelector targetSelector);
}
