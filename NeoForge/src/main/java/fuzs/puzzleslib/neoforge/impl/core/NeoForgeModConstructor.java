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

    public static void construct(ModConstructor constructor, String modId) {
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            constructor.onConstructMod();
            // these need to run immediately, as they register content for data generation,
            // which cannot be added during common setup, as it does not run during data generation
            constructor.onRegisterCompostableBlocks(new CompostableBlocksContextNeoForgeImpl(modId));
            constructor.onRegisterBiomeModifications(new BiomeModificationsContextNeoForgeImpl(modId, eventBus));
            eventBus.addListener((final FMLCommonSetupEvent evt) -> {
                evt.enqueueWork(() -> {
                    constructor.onCommonSetup();
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
            eventBus.addListener((final AddPackFindersEvent evt) -> {
                if (evt.getPackType() == PackType.SERVER_DATA) {
                    constructor.onAddDataPackFinders(new DataPackSourcesContextNeoForgeImpl(evt::addRepositorySource));
                }
            });
            eventBus.addListener((final NewRegistryEvent evt) -> {
                constructor.onGameRegistriesContext(new GameRegistriesContextNeoForgeImpl(evt));
            });
            eventBus.addListener((final DataPackRegistryEvent.NewRegistry evt) -> {
                constructor.onDataPackRegistriesContext(new DataPackRegistriesContextNeoForgeImpl(evt));
            });
        });
    }
}
