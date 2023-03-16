package fuzs.puzzleslib.client.core;

import net.minecraft.client.KeyMapping;

public class FabricClientAbstractions implements ClientAbstractions {

    @Override
    public boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
        return keyMapping.matches(keyCode, scanCode);
    }
}
