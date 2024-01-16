package fuzs.puzzleslib.forge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.BlockEntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ForgeBlockEntityCapabilityKey<T extends BlockEntity, C extends CapabilityComponent<T>> extends ForgeCapabilityKey<T, C> implements BlockEntityCapabilityKey<T, C> {

    public ForgeBlockEntityCapabilityKey(ResourceLocation identifier, CapabilityTokenFactory<T, C> tokenFactory) {
        super(identifier, tokenFactory);
    }
}
