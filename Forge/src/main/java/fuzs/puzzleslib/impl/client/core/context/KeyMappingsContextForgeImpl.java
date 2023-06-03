package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

import java.util.Objects;

public record KeyMappingsContextForgeImpl() implements KeyMappingsContext {

    @Override
    public void registerKeyMapping(KeyMapping... keyMappings) {
        Objects.requireNonNull(keyMappings, "key mappings is null");
        Preconditions.checkPositionIndex(1, keyMappings.length, "key mappings is empty");
        for (KeyMapping keyMapping : keyMappings) {
            Objects.requireNonNull(keyMapping, "key mapping is null");
            ClientRegistry.registerKeyBinding(keyMapping);
        }
    }
}
