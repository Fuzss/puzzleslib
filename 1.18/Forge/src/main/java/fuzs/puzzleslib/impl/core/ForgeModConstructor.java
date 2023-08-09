package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.impl.core.context.*;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class ForgeModConstructor {

    private ForgeModConstructor() {

    }

    public static void construct(ModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        ModContainerHelper.findModEventBus(modId).ifPresent(modEventBus -> {
            registerModHandlers(constructor, modEventBus, contentRegistrations);
            registerHandlers(constructor);
            constructor.onConstructMod();
            // needs to happen early, will otherwise miss out on search trees
            // also corresponding event needs to run on mod event bus since normal event bus is still shutdown at this point
            constructor.onRegisterCreativeModeTabs(new CreativeModeTabContextForgeImpl());
        });
    }

    private static void registerModHandlers(ModConstructor constructor, IEventBus eventBus, ContentRegistrationFlags[] contentRegistrations) {
        eventBus.addListener((final FMLCommonSetupEvent evt) -> {
            constructor.onCommonSetup(evt::enqueueWork);
            constructor.onRegisterFuelBurnTimes(new FuelBurnTimesContextForgeImpl());
            constructor.onRegisterBiomeModifications(new BiomeModificationsContextForgeImpl(contentRegistrations));
            constructor.onRegisterFlammableBlocks(new FlammableBlocksContextForgeImpl());
            constructor.onRegisterSpawnPlacements(new SpawnPlacementsContextForgeImpl());
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
