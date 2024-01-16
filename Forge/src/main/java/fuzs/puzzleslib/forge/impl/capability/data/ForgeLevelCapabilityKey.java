package fuzs.puzzleslib.forge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.LevelCapabilityKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ForgeLevelCapabilityKey<C extends CapabilityComponent<Level>> extends ForgeCapabilityKey<Level, C> implements LevelCapabilityKey<C> {

    public ForgeLevelCapabilityKey(ResourceLocation identifier, CapabilityTokenFactory<Level, C> tokenFactory) {
        super(identifier, tokenFactory);
    }
}
