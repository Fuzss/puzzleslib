package fuzs.puzzleslib.neoforge.api.event.v1.entity.living;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

/**
 * Called after goals have been registered for a mob. Useful for adding new goals and modifying existing ones.
 * <p>
 * This event is not {@link ICancellableEvent}.
 * <p>
 * This event is fired on the {@link NeoForge#EVENT_BUS}.
 */
public class SetupMobGoalsEvent extends LivingEvent {

    public SetupMobGoalsEvent(Mob mob) {
        super(mob);
    }

    @Override
    public Mob getEntity() {
        return (Mob) super.getEntity();
    }

    public GoalSelector getGoalSelector() {
        return this.getEntity().goalSelector;
    }

    public GoalSelector getTargetSelector() {
        return this.getEntity().targetSelector;
    }
}
