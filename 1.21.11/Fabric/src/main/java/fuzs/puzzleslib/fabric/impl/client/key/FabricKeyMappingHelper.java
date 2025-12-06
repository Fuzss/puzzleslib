package fuzs.puzzleslib.fabric.impl.client.key;

import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public final class FabricKeyMappingHelper implements KeyMappingHelper {

    @Override
    public KeyActivationContext getKeyActivationContext(KeyMapping keyMapping) {
        return ((ActivationContextKeyMapping) keyMapping).puzzleslib$getKeyActivationContext();
    }
}
