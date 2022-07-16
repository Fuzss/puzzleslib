package fuzs.puzzleslib.core;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.mixin.object.builder.SpawnRestrictionAccessor;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Fabric implementation of {@link CommonRegistration}
 */
public class FabricRegistration implements CommonRegistration {

    @Override
    public <T extends Mob> void registerSpawnPlacement(EntityType<T> entityType, SpawnPlacements.Type location, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
        // this accessor is not within an api package, so using it is not a good idea (if it is removed just add the accessor ourselves)
        // unfortunately there is no other access to this within the api, as it's only used as part of net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder
        // which we cannot use without introducing yet another wrapper as it's a Fabric exclusive class
        SpawnRestrictionAccessor.callRegister(entityType, location, heightmap, spawnPredicate);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void registerEntityAttribute(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
        // this is not allowed on Forge, use the separate method which mirrors the Forge implementation
        if (DefaultAttributes.hasSupplier(type)) throw new IllegalStateException("Duplicate DefaultAttributes entry: " + type);
        FabricDefaultAttributeRegistry.register(type, builder);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void modifyEntityAttribute(EntityType<? extends LivingEntity> type, Attribute attribute, double attributeValue) {
        // Forge makes this very simple by patching in a couple of helper methods, but Fabric should work like this
        AttributeSupplier supplier = DefaultAttributes.getSupplier(type);
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
}
