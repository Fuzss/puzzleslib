package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.KeyMappingsContext;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

import java.util.Objects;

public final class KeyMappingsContextFabricImpl implements KeyMappingsContext {

    @Override
    public void registerKeyMappings(KeyMapping... keyMappings) {
        Objects.requireNonNull(keyMappings, "key mappings is null");
        for (KeyMapping keyMapping : keyMappings) {
            Objects.requireNonNull(keyMapping, "key mapping is null");
            KeyBindingHelper.registerKeyBinding(keyMapping);
        }
    }
}
