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
    private Runnable callback;
    private T holder;

    protected final T getHolder() {
        Objects.requireNonNull(this.holder, "holder is null");
        return this.holder;
    }

    @ApiStatus.Internal
    public final void initialize(CapabilityKey<T, CapabilityComponent<T>> capabilityKey, T holder) {
        if (!this.initialized) {
            this.initialized = true;
            this.callback = () -> {
                capabilityKey.setChanged(this);
            };
            this.holder = holder;
        } else {
            throw new IllegalStateException("Capability component '%s' already initialized".formatted(capabilityKey.identifier()));
        }
    }

    /**
     * @param tag tag to write to
     */
    @ApiStatus.OverrideOnly
    public void write(CompoundTag tag) {

    }

    /**
     * @param tag tag to read from
     */
    @ApiStatus.OverrideOnly
    public void read(CompoundTag tag) {
        
    }

    /**
     * @return this capability serialized to {@link CompoundTag}
     */
    @ApiStatus.Internal
    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        this.write(tag);
        return tag;
    }

    /**
     * To be called when capability data changed and requires serializing and / or syncing.
     */
    @MustBeInvokedByOverriders
    public void setChanged() {
        this.callback.run();
    }
}
