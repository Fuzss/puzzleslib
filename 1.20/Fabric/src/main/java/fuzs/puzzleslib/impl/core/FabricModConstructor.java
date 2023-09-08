package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.item.v2.LegacySmithingTransformRecipe;
import fuzs.puzzleslib.impl.core.context.*;
import fuzs.puzzleslib.impl.item.CopyTagRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Set;

public final class FabricModConstructor {

    private FabricModConstructor() {

    }

    public static void construct(ModConstructor constructor, String modId, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        registerContent(modId, flagsToHandle);
        registerHandlers(constructor, modId, availableFlags);
    }

    private static void registerContent(String modId, Set<ContentRegistrationFlags> flagsToHandle) {
        if (flagsToHandle.contains(ContentRegistrationFlags.LEGACY_SMITHING)) {
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(modId, LegacySmithingTransformRecipe.RECIPE_SERIALIZER_ID), new LegacySmithingTransformRecipe.Serializer());
        }
        if (flagsToHandle.contains(ContentRegistrationFlags.COPY_TAG_RECIPES)) {
            CopyTagRecipe.registerSerializers((s, recipeSerializerSupplier) -> {
                Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(modId, s), recipeSerializerSupplier.get());
            });
        }
    }

    private static void registerHandlers(ModConstructor constructor, String modId, Set<ContentRegistrationFlags> availableFlags) {
        constructor.onConstructMod();
        constructor.onRegisterCreativeModeTabs(new CreativeModeTabContextFabricImpl());
        constructor.onBuildCreativeModeTabContents(new BuildCreativeModeTabContentsContextFabricImpl());
        constructor.onCommonSetup();
        constructor.onEntityAttributeCreation(new EntityAttributesCreateContextFabricImpl());
        constructor.onEntityAttributeModification(new EntityAttributesModifyContextFabricImpl());
        constructor.onRegisterSpawnPlacements(new SpawnPlacementsContextFabricImpl());
        constructor.onRegisterFuelBurnTimes(new FuelBurnTimesContextFabricImpl());
        constructor.onRegisterFlammableBlocks(new FlammableBlocksContextFabricImpl());
        constructor.onRegisterBiomeModifications(new BiomeModificationsContextFabricImpl(modId, availableFlags));
        constructor.onAddDataPackFinders(new DataPackSourcesContextFabricImpl());
        constructor.onRegisterDataPackReloadListeners(new AddReloadListenersContextFabricImpl(PackType.SERVER_DATA, modId));
    }
}
