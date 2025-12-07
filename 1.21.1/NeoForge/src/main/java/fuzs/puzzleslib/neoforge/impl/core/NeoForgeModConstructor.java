package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v2.context.BiomeModificationsContext;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.item.CopyComponentsRecipe;
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
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.util.Set;

public final class NeoForgeModConstructor implements ModConstructorImpl<ModConstructor> {

    @Override
    public void construct(String modId, ModConstructor modConstructor, Set<ContentRegistrationFlags> contentRegistrationFlags) {
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            EntityAttributesContextNeoForgeImpl[] entityAttributesContext = new EntityAttributesContextNeoForgeImpl[1];
            modConstructor.onConstructMod();
            // these need to run immediately, as they register content for data generation,
            // which cannot be added during common setup, as it does not run during data generation
            modConstructor.onRegisterGameplayContent(new GameplayContentContextNeoForgeImpl(modId, eventBus));
            BiomeModificationsContextNeoForgeImpl biomeModificationsContext = new BiomeModificationsContextNeoForgeImpl(
                    modId,
                    eventBus,
                    contentRegistrationFlags);
            biomeModificationsContext.registerProviderPack();
            modConstructor.onRegisterBiomeModifications((BiomeModificationsContext) biomeModificationsContext);
            modConstructor.onRegisterCompostableBlocks(new CompostableBlocksContextNeoForgeImpl(modId));
            modConstructor.onRegisterCreativeModeTabs(new CreativeModeTabContextNeoForgeImpl(eventBus));
            if (contentRegistrationFlags.contains(ContentRegistrationFlags.COPY_RECIPES)) {
                DeferredRegister<RecipeSerializer<?>> deferredRegister = DeferredRegister.create(Registries.RECIPE_SERIALIZER,
                        modId);
                deferredRegister.register(eventBus);
                CopyComponentsRecipe.registerSerializers(deferredRegister::register);
            }

            eventBus.addListener((final FMLCommonSetupEvent event) -> {
                event.enqueueWork(() -> {
                    modConstructor.onCommonSetup();
                    modConstructor.onRegisterVillagerTrades(new VillagerTradesContextNeoForgeImpl());
                    modConstructor.onRegisterBiomeModifications((fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext) biomeModificationsContext);
                    modConstructor.onRegisterFuelBurnTimes(new FuelBurnTimesContextNeoForgeImpl());
                    modConstructor.onRegisterFlammableBlocks(new FlammableBlocksContextNeoForgeImpl());
                    modConstructor.onRegisterBlockInteractions(new BlockInteractionsContextNeoForgeImpl());
                });
            });
            eventBus.addListener((final RegisterSpawnPlacementsEvent event) -> {
                modConstructor.onRegisterSpawnPlacements(new SpawnPlacementsContextNeoForgeImpl(event));
            });
            eventBus.addListener((final EntityAttributeCreationEvent event) -> {
                AbstractNeoForgeContext.computeIfAbsent(entityAttributesContext,
                        EntityAttributesContextNeoForgeImpl::new,
                        modConstructor::onRegisterEntityAttributes).registerForEvent(event);
                modConstructor.onEntityAttributeCreation(new EntityAttributesCreateContextNeoForgeImpl(event::put));
            });
            eventBus.addListener((final EntityAttributeModificationEvent event) -> {
                AbstractNeoForgeContext.computeIfAbsent(entityAttributesContext,
                        EntityAttributesContextNeoForgeImpl::new,
                        modConstructor::onRegisterEntityAttributes).registerForEvent(event);
                modConstructor.onEntityAttributeModification(new EntityAttributesModifyContextNeoForgeImpl(event::add));
            });
            eventBus.addListener((final BuildCreativeModeTabContentsEvent evt) -> {
                modConstructor.onBuildCreativeModeTabContents(new CreativeTabContentsContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final AddPackFindersEvent event) -> {
                if (event.getPackType() == PackType.SERVER_DATA) {
                    modConstructor.onAddDataPackFinders(new DataPackSourcesContextNeoForgeImpl(event));
                }
            });
            eventBus.addListener((final NewRegistryEvent event) -> {
                modConstructor.onRegisterGameRegistries(new GameRegistriesContextNeoForgeImpl(event));
            });
            eventBus.addListener((final DataPackRegistryEvent.NewRegistry event) -> {
                modConstructor.onRegisterDataPackRegistries(new DataPackRegistriesContextNeoForgeImpl(event));
            });
        });
    }
}
