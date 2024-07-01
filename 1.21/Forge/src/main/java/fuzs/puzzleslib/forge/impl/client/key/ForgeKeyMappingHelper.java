package fuzs.puzzleslib.forge.impl.client.key;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;

public final class ForgeKeyMappingHelper implements KeyMappingHelper {
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
