package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.screen.v2.KeyMappingActivationHelper;
import fuzs.puzzleslib.impl.client.screen.ForgeKeyMappingActivationHelper;
import net.minecraft.client.KeyMapping;

import java.util.Objects;
import java.util.function.Consumer;

public record KeyMappingsContextForgeImpl(Consumer<KeyMapping> consumer) implements KeyMappingsContext {

    @Override
    public void registerKeyMapping(KeyMapping keyMapping, KeyMappingActivationHelper.KeyActivationContext keyActivationContext) {
        Objects.requireNonNull(keyMapping, "key mapping is null");
        Objects.requireNonNull(keyActivationContext, "activation context is null");
        this.consumer.accept(keyMapping);
        keyMapping.setKeyConflictContext(ForgeKeyMappingActivationHelper.KEY_CONTEXTS.get(keyActivationContext));
    }
}
