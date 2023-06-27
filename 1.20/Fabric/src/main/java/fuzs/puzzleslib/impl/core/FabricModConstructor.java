package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.item.v2.LegacySmithingTransformRecipe;
import fuzs.puzzleslib.impl.core.context.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.apache.commons.lang3.ArrayUtils;

public final class FabricModConstructor {

    private FabricModConstructor() {

    }

    public static void construct(ModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        registerContent(modId, contentRegistrations);
        registerHandlers(constructor, modId);
    }

    private static void registerContent(String modId, ContentRegistrationFlags[] contentRegistrations) {
        if (ArrayUtils.contains(contentRegistrations, ContentRegistrationFlags.LEGACY_SMITHING)) {
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(modId, LegacySmithingTransformRecipe.RECIPE_SERIALIZER_ID), new LegacySmithingTransformRecipe.Serializer());
        }
    }

    private static void registerHandlers(ModConstructor constructor, String modId) {
        constructor.onConstructMod();
        constructor.onRegisterCreativeModeTabs(new CreativeModeTabContextFabricImpl());
        constructor.onCommonSetup(Runnable::run);
        constructor.onRegisterSpawnPlacements(new SpawnPlacementsContextFabricImpl());
        constructor.onEntityAttributeCreation(new EntityAttributesCreateContextFabricImpl());
        constructor.onEntityAttributeModification(new EntityAttributesModifyContextFabricImpl());
        constructor.onRegisterFuelBurnTimes(new FuelBurnTimesContextFabricImpl());
        constructor.onRegisterBiomeModifications(new BiomeModificationsContextFabricImpl(modId));
        constructor.onRegisterFlammableBlocks(new FlammableBlocksContextFabricImpl());
        constructor.onBuildCreativeModeTabContents(new BuildCreativeModeTabContentsContextFabricImpl());
        constructor.onAddDataPackFinders(new DataPackSourcesContextFabricImpl());
        constructor.onRegisterDataPackReloadListeners(new AddReloadListenersContextFabricImpl(PackType.SERVER_DATA, modId));
    }
}
