package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.core.context.*;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

public final class NeoForgeModConstructor {

    private NeoForgeModConstructor() {
        // NO-OP
    }

    public static void construct(ModConstructor modConstructor, String modId) {
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            EntityAttributesContextNeoForgeImpl[] entityAttributesContext = new EntityAttributesContextNeoForgeImpl[1];
            modConstructor.onConstructMod();
            // these need to run immediately, as they register content for data generation,
            // which cannot be added during common setup, as it does not run during data generation
            modConstructor.onRegisterGameplayContent(new GameplayContentContextNeoForgeImpl(modId, eventBus));
            modConstructor.onRegisterBiomeModifications(new BiomeModificationsContextNeoForgeImpl(modId, eventBus));
            eventBus.addListener((final FMLCommonSetupEvent evt) -> {
                evt.enqueueWork(modConstructor::onCommonSetup);
            });
            eventBus.addListener((final RegisterSpawnPlacementsEvent evt) -> {
                modConstructor.onRegisterSpawnPlacements(new SpawnPlacementsContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final EntityAttributeCreationEvent evt) -> {
                AbstractNeoForgeContext.computeIfAbsent(entityAttributesContext,
                        EntityAttributesContextNeoForgeImpl::new,
                        modConstructor::onRegisterEntityAttributes);
            });
            eventBus.addListener((final EntityAttributeModificationEvent evt) -> {
                AbstractNeoForgeContext.computeIfAbsent(entityAttributesContext,
                        EntityAttributesContextNeoForgeImpl::new,
                        modConstructor::onRegisterEntityAttributes);
            });
            eventBus.addListener((final AddPackFindersEvent evt) -> {
                if (evt.getPackType() == PackType.SERVER_DATA) {
                    modConstructor.onAddDataPackFinders(new DataPackSourcesContextNeoForgeImpl(evt));
                }
            });
            eventBus.addListener((final NewRegistryEvent evt) -> {
                modConstructor.onRegisterGameRegistriesContext(new GameRegistriesContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final DataPackRegistryEvent.NewRegistry evt) -> {
                modConstructor.onRegisterDataPackRegistriesContext(new DataPackRegistriesContextNeoForgeImpl(evt));
            });
        });
    }
}
