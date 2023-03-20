package fuzs.puzzleslib.api.core.v1;

/**
 * A utility interface for completing builders that might have been created before they can be safely applied.
 */
public interface Buildable {

    /**
     * Executes this builder instance.
     * <p>Does not have a result, the builder itself must be implemented on the same instance it is supposed to build. Just hide this fact via polymorphism.
     */
    void build();
}
