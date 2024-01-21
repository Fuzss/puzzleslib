package fuzs.puzzleslib.forge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.LevelCapabilityKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ForgeLevelCapabilityKey<C extends CapabilityComponent<Level>> extends ForgeCapabilityKey<Level, C> implements LevelCapabilityKey<C> {

    public ForgeLevelCapabilityKey(ResourceLocation identifier, CapabilityTokenFactory<Level, C> tokenFactory, Predicate<Object> filter, Supplier<C> capabilityFactory) {
        super(identifier, tokenFactory, filter, capabilityFactory);
    }

    @Override
    public void setChanged(C capabilityComponent) {
        if (this.fallback != capabilityComponent) {
            LevelCapabilityKey.super.setChanged(capabilityComponent);
        }
    }
}
