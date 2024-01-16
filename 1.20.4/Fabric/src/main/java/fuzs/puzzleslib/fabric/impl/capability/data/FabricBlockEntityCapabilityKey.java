package fuzs.puzzleslib.fabric.impl.capability.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import fuzs.puzzleslib.api.capability.v3.data.BlockEntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FabricBlockEntityCapabilityKey<T extends BlockEntity, C extends CapabilityComponent<T>> extends FabricCapabilityKey<T, C> implements BlockEntityCapabilityKey<T, C> {

    public FabricBlockEntityCapabilityKey(ComponentKey<ComponentAdapter<T, C>> capability) {
        super(capability);
    }
}
