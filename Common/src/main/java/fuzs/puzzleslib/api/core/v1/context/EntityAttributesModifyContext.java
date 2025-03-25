package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

/**
 * Registers modifications to attributes of already existing entities.
 */
@FunctionalInterface
public interface EntityAttributesModifyContext {

    /**
     * Modify attributes of existing entity types.
     * <p>
     * This overrides or adds attributes individually.
     *
     * @param entityType the entity type
     * @param attribute  attribute to override or add
     */
    default void registerAttributeModification(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute) {
        this.registerAttributeModification(entityType, attribute, attribute.value().getDefaultValue());
    }

    /**
     * Modify attributes of existing entity types.
     * <p>
     * This overrides or adds attributes individually.
     *
     * @param entityType     the entity type
     * @param attribute      attribute to override or add
     * @param attributeValue new value, possibly {@link Attribute#getDefaultValue()}
     */
    void registerAttributeModification(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute, double attributeValue);
}
