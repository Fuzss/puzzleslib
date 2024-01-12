package fuzs.puzzleslib.forge.impl.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.forge.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.forge.impl.core.context.*;
import fuzs.puzzleslib.impl.item.CopyTagRecipe;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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
            DeferredRegister<RecipeSerializer<?>> deferredRegister = DeferredRegister.create(ForgeRegistries.Keys.RECIPE_SERIALIZERS, modId);
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
        MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent evt) -> {
            constructor.onRegisterDataPackReloadListeners(new AddReloadListenersContextForgeImpl(modId, evt::addListener));
        });
    }
}
