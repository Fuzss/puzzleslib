package fuzs.puzzleslib.capability.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * dispatcher for this serializable capability
 * @param <T> capability class
 */
public class CapabilityHolder<T extends CapabilityComponent> implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    /**
     * capability wrapper for object
     */
    private final Capability<T> capability;
    /**
     * capability object
     */
    private final T storage;

    /**
     * @param storage object
     * @param capability wrapper
     */
    public CapabilityHolder(Capability<T> capability, T storage) {
        this.capability = capability;
        this.storage = storage;
    }

    @Nonnull
    @Override
    public <S> LazyOptional<S> getCapability(@Nonnull Capability<S> capability, @Nullable Direction facing) {
        return capability == this.capability  ? LazyOptional.of(() -> this.storage).cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        this.storage.write(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.storage.read(nbt);
    }
}
