package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.EntityAttributesCreateContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.Objects;
import java.util.function.BiConsumer;

@Deprecated
public record EntityAttributesCreateContextNeoForgeImpl(
        BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier> consumer) implements EntityAttributesCreateContext {

    @Override
    public void registerEntityAttributes(EntityType<? extends LivingEntity> entityType, AttributeSupplier.Builder builder) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(builder, "builder is null");
        this.consumer.accept(entityType, builder.build());
    }
}
