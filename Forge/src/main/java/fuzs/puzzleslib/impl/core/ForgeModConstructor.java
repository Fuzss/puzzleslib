package fuzs.puzzleslib.impl.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.impl.biome.BiomeLoadingHandler;
import fuzs.puzzleslib.impl.core.context.*;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.commons.lang3.ArrayUtils;

public final class ForgeModConstructor {

    private ForgeModConstructor() {

    }

    public static void construct(ModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        ModContainerHelper.findModEventBus(modId).ifPresent(eventBus -> {
            Multimap<BiomeLoadingPhase, BiomeLoadingHandler.BiomeModification> biomeModifications = HashMultimap.create();
            registerContent(modId, eventBus, biomeModifications, contentRegistrations);
            registerModHandlers(constructor, eventBus, biomeModifications, contentRegistrations);
            registerHandlers(constructor);
            constructor.onConstructMod();
        });
    }

    private static void registerContent(String modId, IEventBus eventBus, Multimap<BiomeLoadingPhase, BiomeLoadingHandler.BiomeModification> biomeModifications, ContentRegistrationFlags[] contentRegistrations) {
        if (ArrayUtils.contains(contentRegistrations, ContentRegistrationFlags.BIOMES)) {
            BiomeLoadingHandler.register(modId, eventBus, biomeModifications);
        }
    }

    private static void registerModHandlers(ModConstructor constructor, IEventBus eventBus, Multimap<BiomeLoadingPhase, BiomeLoadingHandler.BiomeModification> biomeModifications, ContentRegistrationFlags[] contentRegistrations) {
        eventBus.addListener((final FMLCommonSetupEvent evt) -> {
            constructor.onCommonSetup(evt::enqueueWork);
            constructor.onRegisterFuelBurnTimes(new FuelBurnTimesContextForgeImpl());
            constructor.onRegisterBiomeModifications(new BiomeModificationsContextForgeImpl(biomeModifications, contentRegistrations));
            constructor.onRegisterFlammableBlocks(new FlammableBlocksContextForgeImpl());
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
        eventBus.addListener((final CreativeModeTabEvent.Register evt) -> {
            constructor.onRegisterCreativeModeTabs(new CreativeModeTabContextForgeImpl(evt::registerCreativeModeTab));
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
