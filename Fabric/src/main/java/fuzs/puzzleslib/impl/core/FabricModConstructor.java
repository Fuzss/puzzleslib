package fuzs.puzzleslib.impl.core;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.contexts.*;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.core.contexts.BiomeModificationsContextFabricImpl;
import fuzs.puzzleslib.impl.core.contexts.CreativeModeTabContextFabricImpl;
import fuzs.puzzleslib.impl.core.contexts.FlammableBlocksContextFabricImpl;
import fuzs.puzzleslib.impl.core.contexts.FuelBurnTimesContextFabricImpl;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FabricModConstructor {

    private FabricModConstructor() {

    }

    /**
     * construct the mod, calling all necessary registration methods (we don't need the object, it's only useful on Forge)
     *
     * @param modId       the mod id for registering events on Forge to the correct mod event bus
     * @param constructor mod base class
     */
    public static void construct(ModConstructor constructor, String modId, ContentRegistrationFlags... contentRegistrations) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("mod id cannot be empty");
        PuzzlesLib.LOGGER.info("Constructing common components for mod {}", modId);
        FabricModConstructor fabricModConstructor = new FabricModConstructor();
        // everything after this is done on Forge using events called by the mod event bus
        // this is done since Forge works with loading stages, Fabric doesn't have those stages, so everything is called immediately
        constructor.onConstructMod();
        constructor.onCommonSetup(Runnable::run);
        constructor.onRegisterSpawnPlacements(SpawnPlacements::register);
        constructor.onEntityAttributeCreation(fabricModConstructor::registerEntityAttribute);
        constructor.onEntityAttributeModification(fabricModConstructor::modifyEntityAttribute);
        constructor.onRegisterFuelBurnTimes(new FuelBurnTimesContextFabricImpl());
        CommandRegistrationCallback.EVENT.register((CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) -> {
            constructor.onRegisterCommands(new RegisterCommandsContext(dispatcher, context, environment));
        });
        LootTableEvents.REPLACE.register((ResourceManager resourceManager, LootTables lootManager, ResourceLocation id, LootTable original, LootTableSource source) -> {
            // keep this the same as Forge where editing data pack specified loot tables is not supported
            if (source != LootTableSource.DATA_PACK) {
                MutableObject<LootTable> replacement = new MutableObject<>();
                constructor.onLootTableReplacement(fabricModConstructor.getLootTablesReplaceContext(lootManager, id, original, replacement));
                // still returns null if nothing has been set
                return replacement.getValue();
            }
            return null;
        });
        LootTableEvents.MODIFY.register((ResourceManager resourceManager, LootTables lootManager, ResourceLocation id, LootTable.Builder tableBuilder, LootTableSource source) -> {
            // keep this the same as Forge where editing data pack specified loot tables is not supported
            if (source != LootTableSource.DATA_PACK) {
                constructor.onLootTableModification(fabricModConstructor.getLootTablesModifyContext(lootManager, id, tableBuilder));
            }
        });
        constructor.onRegisterBiomeModifications(new BiomeModificationsContextFabricImpl(modId));
        constructor.onRegisterFlammableBlocks(new FlammableBlocksContextFabricImpl());
        constructor.onRegisterCreativeModeTabs(new CreativeModeTabContextFabricImpl());
    }

    /**
     * utility method for registering attributes for our own entities
     *
     * @param type    type of entity
     * @param builder the attribute supplier builder
     */
    @SuppressWarnings("ConstantConditions")
    private void registerEntityAttribute(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
        // this is not allowed on Forge, use the separate method which mirrors the Forge implementation
        if (DefaultAttributes.hasSupplier(type))
            throw new IllegalStateException("Duplicate DefaultAttributes entry: " + type);
        FabricDefaultAttributeRegistry.register(type, builder);
    }

    /**
     * utility method for modifying default attributes for an existing entity type
     *
     * @param type           entity type to modify
     * @param attribute      the attribute to override
     * @param attributeValue new default value for <code>attribute</code>
     */
    @SuppressWarnings("ConstantConditions")
    private void modifyEntityAttribute(EntityType<? extends LivingEntity> type, Attribute attribute, double attributeValue) {
        // Forge makes this very simple by patching in a couple of helper methods, but Fabric should work like this
        AttributeSupplier supplier = DefaultAttributes.getSupplier(type);
        // there aren't many attributes anyway, so iterating the whole registry isn't costly
        Map<Attribute, Double> attributeToBaseValueMap = BuiltInRegistries.ATTRIBUTE.stream().filter(supplier::hasAttribute).map(attribute1 -> supplier.createInstance(instance -> {
        }, attribute1)).filter(Objects::nonNull).collect(Collectors.toMap(AttributeInstance::getAttribute, AttributeInstance::getBaseValue));
        attributeToBaseValueMap.put(attribute, attributeValue);
        AttributeSupplier.Builder builder = AttributeSupplier.builder();
        attributeToBaseValueMap.forEach(builder::add);
        FabricDefaultAttributeRegistry.register(type, builder);
    }

    private LootTablesContext.Replace getLootTablesReplaceContext(LootTables lootManager, ResourceLocation id, LootTable original, MutableObject<LootTable> replacement) {
        return new LootTablesContext.Replace(lootManager, id, original) {

            @Override
            public void setLootTable(LootTable table) {
                replacement.setValue(table);
            }
        };
    }

    private LootTablesContext.Modify getLootTablesModifyContext(LootTables lootManager, ResourceLocation id, LootTable.Builder tableBuilder) {
        return new LootTablesContext.Modify(lootManager, id) {

            @Override
            public void addLootPool(LootPool pool) {
                tableBuilder.pool(pool);
            }

            @Override
            public boolean removeLootPool(int index) {
                MutableInt counter = new MutableInt();
                MutableBoolean result = new MutableBoolean();
                tableBuilder.modifyPools(builder -> {
                    if (index == counter.getAndIncrement()) {
                        // there is no way in Fabric Api to remove loot pools, but this seems to work
                        builder.setRolls(ConstantValue.exactly(0.0F));
                        builder.setBonusRolls(ConstantValue.exactly(0.0F));
                        result.setTrue();
                    }
                });
                return result.booleanValue();
            }
        };
    }
}
