package fuzs.puzzleslib.neoforge.impl.client.screen;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import fuzs.puzzleslib.api.client.screen.v2.KeyActivationContext;
import fuzs.puzzleslib.api.client.screen.v2.KeyMappingActivationHelper;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

public final class NeoForgeKeyMappingActivationHelper implements KeyMappingActivationHelper {
    public static final BiMap<KeyActivationContext, IKeyConflictContext> KEY_CONTEXTS = ImmutableBiMap.of(
            KeyActivationContext.UNIVERSAL, KeyConflictContext.UNIVERSAL,
            KeyActivationContext.GAME, KeyConflictContext.IN_GAME,
            KeyActivationContext.SCREEN, KeyConflictContext.GUI
    );

    @Override
    public KeyActivationContext getKeyActivationContext(KeyMapping keyMapping) {
        return KEY_CONTEXTS.inverse().getOrDefault(keyMapping.getKeyConflictContext(), KeyActivationContext.UNIVERSAL);
    }
}
