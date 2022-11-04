package fuzs.puzzleslib.core;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.mixin.accessor.SpawnPlacementsAccessor;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
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

/**
 * wrapper class for {@link ModConstructor} for calling all required registration methods at the correct time,
 * which means everything is called immediately on Fabric (but in the correct order)
 *
 * <p>we use this wrapper style to allow for already registered to be used within the registration methods instead of having to use suppliers
 * (this doesn't really matter on Fabric)
 */
public class FabricModConstructor {

    /**
     * @param constructor the common mod main class implementation
     */
    private FabricModConstructor(ModConstructor constructor) {
        // only call ModConstructor::onConstructMod during object construction to be similar to Forge
        constructor.onConstructMod();
    }

    /**
     * utility method for registering attributes for our own entities
     *
     * @param type type of entity
     * @param builder the attribute supplier builder
     */
    @SuppressWarnings("ConstantConditions")
    private void registerEntityAttribute(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
        // this is not allowed on Forge, use the separate method which mirrors the Forge implementation
        if (DefaultAttributes.hasSupplier(type)) throw new IllegalStateException("Duplicate DefaultAttributes entry: " + type);
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
        Map<Attribute, Double> attributeToBaseValueMap = Registry.ATTRIBUTE.stream()
                .filter(supplier::hasAttribute)
                .map(attribute1 -> supplier.createInstance(instance -> {}, attribute1))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(AttributeInstance::getAttribute, AttributeInstance::getBaseValue));
        attributeToBaseValueMap.put(attribute, attributeValue);
        AttributeSupplier.Builder builder = AttributeSupplier.builder();
        attributeToBaseValueMap.forEach(builder::add);
        FabricDefaultAttributeRegistry.register(type, builder);
    }

    /**
     * utility method for registering a new fuel item
     *
     * @param item     the fuel item
     * @param burnTime burn time in ticks
     */
    private void registerFuelItem(Item item, int burnTime) {
        if (burnTime > 0 && item != null) FuelRegistry.INSTANCE.add(item, burnTime);
    }

    private ModConstructor.LootTablesReplaceContext getLootTablesReplaceContext(LootTables lootManager, ResourceLocation id, LootTable original, MutableObject<LootTable> replacement) {
        return new ModConstructor.LootTablesReplaceContext(lootManager, id, original) {

            @Override
            public void setLootTable(LootTable table) {
                replacement.setValue(table);
            }
        };
    }

    private ModConstructor.LootTablesModifyContext getLootTablesModifyContext(LootTables lootManager, ResourceLocation id, LootTable.Builder tableBuilder) {
        return new ModConstructor.LootTablesModifyContext(lootManager, id) {

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

    /**
     * construct the mod, calling all necessary registration methods (we don't need the object, it's only useful on Forge)
     *
     * @param modId the mod id for registering events on Forge to the correct mod event bus
     * @param constructor mod base class
     */
    public static void construct(String modId, ModConstructor constructor) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("modId cannot be empty");
        PuzzlesLib.LOGGER.info("Constructing common components for mod {}", modId);
        FabricModConstructor fabricModConstructor = new FabricModConstructor(constructor);
        // everything after this is done on Forge using events called by the mod event bus
        // this is done since Forge works with loading stages, Fabric doesn't have those stages, so everything is called immediately
        constructor.onCommonSetup();
        constructor.onRegisterSpawnPlacements(SpawnPlacementsAccessor::callRegister);
        constructor.onEntityAttributeCreation(fabricModConstructor::registerEntityAttribute);
        constructor.onEntityAttributeModification(fabricModConstructor::modifyEntityAttribute);
        constructor.onRegisterFuelBurnTimes(fabricModConstructor::registerFuelItem);
        CommandRegistrationCallback.EVENT.register((CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) -> constructor.onRegisterCommands(new ModConstructor.RegisterCommandsContext(dispatcher, dedicated)));
        LootTableEvents.REPLACE.register((ResourceManager resourceManager, LootTables lootManager, ResourceLocation id, LootTable original, LootTableSource source) -> {
            // keep this the same as Forge, editing data pack specified loot tables is not supported
            if (source != LootTableSource.DATA_PACK) {
                MutableObject<LootTable> replacement = new MutableObject<>();
                constructor.onLootTableReplacement(fabricModConstructor.getLootTablesReplaceContext(lootManager, id, original, replacement));
                // still returns null if nothing has been set
                return replacement.getValue();
            }
            return null;
        });
        LootTableEvents.MODIFY.register((ResourceManager resourceManager, LootTables lootManager, ResourceLocation id, LootTable.Builder tableBuilder, LootTableSource source) -> {
            // keep this the same as Forge, editing data pack specified loot tables is not supported
            if (source != LootTableSource.DATA_PACK) constructor.onLootTableModification(fabricModConstructor.getLootTablesModifyContext(lootManager, id, tableBuilder));
        });
    }
}
