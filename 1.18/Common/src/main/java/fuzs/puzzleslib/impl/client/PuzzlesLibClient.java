package fuzs.puzzleslib.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.screen.v2.DeferredTooltipRendering;

public class PuzzlesLibClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        DeferredTooltipRendering.registerHandlers();
    }
}
