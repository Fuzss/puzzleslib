package fuzs.puzzleslib.api.core.v1.contexts;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

/**
 * register default attributes for our own entities
 */
@FunctionalInterface
public interface EntityAttributesCreateContext {

    /**
     * register attributes for our own entities, modifying attributes for any other entity (vanilla or modded) should be done using {@link EntityAttributesModifyContext}
     *
     * @param type    type of entity
     * @param builder the attribute supplier builder
     */
    void registerEntityAttributes(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder);
}
