package fuzs.puzzleslib.api.capability.v3.data;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.Objects;

/**
 * Common capability implementation with read and write methods for serialization.
 *
 * @param <T> capability provider type
 */
public abstract class CapabilityComponent<T> {
    private boolean initialized;
    private CapabilityKey<T, CapabilityComponent<T>> capabilityKey;
    private T holder;

    /**
     * Getter for the holder of this component.
     *
     * @return the component holder
     */
    protected final T getHolder() {
        Objects.requireNonNull(this.holder, "holder is null");
        return this.holder;
    }

    @ApiStatus.Internal
    public final void initialize(CapabilityKey<T, CapabilityComponent<T>> capabilityKey, T holder) {
        if (!this.initialized) {
            this.initialized = true;
            Objects.requireNonNull(capabilityKey, "capability key is null");
            this.capabilityKey = capabilityKey;
            Objects.requireNonNull(holder, "capability holder is null");
            this.holder = holder;
        } else {
            throw new IllegalStateException("Capability component '%s' already initialized".formatted(capabilityKey.identifier()));
        }
    }

    /**
     * Serialize the component to a {@link CompoundTag}.
     *
     * @param tag tag to write to
     */
    @ApiStatus.OverrideOnly
    public void write(CompoundTag tag) {

    }

    /**
     * Deserialize the component from a {@link CompoundTag}.
     *
     * @param tag tag to read from
     */
    @ApiStatus.OverrideOnly
    public void read(CompoundTag tag) {
        
    }

    /**
     * Serialize the component to a {@link CompoundTag}.
     *
     * @return the capability serialized to {@link CompoundTag}
     */
    @ApiStatus.NonExtendable
    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        this.write(tag);
        return tag;
    }

    /**
     * To be called when capability data changed and requires serializing and / or syncing.
     *
     * <p>Should basically be called in all setters after the new value has been set.
     */
    @MustBeInvokedByOverriders
    public void setChanged() {
        this.capabilityKey.setChanged(this);
    }
}
