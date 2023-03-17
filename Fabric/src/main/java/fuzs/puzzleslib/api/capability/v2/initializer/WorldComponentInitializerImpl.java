package fuzs.puzzleslib.api.capability.v2.initializer;

import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import fuzs.puzzleslib.impl.capability.FabricCapabilityController;
import net.minecraft.world.level.Level;

public class WorldComponentInitializerImpl implements WorldComponentInitializer {

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        FabricCapabilityController.registerComponentFactories(Level.class, registry);
    }
}
