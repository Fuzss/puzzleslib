package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.fabric.impl.core.context.*;

public final class FabricModConstructor {

    private FabricModConstructor() {
        // NO-OP
    }

    public static void construct(ModConstructor constructor, String modId) {
        constructor.onConstructMod();
        constructor.onCommonSetup();
        constructor.onEntityAttributeCreation(new EntityAttributesCreateContextFabricImpl());
        constructor.onEntityAttributeModification(new EntityAttributesModifyContextFabricImpl());
        constructor.onRegisterSpawnPlacements(new SpawnPlacementsContextFabricImpl());
        constructor.onRegisterFlammableBlocks(new FlammableBlocksContextFabricImpl());
        constructor.onRegisterCompostableBlocks(new CompostableBlocksContextFabricImpl());
        constructor.onRegisterBlockInteractions(new BlockInteractionsContextFabricImpl());
        constructor.onRegisterBiomeModifications(new BiomeModificationsContextFabricImpl(modId));
        constructor.onAddDataPackFinders(new DataPackSourcesContextFabricImpl());
        constructor.onGameRegistriesContext(new GameRegistriesContextFabricImpl());
        constructor.onDataPackRegistriesContext(new DataPackRegistriesContextFabricImpl());
    }
}
