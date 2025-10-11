package fuzs.puzzleslib.fabric.impl.attachment.builder;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public final class FabricEntityDataAttachmentBuilder<V> extends FabricDataAttachmentBuilder<Entity, V, DataAttachmentRegistry.EntityBuilder<V>> implements DataAttachmentRegistry.EntityBuilder<V> {
    private boolean copyOnDeath;

    @Override
    public DataAttachmentRegistry.EntityBuilder<V> getThis() {
        return this;
    }

    @Override
    public DataAttachmentRegistry.EntityBuilder<V> defaultValue(Predicate<Entity> defaultFilter, Function<RegistryAccess, V> defaultValueProvider) {
        Objects.requireNonNull(defaultFilter, "default filter is null");
        Objects.requireNonNull(defaultValueProvider, "default value provider is null");
        this.defaultValues.put(defaultFilter, defaultValueProvider);
        return this;
    }

    @Override
    public DataAttachmentRegistry.EntityBuilder<V> copyOnDeath() {
        this.copyOnDeath = true;
        return this;
    }

    @Override
    void configureBuilder(AttachmentRegistry.Builder<V> builder) {
        super.configureBuilder(builder);
        if (this.copyOnDeath) {
            // Fabric does not need this check, but NeoForge does, so implement it for both
            Objects.requireNonNull(this.codec, "codec is null");
            builder.copyOnDeath();
        }
    }

    @Override
    protected RegistryAccess getRegistryAccess(Entity holder) {
        return holder.registryAccess();
    }
}
