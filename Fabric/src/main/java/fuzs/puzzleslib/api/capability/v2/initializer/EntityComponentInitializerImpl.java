package fuzs.puzzleslib.api.capability.v2.initializer;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import fuzs.puzzleslib.impl.capability.FabricCapabilityController;
import net.minecraft.world.entity.Entity;

public class EntityComponentInitializerImpl implements EntityComponentInitializer {

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        FabricCapabilityController.registerComponentFactories(Entity.class, registry);
    }
}
