package fuzs.puzzleslib.neoforge.impl.attachment.builder;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public final class NeoForgeEntityDataAttachmentBuilder<V> extends NeoForgeDataAttachmentBuilder<Entity, V, DataAttachmentRegistry.EntityBuilder<V>> implements DataAttachmentRegistry.EntityBuilder<V> {
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
    void configureBuilder(ResourceLocation resourceLocation, AttachmentType.Builder<V> builder) {
        super.configureBuilder(resourceLocation, builder);
        if (this.copyOnDeath) {
            Objects.requireNonNull(this.codec, "codec is null");
            builder.copyOnDeath();
        }
    }

    @Override
    protected RegistryAccess getRegistryAccess(Entity holder) {
        return holder.registryAccess();
    }
}
