package fuzs.puzzleslib.impl.core;

/**
 * A utility interface for completing certain types of instances, so they can be made read-only.
 */
public interface Freezable {

    /**
     * Makes the instance read-only.
     */
    void freeze();

    /**
     * @return is this instance read-only
     */
    boolean isFrozen();

    /**
     * Checks that this instance is still writable, meaning {@link #isFrozen()} returns {@code false}.
     * <p>
     * Otherwise, throws an exception.
     */
    default void isWritableOrThrow() {
        if (this.isFrozen()) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " is already frozen");
        }
    }

    /**
     * Checks that this instance is read-only, meaning {@link #isFrozen()} returns {@code true}.
     * <p>
     * Otherwise, throws an exception.
     */
    default void isFrozenOrThrow() {
        if (!this.isFrozen()) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " is not yet frozen");
        }
    }
}
