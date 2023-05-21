package fuzs.puzzleslib.impl.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ForgeModConstructor {
    private final ModConstructor constructor;
    private final Multimap<BiomeLoadingPhase, BiomeLoadingHandler.BiomeModificationData> biomeLoadingEntries = HashMultimap.create();

    private ForgeModConstructor(ModConstructor constructor) {
        this.constructor = constructor;
        constructor.onConstructMod();
    }

    @SubscribeEvent
    public void onCommonSetup(final FMLCommonSetupEvent evt) {
        this.constructor.onCommonSetup(evt::enqueueWork);
        this.constructor.onRegisterFuelBurnTimes(new FuelBurnTimesContextForgeImpl());
        this.constructor.onRegisterBiomeModifications((BiomeLoadingPhase phase, Predicate<BiomeLoadingContext> selector, Consumer<BiomeModificationContext> modifier) -> {
            this.biomeLoadingEntries.put(phase, new BiomeLoadingHandler.BiomeModificationData(phase, selector, modifier));
        });
        this.constructor.onRegisterFlammableBlocks(new FlammableBlocksContextForgeImpl());
    }

    @SubscribeEvent
    public void onRegisterSpawnPlacement(final SpawnPlacementRegisterEvent evt) {
        this.constructor.onRegisterSpawnPlacements(new SpawnPlacementsContextForgeImpl(evt));
    }

    @SubscribeEvent
    public void onEntityAttributeCreation(final EntityAttributeCreationEvent evt) {
        this.constructor.onEntityAttributeCreation(new EntityAttributesCreateContextForgeImpl(evt::put));
    }

    @SubscribeEvent
    public void onEntityAttributeModification(final EntityAttributeModificationEvent evt) {
        this.constructor.onEntityAttributeModification(new EntityAttributesModifyContextForgeImpl(evt::add));
    }

    @SubscribeEvent
    public void onCreativeModeTab$Register(final CreativeModeTabEvent.Register evt) {
        this.constructor.onRegisterCreativeModeTabs(new CreativeModeTabContextForgeImpl(evt::registerCreativeModeTab));
    }

    @SubscribeEvent
    public void onAddPackFinders(final AddPackFindersEvent evt) {
        if (evt.getPackType() == PackType.SERVER_DATA) {
            this.constructor.onAddDataPackFinders(new DataPackSourcesContextForgeImpl(evt::addRepositorySource));
        }
    }

    public static void construct(ModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        ForgeModConstructor forgeModConstructor = new ForgeModConstructor(constructor);
        Optional<IEventBus> optional = ModContainerHelper.findModEventBus(modId);
        if (optional.isEmpty()) return;
        IEventBus eventBus = optional.get();
        eventBus.register(forgeModConstructor);
        // we need to manually register events for the normal event bus
        // as you cannot have both event bus types going through SubscribeEvent annotated methods in the same class
        MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent evt) -> {
            forgeModConstructor.constructor.onRegisterDataPackReloadListeners(new AddReloadListenersContextForgeImpl(evt::addListener));
        });
        if (ArrayUtils.contains(contentRegistrations, ContentRegistrationFlags.BIOMES)) {
            BiomeLoadingHandler.register(modId, eventBus, forgeModConstructor.biomeLoadingEntries);
        }
    }
}
