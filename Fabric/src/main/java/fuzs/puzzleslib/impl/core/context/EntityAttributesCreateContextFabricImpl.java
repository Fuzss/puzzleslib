package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.EntityAttributesCreateContext;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

public final class EntityAttributesCreateContextFabricImpl implements EntityAttributesCreateContext {

    @Override
    public void registerEntityAttributes(EntityType<? extends LivingEntity> entityType, AttributeSupplier.Builder builder) {
        // this is not allowed on Forge, use the separate method which mirrors the Forge implementation
        if (DefaultAttributes.hasSupplier(entityType)) {
            throw new IllegalStateException("Duplicate DefaultAttributes entry: " + entityType);
        }
        FabricDefaultAttributeRegistry.register(entityType, builder);
    }
}
