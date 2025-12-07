package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.core.context.*;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

public final class NeoForgeModConstructor implements ModConstructorImpl<ModConstructor> {

    @Override
    public void construct(String modId, ModConstructor modConstructor) {
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            EntityAttributesContextNeoForgeImpl[] entityAttributesContext = new EntityAttributesContextNeoForgeImpl[1];
            modConstructor.onConstructMod();
            // these need to run immediately, as they register content for data generation,
            // which cannot be added during common setup, as it does not run during data generation
            modConstructor.onRegisterGameplayContent(new GameplayContentContextNeoForgeImpl(modId, eventBus));
            modConstructor.onRegisterBiomeModifications(new BiomeModificationsContextNeoForgeImpl(modId, eventBus));
            eventBus.addListener((final FMLCommonSetupEvent event) -> {
                event.enqueueWork(() -> {
                    modConstructor.onCommonSetup();
                    modConstructor.onRegisterVillagerTrades(new VillagerTradesContextNeoForgeImpl());
                });
            });
            eventBus.addListener((final RegisterPayloadHandlersEvent event) -> {
                modConstructor.onRegisterPayloadTypes(NeoForgeProxy.get().createPayloadTypesContext(modId, event));
            });
            eventBus.addListener((final RegisterSpawnPlacementsEvent event) -> {
                modConstructor.onRegisterSpawnPlacements(new SpawnPlacementsContextNeoForgeImpl(event));
            });
            eventBus.addListener((final EntityAttributeCreationEvent event) -> {
                AbstractNeoForgeContext.computeIfAbsent(entityAttributesContext,
                        EntityAttributesContextNeoForgeImpl::new,
                        modConstructor::onRegisterEntityAttributes).registerForEvent(event);
            });
            eventBus.addListener((final EntityAttributeModificationEvent event) -> {
                AbstractNeoForgeContext.computeIfAbsent(entityAttributesContext,
                        EntityAttributesContextNeoForgeImpl::new,
                        modConstructor::onRegisterEntityAttributes).registerForEvent(event);
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
            eventBus.addListener((final AddServerReloadListenersEvent event) -> {
                modConstructor.onAddDataPackReloadListeners(new DataPackReloadListenersContextNeoForgeImpl(event));
            });
        });
    }
}
