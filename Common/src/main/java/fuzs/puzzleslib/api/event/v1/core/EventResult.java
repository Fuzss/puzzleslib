package fuzs.puzzleslib.api.event.v1.core;

import org.jspecify.annotations.Nullable;

import java.util.NoSuchElementException;

/**
 * A result that can be returned from an event, either allowing the event to continue processing or to cancel the
 * implementation resulting in vanilla behavior being disrupted.
 * <p>
 * Can be either used with a combination of {@link #PASS} and {@link #INTERRUPT}, or alternatively if three states are
 * required with {@link #PASS}, {@link #ALLOW} and {@link #DENY}.
 * <p>
 * For both purposes {@link #INTERRUPT} and {@link #ALLOW} can be used interchangeably.
 * <p>
 * Very similar to vanilla's {@link net.minecraft.world.InteractionResult}.
 * <p>
 * Implementation is also remotely similar to <a
 * href="https://github.com/architectury/architectury-api/blob/1.19.3/common/src/main/java/dev/architectury/event/EventResult.java">EventResult.java</a>
 * from Architectury API.
 */
public enum EventResult {
    /**
     * Allows the event to continue processing. It will be passed to both other event implementations and will not
     * prevent vanilla behavior from running.
     */
    PASS(false, null),
    /**
     * Prevents the event from processing any further and disrupts vanilla behavior, similar to cancelling an event on
     * Forge.
     * <p>
     * Unprocessed callbacks will be skipped if cancelled.
     */
    INTERRUPT(true, Boolean.FALSE),
    /**
     * Interrupts the event just like {@link #INTERRUPT}. Useful for event implementations that require three states for
     * their cancellation result.
     */
    ALLOW(true, Boolean.TRUE),
    /**
     * Interrupts the event just like {@link #INTERRUPT}. Useful for event implementations that require three states for
     * their cancellation result.
     */
    DENY(true, Boolean.FALSE);

    private final boolean interrupt;
    @Nullable
    private final Boolean value;

    EventResult(boolean interrupt, @Nullable Boolean value) {
        this.interrupt = interrupt;
        this.value = value;
    }

    /**
     * Returns the value for {@link #ALLOW} and {@link #DENY} states.
     * <p>
     * Will throw an exception for a passing result.
     *
     * @return value for {@link #ALLOW} and {@link #DENY} states
     */
    public boolean getAsBoolean() {
        if (this.value == null) {
            throw new NoSuchElementException("No value present");
        } else {
            return this.value;
        }
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
