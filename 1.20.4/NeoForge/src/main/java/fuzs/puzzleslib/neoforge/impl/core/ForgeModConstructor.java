package fuzs.puzzleslib.neoforge.impl.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.impl.item.CopyTagRecipe;
import fuzs.puzzleslib.neoforge.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.core.context.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public final class ForgeModConstructor {

    private ForgeModConstructor() {

    }

    public static void construct(ModConstructor constructor, String modId, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        ModContainerHelper.getOptionalModEventBus(modId).ifPresent(modEventBus -> {
            Multimap<BiomeLoadingPhase, BiomeLoadingHandler.BiomeModification> biomeModifications = HashMultimap.create();
            registerContent(constructor, modId, modEventBus, biomeModifications, flagsToHandle);
            registerModHandlers(constructor, modId, modEventBus, biomeModifications, availableFlags, flagsToHandle);
            registerHandlers(constructor, modId);
            constructor.onConstructMod();
        });
    }

    private static void registerContent(ModConstructor constructor, String modId, IEventBus modEventBus, Multimap<BiomeLoadingPhase, BiomeLoadingHandler.BiomeModification> biomeModifications, Set<ContentRegistrationFlags> flagsToHandle) {
        constructor.onRegisterCreativeModeTabs(new CreativeModeTabContextForgeImpl(modEventBus));
        if (flagsToHandle.contains(ContentRegistrationFlags.BIOME_MODIFICATIONS)) {
            BiomeLoadingHandler.register(modId, modEventBus, biomeModifications);
        }
        if (flagsToHandle.contains(ContentRegistrationFlags.COPY_TAG_RECIPES)) {
            DeferredRegister<RecipeSerializer<?>> deferredRegister = DeferredRegister.create(Registries.RECIPE_SERIALIZER, modId);
            deferredRegister.register(modEventBus);
            CopyTagRecipe.registerSerializers(deferredRegister::register);
        }
    }

    private static void registerModHandlers(ModConstructor constructor, String modId, IEventBus eventBus, Multimap<BiomeLoadingPhase, BiomeLoadingHandler.BiomeModification> biomeModifications, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        eventBus.addListener((final FMLCommonSetupEvent evt) -> {
            evt.enqueueWork(() -> {
                constructor.onCommonSetup();
                constructor.onRegisterFuelBurnTimes(new FuelBurnTimesContextForgeImpl());
                constructor.onRegisterBiomeModifications(new BiomeModificationsContextForgeImpl(biomeModifications, availableFlags));
                constructor.onRegisterFlammableBlocks(new FlammableBlocksContextForgeImpl());
                constructor.onRegisterBlockInteractions(new BlockInteractionsContextForgeImpl());
            });
        });
        eventBus.addListener((final SpawnPlacementRegisterEvent evt) -> {
            constructor.onRegisterSpawnPlacements(new SpawnPlacementsContextForgeImpl(evt));
        });
        eventBus.addListener((final EntityAttributeCreationEvent evt) -> {
            constructor.onEntityAttributeCreation(new EntityAttributesCreateContextForgeImpl(evt::put));
        });
        eventBus.addListener((final EntityAttributeModificationEvent evt) -> {
            constructor.onEntityAttributeModification(new EntityAttributesModifyContextForgeImpl(evt::add));
        });
        eventBus.addListener((final BuildCreativeModeTabContentsEvent evt) -> {
            constructor.onBuildCreativeModeTabContents(new BuildCreativeModeTabContentsContextForgeImpl(evt.getTabKey(), evt.getParameters(), evt));
        });
        eventBus.addListener((final AddPackFindersEvent evt) -> {
            if (evt.getPackType() == PackType.SERVER_DATA) {
                constructor.onAddDataPackFinders(new DataPackSourcesContextForgeImpl(evt::addRepositorySource));
                if (flagsToHandle.contains(ContentRegistrationFlags.BIOME_MODIFICATIONS)) {
                    evt.addRepositorySource(BiomeLoadingHandler.buildPack(modId));
                }
            }
        });
    }

    private static void registerHandlers(ModConstructor constructor, String modId) {
        NeoForge.EVENT_BUS.addListener((AddReloadListenerEvent evt) -> {
            constructor.onRegisterDataPackReloadListeners(new AddReloadListenersContextForgeImpl(modId, evt::addListener));
        });
    }
}
