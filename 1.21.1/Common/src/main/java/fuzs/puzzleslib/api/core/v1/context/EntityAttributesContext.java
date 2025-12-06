package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

/**
 * Register attributes for entities.
 */
public interface EntityAttributesContext {

    /**
     * Register attributes for a new entity type.
     *
     * @param entityType        the entity type
     * @param attributesBuilder the attribute supplier builder
     */
    void registerAttributes(EntityType<? extends LivingEntity> entityType, AttributeSupplier.Builder attributesBuilder);

    /**
     * Modify attributes of an existing entity type with the value from {@link Attribute#getDefaultValue()}.
     *
     * @param entityType the entity type
     * @param attribute  the attribute to override or add
     */
    default void registerAttribute(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute) {
        this.registerAttribute(entityType, attribute, attribute.value().getDefaultValue());
    }

    /**
     * Modify attributes of an existing entity type.
     *
     * @param entityType     the entity type
     * @param attribute      the attribute to override or add
     * @param attributeValue the default value
     */
    void registerAttribute(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute, double attributeValue);
}
