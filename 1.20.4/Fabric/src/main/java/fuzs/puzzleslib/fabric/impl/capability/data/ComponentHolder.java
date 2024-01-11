package fuzs.puzzleslib.fabric.impl.capability.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;
import net.minecraft.nbt.CompoundTag;

/**
 * a wrapper for {@link FabricCapabilityKey} for implementing
 * this would be much nicer using a parametrized type instead of raw {@link CapabilityComponent},
 * but this is unfortunately not possible due to the way {@link ComponentKey} is created using the factory
 * therefore we need to perform a manual cast when retrieving the wrapped {@link #component}
 */
public record ComponentHolder(CapabilityComponent component) implements ComponentV3 {

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.component.read(tag);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        this.component.write(tag);
    }
}
