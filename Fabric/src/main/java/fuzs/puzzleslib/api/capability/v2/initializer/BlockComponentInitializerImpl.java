package fuzs.puzzleslib.api.capability.v2.initializer;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import fuzs.puzzleslib.impl.capability.FabricCapabilityController;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockComponentInitializerImpl implements BlockComponentInitializer {

    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
        FabricCapabilityController.registerComponentFactories(BlockEntity.class, registry);
    }
}
