package fuzs.puzzleslib.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import net.minecraft.client.KeyMapping;

public final class FabricClientAbstractions implements ClientAbstractions {

    @Override
    public boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
        return keyMapping.matches(keyCode, scanCode);
    }
}
