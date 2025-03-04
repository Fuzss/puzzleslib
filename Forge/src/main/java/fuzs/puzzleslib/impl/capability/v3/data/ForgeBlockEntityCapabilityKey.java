package fuzs.puzzleslib.impl.capability.v3.data;

import fuzs.puzzleslib.api.capability.v3.data.BlockEntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ForgeBlockEntityCapabilityKey<T extends BlockEntity, C extends CapabilityComponent<T>> extends ForgeCapabilityKey<T, C> implements BlockEntityCapabilityKey<T, C> {

    public ForgeBlockEntityCapabilityKey(ResourceLocation identifier, Capability<C> capability, Predicate<Object> filter, Supplier<C> capabilityFactory) {
        super(identifier, capability, filter, capabilityFactory);
    }

    @Override
    public void setChanged(C capabilityComponent, @Nullable PlayerSet playerSet) {
        if (this.fallback != capabilityComponent) {
            BlockEntityCapabilityKey.super.setChanged(capabilityComponent, playerSet);
        }
    }
}
