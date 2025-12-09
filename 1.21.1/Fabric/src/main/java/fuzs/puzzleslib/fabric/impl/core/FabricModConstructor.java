package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.core.v2.context.BiomeModificationsContext;
import fuzs.puzzleslib.fabric.impl.core.context.*;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.item.CopyComponentsRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Set;
import java.util.function.Supplier;

public final class FabricModConstructor implements ModConstructorImpl<ModConstructor> {

    @Override
    public void construct(String modId, ModConstructor modConstructor, Set<ContentRegistrationFlags> contentRegistrationFlags) {
        modConstructor.onConstructMod();
        modConstructor.onRegisterCreativeModeTabs(new CreativeModeTabContextFabricImpl());
        modConstructor.onBuildCreativeModeTabContents(new CreativeTabContentsContextFabricImpl());
        if (contentRegistrationFlags.contains(ContentRegistrationFlags.COPY_RECIPES)) {
            CopyComponentsRecipe.registerSerializers((String path, Supplier<RecipeSerializer<?>> supplier) -> {
                Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                        ResourceLocationHelper.fromNamespaceAndPath(modId, path),
                        supplier.get());
            });
        }

        modConstructor.onCommonSetup();
        modConstructor.onRegisterPayloadTypes(FabricProxy.get().createPayloadTypesContext(modId));
        modConstructor.onRegisterEntityAttributes(new EntityAttributesContextFabricImpl());
        modConstructor.onEntityAttributeCreation(new EntityAttributesCreateContextFabricImpl());
        modConstructor.onEntityAttributeModification(new EntityAttributesModifyContextFabricImpl());
        modConstructor.onRegisterSpawnPlacements(new SpawnPlacementsContextFabricImpl());
        modConstructor.onRegisterGameplayContent(new GameplayContentContextFabricImpl());
        modConstructor.onRegisterFuelBurnTimes(new FuelBurnTimesContextFabricImpl());
        modConstructor.onRegisterFlammableBlocks(new FlammableBlocksContextFabricImpl());
        modConstructor.onRegisterCompostableBlocks(new CompostableBlocksContextFabricImpl());
        modConstructor.onRegisterBlockInteractions(new BlockInteractionsContextFabricImpl());
        modConstructor.onRegisterBiomeModifications((BiomeModificationsContext) new BiomeModificationsContextFabricImpl(
                modId));
        modConstructor.onRegisterBiomeModifications((fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext) new BiomeModificationsContextFabricImpl(
                modId));
        modConstructor.onAddDataPackFinders(new DataPackSourcesContextFabricImpl());
        modConstructor.onRegisterGameRegistries(new GameRegistriesContextFabricImpl());
        modConstructor.onRegisterDataPackRegistries(new DataPackRegistriesContextFabricImpl());
        modConstructor.onRegisterVillagerTrades(new VillagerTradesContextFabricImpl());
    }
}
