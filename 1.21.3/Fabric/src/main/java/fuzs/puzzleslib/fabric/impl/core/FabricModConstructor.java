package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.fabric.impl.core.context.*;
import fuzs.puzzleslib.impl.item.CustomTransmuteRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Set;
import java.util.function.Supplier;

public final class FabricModConstructor {

    private FabricModConstructor() {
        // NO-OP
    }

    public static void construct(ModConstructor constructor, String modId, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        constructor.onConstructMod();
        registerContent(modId, flagsToHandle);
        registerLoadingHandlers(constructor, modId, availableFlags);
    }

    private static void registerContent(String modId, Set<ContentRegistrationFlags> flagsToHandle) {
        if (flagsToHandle.contains(ContentRegistrationFlags.CRAFTING_TRANSMUTE)) {
            CustomTransmuteRecipe.registerSerializers((String s, Supplier<RecipeSerializer<?>> supplier) -> {
                Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                        ResourceLocationHelper.fromNamespaceAndPath(modId, s),
                        supplier.get());
            });
        }
    }

    private static void registerLoadingHandlers(ModConstructor constructor, String modId, Set<ContentRegistrationFlags> availableFlags) {
        constructor.onRegisterCreativeModeTabs(new CreativeModeTabContextFabricImpl());
        constructor.onBuildCreativeModeTabContents(new CreativeTabContentsContextFabricImpl());
        constructor.onCommonSetup();
        constructor.onEntityAttributeCreation(new EntityAttributesCreateContextFabricImpl());
        constructor.onEntityAttributeModification(new EntityAttributesModifyContextFabricImpl());
        constructor.onRegisterSpawnPlacements(new SpawnPlacementsContextFabricImpl());
        constructor.onRegisterFlammableBlocks(new FlammableBlocksContextFabricImpl());
        constructor.onRegisterCompostableBlocks(new CompostableBlocksContextFabricImpl());
        constructor.onRegisterBlockInteractions(new BlockInteractionsContextFabricImpl());
        constructor.onRegisterBiomeModifications(new BiomeModificationsContextFabricImpl(modId, availableFlags));
        constructor.onAddDataPackFinders(new DataPackSourcesContextFabricImpl());
        constructor.onDataPackRegistriesContext(new DataPackRegistriesContextFabricImpl());
    }
}
