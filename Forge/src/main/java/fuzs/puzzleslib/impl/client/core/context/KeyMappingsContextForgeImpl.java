package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import net.minecraft.client.KeyMapping;

import java.util.Objects;
import java.util.function.Consumer;

public record KeyMappingsContextForgeImpl(Consumer<KeyMapping> consumer) implements KeyMappingsContext {

    @Override
    public void registerKeyMapping(KeyMapping... keyMappings) {
        Objects.requireNonNull(keyMappings, "key mappings is null");
        Preconditions.checkPositionIndex(0, keyMappings.length, "key mappings is empty");
        for (KeyMapping keyMapping : keyMappings) {
            Objects.requireNonNull(keyMapping, "key mapping is null");
            this.consumer.accept(keyMapping);
        }
    }
}
