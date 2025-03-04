package fuzs.puzzleslib.impl.capability.v3.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
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
        // FIXME we don't have a registry access available here
        return this.component.toCompoundTag(RegistryAccess.EMPTY);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // FIXME we don't have a registry access available here
        this.component.read(nbt, RegistryAccess.EMPTY);
    }
}
