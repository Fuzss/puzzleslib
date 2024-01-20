package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.fabric.impl.client.key.FabricKeyMappingActivationHelper;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

import java.util.Objects;

public final class KeyMappingsContextFabricImpl implements KeyMappingsContext {

    @Override
    public void registerKeyMapping(KeyMapping keyMapping, KeyActivationContext keyActivationContext) {
        Objects.requireNonNull(keyMapping, "key mapping is null");
        Objects.requireNonNull(keyActivationContext, "activation context is null");
        KeyBindingHelper.registerKeyBinding(keyMapping);
        FabricKeyMappingActivationHelper.setKeyActivationContext(keyMapping, keyActivationContext);
    }
}
