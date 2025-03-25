package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.fabric.impl.core.context.*;

public final class FabricModConstructor {

    private FabricModConstructor() {
        // NO-OP
    }

    public static void construct(ModConstructor modConstructor, String modId) {
        modConstructor.onConstructMod();
        modConstructor.onCommonSetup();
        modConstructor.onRegisterEntityAttributes(new EntityAttributesContextFabricImpl());
        modConstructor.onRegisterSpawnPlacements(new SpawnPlacementsContextFabricImpl());
        modConstructor.onRegisterGameplayContent(new GameplayContentContextFabricImpl());
        modConstructor.onRegisterBiomeModifications(new BiomeModificationsContextFabricImpl(modId));
        modConstructor.onAddDataPackFinders(new DataPackSourcesContextFabricImpl());
        modConstructor.onRegisterGameRegistriesContext(new GameRegistriesContextFabricImpl());
        modConstructor.onRegisterDataPackRegistriesContext(new DataPackRegistriesContextFabricImpl());
    }
}
