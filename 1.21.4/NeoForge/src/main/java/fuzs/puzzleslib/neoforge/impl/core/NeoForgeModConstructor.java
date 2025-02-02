package fuzs.puzzleslib.neoforge.impl.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.impl.item.CustomTransmuteRecipe;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.core.context.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public final class NeoForgeModConstructor {

    private NeoForgeModConstructor() {
        // NO-OP
    }

    public static void construct(ModConstructor constructor, String modId, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            constructor.onConstructMod();
            Multimap<BiomeLoadingPhase, NeoForgeBiomeLoadingHandler.BiomeModification> biomeModifications = HashMultimap.create();
            registerContent(constructor, modId, eventBus, biomeModifications, flagsToHandle);
            registerLoadingHandlers(constructor, modId, eventBus, biomeModifications, availableFlags, flagsToHandle);
        });
    }

    private static void registerContent(ModConstructor constructor, String modId, IEventBus modEventBus, Multimap<BiomeLoadingPhase, NeoForgeBiomeLoadingHandler.BiomeModification> biomeModifications, Set<ContentRegistrationFlags> flagsToHandle) {
        constructor.onRegisterCreativeModeTabs(new CreativeModeTabContextNeoForgeImpl(modEventBus));
        constructor.onRegisterCompostableBlocks(new CompostableBlocksContextNeoForgeImpl(modId));
        if (flagsToHandle.contains(ContentRegistrationFlags.BIOME_MODIFICATIONS)) {
            NeoForgeBiomeLoadingHandler.register(modId, modEventBus, biomeModifications);
        }
        if (flagsToHandle.contains(ContentRegistrationFlags.CRAFTING_TRANSMUTE)) {
            DeferredRegister<RecipeSerializer<?>> deferredRegister = DeferredRegister.create(Registries.RECIPE_SERIALIZER,
                    modId);
            deferredRegister.register(modEventBus);
            CustomTransmuteRecipe.registerSerializers(deferredRegister::register);
        }
    }

    private static void registerLoadingHandlers(ModConstructor constructor, String modId, IEventBus eventBus, Multimap<BiomeLoadingPhase, NeoForgeBiomeLoadingHandler.BiomeModification> biomeModifications, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        eventBus.addListener((final FMLCommonSetupEvent evt) -> {
            evt.enqueueWork(() -> {
                constructor.onCommonSetup();
                constructor.onRegisterBiomeModifications(new BiomeModificationsContextNeoForgeImpl(biomeModifications,
                        availableFlags));
                constructor.onRegisterFlammableBlocks(new FlammableBlocksContextNeoForgeImpl());
                constructor.onRegisterBlockInteractions(new BlockInteractionsContextNeoForgeImpl());
            });
        });
        eventBus.addListener((final RegisterSpawnPlacementsEvent evt) -> {
            constructor.onRegisterSpawnPlacements(new SpawnPlacementsContextNeoForgeImpl(evt));
        });
        eventBus.addListener((final EntityAttributeCreationEvent evt) -> {
            constructor.onEntityAttributeCreation(new EntityAttributesCreateContextNeoForgeImpl(evt::put));
        });
        eventBus.addListener((final EntityAttributeModificationEvent evt) -> {
            constructor.onEntityAttributeModification(new EntityAttributesModifyContextNeoForgeImpl(evt::add));
        });
        eventBus.addListener((final BuildCreativeModeTabContentsEvent evt) -> {
            constructor.onBuildCreativeModeTabContents(new CreativeTabContentsContextNeoForgeImpl(evt));
        });
        eventBus.addListener((final AddPackFindersEvent evt) -> {
            if (evt.getPackType() == PackType.SERVER_DATA) {
                constructor.onAddDataPackFinders(new DataPackSourcesContextNeoForgeImpl(evt::addRepositorySource));
                if (flagsToHandle.contains(ContentRegistrationFlags.BIOME_MODIFICATIONS)) {
                    evt.addRepositorySource(NeoForgeBiomeLoadingHandler.buildPack(modId));
                }
            }
        });
        eventBus.addListener((final DataPackRegistryEvent.NewRegistry evt) -> {
            constructor.onDataPackRegistriesContext(new DataPackRegistriesContextNeoForgeImpl(evt));
        });
    }
}
