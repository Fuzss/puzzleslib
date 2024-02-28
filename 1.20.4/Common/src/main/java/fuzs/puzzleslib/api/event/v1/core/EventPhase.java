package fuzs.puzzleslib.api.event.v1.core;

import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.event.core.EventPhaseImpl;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

/**
 * Event phases useful for handling <code>net.fabricmc.fabric.api.event.Event</code> on Fabric, equivalent to <code>net.minecraftforge.eventbus.api.EventPriority</code> on Forge.
 */
@ApiStatus.NonExtendable
public interface EventPhase {
    /**
     * Fabric's default event phase, equivalent to EventPriority#NORMAL on Forge.
     */
    EventPhase DEFAULT = new EventPhaseImpl(new ResourceLocation("fabric", "default"), null, null);
    /**
     * A phase to be used directly before the default phase, equivalent to EventPriority#HIGH on Forge.
     */
    EventPhase BEFORE = new EventPhaseImpl(PuzzlesLibMod.id("before"), DEFAULT, EventPhaseImpl.Ordering.BEFORE);
    /**
     * A phase to be used directly after the default phase, equivalent to EventPriority#LOW on Forge.
     */
    EventPhase AFTER = new EventPhaseImpl(PuzzlesLibMod.id("after"), DEFAULT, EventPhaseImpl.Ordering.AFTER);
    /**
     * A phase to be used as the very first phase, equivalent to EventPriority#HIGHEST on Forge.
     */
    EventPhase FIRST = new EventPhaseImpl(PuzzlesLibMod.id("first"), BEFORE, EventPhaseImpl.Ordering.BEFORE);
    /**
     * A phase to be used as the very last phase, equivalent to EventPriority#LOWEST on Forge.
     */
    EventPhase LAST = new EventPhaseImpl(PuzzlesLibMod.id("last"), AFTER, EventPhaseImpl.Ordering.AFTER);

    /**
     * @return the identifier used for registering this phase on Fabric
     */
    ResourceLocation identifier();

    /**
     * @return another event phase that runs before / after this one, the order is defined by {@link #applyOrdering(BiConsumer)}
     */
    EventPhase parent();

    /**
     * The ordering defines in which relation this event phase is to {@link #parent()}, if it is supposed to run before or afterward.
     *
     * @param consumer apply event phases to the Fabric event
     */
    void applyOrdering(BiConsumer<ResourceLocation, ResourceLocation> consumer);

    /**
     * Constructs a custom event phase that runs before <code>eventPhase</code>.
     *
     * @param eventPhase the event phase to run before
     * @return the custom event phase
     */
    static EventPhase early(EventPhase eventPhase) {
        return new EventPhaseImpl(PuzzlesLibMod.id("early_" + eventPhase.identifier().getPath()), eventPhase, EventPhaseImpl.Ordering.BEFORE);
    }

    /**
     * Constructs a custom event phase that runs after <code>eventPhase</code>.
     *
     * @param eventPhase the event phase to run after
     * @return the custom event phase
     */
    static EventPhase late(EventPhase eventPhase) {
        return new EventPhaseImpl(PuzzlesLibMod.id("late_" + eventPhase.identifier().getPath()), eventPhase, EventPhaseImpl.Ordering.AFTER);
    }
}
