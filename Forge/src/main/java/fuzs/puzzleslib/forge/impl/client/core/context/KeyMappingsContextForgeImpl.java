package fuzs.puzzleslib.forge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.forge.impl.client.key.ForgeKeyMappingActivationHelper;
import net.minecraft.client.KeyMapping;

import java.util.Objects;
import java.util.function.Consumer;

public record KeyMappingsContextForgeImpl(Consumer<KeyMapping> consumer) implements KeyMappingsContext {

    @Override
    public void registerKeyMapping(KeyMapping keyMapping, KeyActivationContext keyActivationContext) {
        Objects.requireNonNull(keyMapping, "key mapping is null");
        Objects.requireNonNull(keyActivationContext, "activation context is null");
        this.consumer.accept(keyMapping);
        keyMapping.setKeyConflictContext(ForgeKeyMappingActivationHelper.KEY_CONTEXTS.get(keyActivationContext));
    }
}
