package fuzs.puzzleslib.fabric.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.EntityAttributesModifyContext;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EntityAttributesModifyContextFabricImpl implements EntityAttributesModifyContext {

    @Override
    public void registerAttributeModification(EntityType<? extends LivingEntity> entityType, Attribute attribute, double attributeValue) {
        // Forge makes this very simple by patching in a couple of helper methods, but Fabric should work like this
        AttributeSupplier supplier = DefaultAttributes.getSupplier(entityType);
        // there aren't many attributes anyway, so iterating the whole registry isn't costly
        Map<Attribute, Double> attributeToBaseValueMap = BuiltInRegistries.ATTRIBUTE.stream().filter(supplier::hasAttribute).map(attribute1 -> supplier.createInstance(instance -> {
        }, attribute1)).filter(Objects::nonNull).collect(Collectors.toMap(AttributeInstance::getAttribute, AttributeInstance::getBaseValue));
        attributeToBaseValueMap.put(attribute, attributeValue);
        AttributeSupplier.Builder builder = AttributeSupplier.builder();
        attributeToBaseValueMap.forEach(builder::add);
        FabricDefaultAttributeRegistry.register(entityType, builder);
    }
}
