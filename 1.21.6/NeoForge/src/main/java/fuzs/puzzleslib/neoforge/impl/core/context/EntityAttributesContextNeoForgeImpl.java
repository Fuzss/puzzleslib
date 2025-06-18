package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.EntityAttributesContext;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

import java.util.Objects;

public final class EntityAttributesContextNeoForgeImpl extends AbstractNeoForgeContext implements EntityAttributesContext {

    @Override
    public void registerAttributes(EntityType<? extends LivingEntity> entityType, AttributeSupplier.Builder attributesBuilder) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(attributesBuilder, "attributes builder is null");
        this.registerForEvent(EntityAttributeCreationEvent.class, (EntityAttributeCreationEvent event) -> {
            event.put(entityType, attributesBuilder.build());
        });
    }

    @Override
    public void registerAttribute(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute, double attributeValue) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(attribute, "attribute is null");
        this.registerForEvent(EntityAttributeModificationEvent.class, (EntityAttributeModificationEvent event) -> {
            event.add(entityType, attribute, attributeValue);
        });
    }
}
