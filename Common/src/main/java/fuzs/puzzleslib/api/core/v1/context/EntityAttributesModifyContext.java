package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

/**
 * registers modifications to attributes of already existing entities (not from our mod)
 */
@FunctionalInterface
public interface EntityAttributesModifyContext {

    /**
     * use this method for modifying attributes of existing entity types
     * this overrides/adds attributes individually as this is what is done on Forge
     *
     * @param type      type of entity
     * @param attribute attribute to override/add
     */
    default void registerAttributeModification(EntityType<? extends LivingEntity> type, Attribute attribute) {
        this.registerAttributeModification(type, attribute, attribute.getDefaultValue());
    }

    /**
     * use this method for modifying attributes of existing entity types
     * this overrides/adds attributes individually as this is what is done on Forge
     *
     * @param type           type of entity
     * @param attribute      attribute to override/add
     * @param attributeValue new value, possibly {@link Attribute#getDefaultValue()}
     */
    void registerAttributeModification(EntityType<? extends LivingEntity> type, Attribute attribute, double attributeValue);
}
