package fuzs.puzzleslib.forge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.BlockEntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ForgeBlockEntityCapabilityKey<T extends BlockEntity, C extends CapabilityComponent<T>> extends ForgeCapabilityKey<T, C> implements BlockEntityCapabilityKey<T, C> {

    public ForgeBlockEntityCapabilityKey(ResourceLocation identifier, Capability<C> capability, Predicate<Object> filter, Supplier<C> capabilityFactory) {
        super(identifier, capability, filter, capabilityFactory);
    }

    @Override
    public void setChanged(C capabilityComponent) {
        if (this.fallback != capabilityComponent) {
            BlockEntityCapabilityKey.super.setChanged(capabilityComponent);
        }
    }
}
