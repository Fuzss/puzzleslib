package fuzs.puzzleslib.api.events.v2;

import fuzs.puzzleslib.impl.PuzzlesLib;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.resources.ResourceLocation;

/**
 * Event phases useful for handling {@link net.fabricmc.fabric.api.event.Event}, similar to net.minecraftforge.eventbus.api.EventPriority on Forge.
 * <p>A phase ordering must first be added using {@link net.fabricmc.fabric.api.event.Event#addPhaseOrdering(ResourceLocation, ResourceLocation)}.
 * <p>Then the callback must be registered for the correct phase using {@link net.fabricmc.fabric.api.event.Event#register(ResourceLocation, Object)}.
 */
public final class DefaultEventPhases {
    /**
     * A phase to be used as the very first phase, equivalent to EventPriority#HIGHEST on Forge.
     */
    public static final ResourceLocation FIRST_PHASE = PuzzlesLib.id("first");
    /**
     * A phase to be used directly before the default phase, equivalent to EventPriority#HIGH on Forge.
     */
    public static final ResourceLocation BEFORE_PHASE = PuzzlesLib.id("before");
    /**
     * Fabric's default event phase, equivalent to EventPriority#NORMAL on Forge.
     */
    public static final ResourceLocation DEFAULT_PHASE = Event.DEFAULT_PHASE;
    /**
     * A phase to be used directly after the default phase, equivalent to EventPriority#LOW on Forge.
     */
    public static final ResourceLocation AFTER_PHASE = PuzzlesLib.id("after");
    /**
     * A phase to be used as the very last phase, equivalent to EventPriority#LOWEST on Forge.
     */
    public static final ResourceLocation LAST_PHASE = PuzzlesLib.id("last");
}
