package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.screen.v2.KeyMappingActivationHelper;
import fuzs.puzzleslib.impl.client.screen.ForgeKeyMappingActivationHelper;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

import java.util.Objects;

public record KeyMappingsContextForgeImpl() implements KeyMappingsContext {

    @Override
    public void registerKeyMapping(KeyMapping keyMapping, KeyMappingActivationHelper.KeyActivationContext keyActivationContext) {
        Objects.requireNonNull(keyMapping, "key mapping is null");
        Objects.requireNonNull(keyActivationContext, "activation context is null");
        ClientRegistry.registerKeyBinding(keyMapping);
        keyMapping.setKeyConflictContext(ForgeKeyMappingActivationHelper.KEY_CONTEXTS.get(keyActivationContext));
    }
}
