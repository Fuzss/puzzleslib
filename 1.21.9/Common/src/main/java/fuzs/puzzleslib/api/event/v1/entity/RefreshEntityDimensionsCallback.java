package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

@FunctionalInterface
public interface RefreshEntityDimensionsCallback {
    EventInvoker<RefreshEntityDimensionsCallback> EVENT = EventInvoker.lookup(RefreshEntityDimensionsCallback.class);

    /**
     * Called when the size of an entity changes, usually from switching to a different {@link Pose}.
     *
     * @param entity           the entity
     * @param pose             the current entity pose
     * @param entityDimensions the new dimensions going to be set
     * @return <ul>
     *         <li>{@link EventResultHolder#interrupt(Object)} to force the provided dimensions to be set</li>
     *         <li>{@link EventResultHolder#pass()} to allow the original dimensions to be set</li>
     *         </ul>
     */
    EventResultHolder<EntityDimensions> onRefreshEntityDimensions(Entity entity, Pose pose, EntityDimensions entityDimensions);
}
