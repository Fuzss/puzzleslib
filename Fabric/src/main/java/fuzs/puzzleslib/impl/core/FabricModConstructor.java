package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.contexts.LootTablesContext;
import fuzs.puzzleslib.api.core.v1.contexts.RegisterCommandsContext;
import fuzs.puzzleslib.impl.core.contexts.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.function.Consumer;

public final class FabricModConstructor {

    private FabricModConstructor() {

    }

    public static void construct(ModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        constructor.onConstructMod();
        constructor.onCommonSetup(Runnable::run);
        constructor.onRegisterSpawnPlacements(SpawnPlacements::register);
        constructor.onEntityAttributeCreation(new EntityAttributesCreateContextFabricImpl());
        constructor.onEntityAttributeModification(new EntityAttributesModifyContextFabricImpl());
        constructor.onRegisterFuelBurnTimes(new FuelBurnTimesContextFabricImpl());
        registerCommands(constructor::onRegisterCommands);
        registerLootTablesListeners(constructor::onLootTableReplacement, constructor::onLootTableModification);
        constructor.onRegisterBiomeModifications(new BiomeModificationsContextFabricImpl(modId));
        constructor.onRegisterFlammableBlocks(new FlammableBlocksContextFabricImpl());
        constructor.onRegisterCreativeModeTabs(new CreativeModeTabContextFabricImpl());
    }

    private static void registerCommands(Consumer<RegisterCommandsContext> consumer) {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) -> {
            consumer.accept(new RegisterCommandsContext(dispatcher, context, environment));
        });
    }

    private static void registerLootTablesListeners(Consumer<LootTablesContext.Replace> consumer1, Consumer<LootTablesContext.Modify> consumer2) {
        LootTableEvents.REPLACE.register((ResourceManager resourceManager, LootTables lootManager, ResourceLocation id, LootTable original, LootTableSource source) -> {
            // keep this the same as Forge where editing data pack specified loot tables is not supported
            if (source == LootTableSource.DATA_PACK) return null;
            MutableObject<LootTable> replacement = new MutableObject<>();
            consumer1.accept(new LootTablesReplaceContext(lootManager, id, original, replacement));
            // still returns null if nothing has been set
            return replacement.getValue();
        });
        LootTableEvents.MODIFY.register((ResourceManager resourceManager, LootTables lootManager, ResourceLocation id, LootTable.Builder tableBuilder, LootTableSource source) -> {
            // keep this the same as Forge where editing data pack specified loot tables is not supported
            if (source == LootTableSource.DATA_PACK) return;
            consumer2.accept(new LootTablesModifyContext(lootManager, id, tableBuilder));
        });
    }
}
