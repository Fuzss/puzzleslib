package fuzs.puzzleslib.fabric.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.EntityAttributesCreateContext;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

@Deprecated
public final class EntityAttributesCreateContextFabricImpl implements EntityAttributesCreateContext {

    @Override
    public void registerEntityAttributes(EntityType<? extends LivingEntity> entityType, AttributeSupplier.Builder builder) {
        // do not check DefaultAttributes::hasSupplier, it crashes on some dedicated servers
        FabricDefaultAttributeRegistry.register(entityType, builder);
    }
}
