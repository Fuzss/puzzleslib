package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.impl.core.context.*;
import net.minecraft.world.entity.SpawnPlacements;

public final class FabricModConstructor {

    private FabricModConstructor() {

    }

    public static void construct(ModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        constructor.onConstructMod();
        constructor.onCommonSetup(Runnable::run);
        constructor.onRegisterSpawnPlacements(SpawnPlacements::register);
        constructor.onEntityAttributeCreation(new EntityAttributesCreateContextFabricImpl());
        constructor.onEntityAttributeModification(new EntityAttributesModifyContextFabricImpl());
        constructor.onRegisterFuelBurnTimes(new FuelBurnTimesContextFabricImpl());
        constructor.onRegisterBiomeModifications(new BiomeModificationsContextFabricImpl(modId));
        constructor.onRegisterFlammableBlocks(new FlammableBlocksContextFabricImpl());
        constructor.onRegisterCreativeModeTabs(new CreativeModeTabContextFabricImpl());
        constructor.onAddDataPackFinders(new DataPackSourcesContextFabricImpl());
    }
}
