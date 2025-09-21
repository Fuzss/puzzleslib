package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.EntityAttributesContext;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

import java.util.Objects;

@SuppressWarnings("DataFlowIssue")
public final class EntityAttributesContextFabricImpl implements EntityAttributesContext {

    @Override
    public void registerAttributes(EntityType<? extends LivingEntity> entityType, AttributeSupplier.Builder attributesBuilder) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(attributesBuilder, "attributes builder is null");
        Preconditions.checkState(!DefaultAttributes.hasSupplier(entityType),
                "attributes already present for " + entityType);
        FabricDefaultAttributeRegistry.register(entityType, attributesBuilder);
    }

    @Override
    public void registerAttribute(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute, double attributeValue) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(attribute, "attribute is null");
        Preconditions.checkState(DefaultAttributes.hasSupplier(entityType), "attributes missing for " + entityType);
        AttributeSupplier attributeSupplier = DefaultAttributes.getSupplier(entityType);
        AttributeSupplier.Builder attributesBuilder = AttributeSupplier.builder();
        // there aren't many attributes anyway, so iterating the whole registry isn't costly
        BuiltInRegistries.ATTRIBUTE.listElements().forEach((Holder.Reference<Attribute> holder) -> {
            if (attributeSupplier.hasAttribute(holder)) {
                attributesBuilder.add(holder, attributeSupplier.getBaseValue(holder));
            }
        });
        attributesBuilder.add(attribute, attributeValue);
        FabricDefaultAttributeRegistry.register(entityType, attributesBuilder);
    }
}
