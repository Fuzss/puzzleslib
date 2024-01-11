package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.impl.core.context.*;
import fuzs.puzzleslib.impl.item.CopyTagRecipe;
import fuzs.puzzleslib.impl.item.ForgeCopyTagRecipeSerializer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

public final class ForgeModConstructor {

    private ForgeModConstructor() {

    }

    public static void construct(ModConstructor constructor, String modId, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        ModContainerHelper.findModEventBus(modId).ifPresent(modEventBus -> {
            registerContent(constructor, modId, modEventBus, flagsToHandle);
            registerModHandlers(constructor, modEventBus, availableFlags);
            registerHandlers(constructor);
            constructor.onConstructMod();
        });
    }

    private static void registerContent(ModConstructor constructor, String modId, IEventBus modEventBus, Set<ContentRegistrationFlags> flagsToHandle) {
        if (flagsToHandle.contains(ContentRegistrationFlags.COPY_TAG_RECIPES)) {
            DeferredRegister<RecipeSerializer<?>> deferredRegister = DeferredRegister.create(ForgeRegistries.Keys.RECIPE_SERIALIZERS, modId);
            deferredRegister.register(modEventBus);
            if (flagsToHandle.contains(ContentRegistrationFlags.COPY_TAG_RECIPES)) {
                CopyTagRecipe.registerSerializers(deferredRegister::register, ForgeCopyTagRecipeSerializer::new);
            }
        }
    }

    private static void registerModHandlers(ModConstructor constructor, IEventBus eventBus, Set<ContentRegistrationFlags> availableFlags) {
        // we need to run this before search trees are created on the client, all lifecycle events that run after registration run way too late for this
        // other events running in-between don't seem to run during data gen, which relies on items having valid creative tabs
        // so since blocks and items are guaranteed to be registered first, we just pick basically any other registration type (they should run in alphabetical order)
        eventBus.addGenericListener(Fluid.class, (final RegistryEvent<Fluid> evt) -> {
            constructor.onRegisterCreativeModeTabs(new CreativeModeTabContextForgeImpl());
            constructor.onBuildCreativeModeTabContents(new BuildCreativeModeTabContentsContextForgeImpl());
        });
        eventBus.addListener((final FMLCommonSetupEvent evt) -> {
            evt.enqueueWork(() -> {
                constructor.onCommonSetup();
                constructor.onRegisterFuelBurnTimes(new FuelBurnTimesContextForgeImpl());
                constructor.onRegisterBiomeModifications(new BiomeModificationsContextForgeImpl(availableFlags));
                constructor.onRegisterFlammableBlocks(new FlammableBlocksContextForgeImpl());
                constructor.onRegisterSpawnPlacements(new SpawnPlacementsContextForgeImpl());
            });
        });
        eventBus.addListener((final EntityAttributeCreationEvent evt) -> {
            constructor.onEntityAttributeCreation(new EntityAttributesCreateContextForgeImpl(evt::put));
        });
        eventBus.addListener((final EntityAttributeModificationEvent evt) -> {
            constructor.onEntityAttributeModification(new EntityAttributesModifyContextForgeImpl(evt::add));
        });
        eventBus.addListener((final AddPackFindersEvent evt) -> {
            if (evt.getPackType() == PackType.SERVER_DATA) {
                constructor.onAddDataPackFinders(new DataPackSourcesContextForgeImpl(evt::addRepositorySource));
            }
        });
    }

    private static void registerHandlers(ModConstructor constructor) {
        MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent evt) -> {
            constructor.onRegisterDataPackReloadListeners(new AddReloadListenersContextForgeImpl(evt::addListener));
        });
    }
}
