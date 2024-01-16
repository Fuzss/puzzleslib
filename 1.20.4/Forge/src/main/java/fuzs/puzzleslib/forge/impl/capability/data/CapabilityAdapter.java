package fuzs.puzzleslib.forge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record CapabilityAdapter<T, C extends CapabilityComponent<T>>(Capability<C> capability, C component) implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    @NotNull
    @Override
    public <S> LazyOptional<S> getCapability(@NotNull Capability<S> capability, @Nullable Direction facing) {
        return capability == this.capability  ? LazyOptional.of(() -> this.component).cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.component.toCompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.component.read(nbt);
    }
}
