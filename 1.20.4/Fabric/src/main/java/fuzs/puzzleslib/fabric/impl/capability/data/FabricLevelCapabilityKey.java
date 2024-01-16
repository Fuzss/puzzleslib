package fuzs.puzzleslib.fabric.impl.capability.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.LevelCapabilityKey;
import net.minecraft.world.level.Level;

public class FabricLevelCapabilityKey<C extends CapabilityComponent<Level>> extends FabricCapabilityKey<Level, C> implements LevelCapabilityKey<C> {

    public FabricLevelCapabilityKey(ComponentKey<ComponentAdapter<Level, C>> capability) {
        super(capability);
    }
}
