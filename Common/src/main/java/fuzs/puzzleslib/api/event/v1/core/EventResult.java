package fuzs.puzzleslib.api.event.v1.core;

import java.util.NoSuchElementException;

/**
 * A result that can be returned from an event, either allowing the event to continue processing or to cancel the implementation resulting in vanilla behavior being disrupted.
 * <p>Can be either used with a combination of {@link #PASS} and {@link #INTERRUPT}, or alternatively if three states are required with {@link #PASS}, {@link #ALLOW} and {@link #DENY}.
 * <p>For both purposes {@link #INTERRUPT} and {@link #ALLOW} can be used interchangeably.
 * <p>Very similar to vanilla's {@link net.minecraft.world.InteractionResult}.
 * <p>Implementation is also remotely similar to <a href="https://github.com/architectury/architectury-api/blob/1.19.3/common/src/main/java/dev/architectury/event/EventResult.java">EventResult.java</a> from Architectury API.
 */
public enum EventResult {
    /**
     * Allows the event to continue processing. It will be passed to both other event implementations and will not prevent vanilla behavior from running.
     */
    PASS(false, false),
    /**
     * Prevents the event from processing any further and disrupts vanilla behavior, equal to passing <code>true</code> to <code>net.minecraftforge.eventbus.api.Event#setCanceled</code> on Forge.
     * <p>Unprocessed callbacks will be skipped. There is no way of receiving an event after it has been cancelled by a previous callback,
     * as there is on Forge by setting <code>receiveCancelled</code> to <code>true</code> during event registration, since Fabric doesn't support such a behavior.
     */
    INTERRUPT(true, true),
    /**
     * Interrupts the event just like {@link #INTERRUPT}. Useful for event implementations that require three states for their cancellation result.
     * <p>Equivalent to <code>net.minecraftforge.eventbus.api.Result</code> on Forge.
     */
    ALLOW(true, true),
    /**
     * Interrupts the event just like {@link #INTERRUPT}. Useful for event implementations that require three states for their cancellation result.
     * <p>Equivalent to <code>net.minecraftforge.eventbus.api.Result</code> on Forge.
     */
    DENY(true, false);

    private final boolean interrupt;
    private final boolean value;

    EventResult(boolean interrupt, boolean value) {
        this.interrupt = interrupt;
        this.value = value;
    }

    /**
     * Returns the value for {@link #ALLOW} and {@link #DENY} states.
     * <p>Will throw an exception for a passing result.
     *
     * @return value for {@link #ALLOW} and {@link #DENY} states
     */
    public boolean getAsBoolean() {
        if (!this.interrupt) {
            throw new NoSuchElementException("No value present");
        }
        return this.value;
    }

    /**
     * @return does this result prevent the vanilla behavior from running
     */
    public boolean isInterrupt() {
        return this.interrupt;
    }

    /**
     * @return does this result allow for events to continue processing and for vanilla behavior to apply
     */
    public boolean isPass() {
        return !this.interrupt;
    }
}
