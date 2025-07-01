package fuzs.puzzleslib.api.attachment.v4;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A registry for creating new attachment types.
 */
public final class DataAttachmentRegistry {

    private DataAttachmentRegistry() {
        // NO-OP
    }

    /**
     * @param <V> attachment value type
     * @return the entity attachment type builder
     */
    public static <V> EntityBuilder<V> entityBuilder() {
        return DataAttachmentRegistryImpl.INSTANCE.getEntityTypeBuilder();
    }

    /**
     * @param <V> attachment value type
     * @return the block entity attachment type builder
     */
    public static <V> BlockEntityBuilder<V> blockEntityBuilder() {
        return DataAttachmentRegistryImpl.INSTANCE.getBlockEntityTypeBuilder();
    }

    /**
     * @param <V> attachment value type
     * @return the level chunk attachment type builder
     */
    public static <V> Builder<LevelChunk, V> levelChunkBuilder() {
        return DataAttachmentRegistryImpl.INSTANCE.getLevelChunkBuilder();
    }

    /**
     * @param <V> attachment value type
     * @return the level attachment type builder
     */
    public static <V> Builder<Level, V> levelBuilder() {
        return DataAttachmentRegistryImpl.INSTANCE.getLevelBuilder();
    }

    /**
     * General attachment type builder.
     *
     * @param <T> attachment holder type
     * @param <V> attachment value type
     */
    public interface Builder<T, V> {

        /**
         * Set a default value for all attachment holders.
         *
         * @param defaultValue the default value
         * @return the builder instance
         */
        default Builder<T, V> defaultValue(V defaultValue) {
            return this.defaultValue((RegistryAccess registries) -> defaultValue);
        }

        /**
         * Set a default value for all attachment holders.
         *
         * @param defaultValueProvider the default value provider
         * @return the builder instance
         */
        Builder<T, V> defaultValue(Function<RegistryAccess, V> defaultValueProvider);

        /**
         * Allow the attachment type to be serialized.
         *
         * @param codec the attachment value codec
         * @return the builder instance
         */
        Builder<T, V> persistent(Codec<V> codec);

        /**
         * Build the attachment type.
         * <p>
         * Can be called multiple times with different inputs.
         *
         * @param resourceLocation the resource location
         * @return the attachment type
         */
        DataAttachmentType<T, V> build(ResourceLocation resourceLocation);
    }

    /**
     * Attachment type builder for multiple possible holder types.
     *
     * @param <T> attachment holder type
     * @param <V> attachment value type
     */
    public interface RegistryBuilder<T, V> extends Builder<T, V> {

        @Override
        default RegistryBuilder<T, V> defaultValue(Function<RegistryAccess, V> defaultValueProvider) {
            return this.defaultValue(Predicates.alwaysTrue(), defaultValueProvider);
        }

        /**
         * Set a default value for the provided holder type.
         *
         * @param holderType   the attachment holder type
         * @param defaultValue the default value
         * @return the builder instance
         */
        default RegistryBuilder<T, V> defaultValue(Class<? extends T> holderType, V defaultValue) {
            return this.defaultValue(holderType::isInstance, defaultValue);
        }

        /**
         * Set a default value for the provided holder type.
         *
         * @param defaultFilter the attachment holder filter
         * @param defaultValue  the default value
         * @return the builder instance
         */
        default RegistryBuilder<T, V> defaultValue(Predicate<T> defaultFilter, V defaultValue) {
            return this.defaultValue(defaultFilter, (RegistryAccess registries) -> defaultValue);
        }

        /**
         * Set a default value for the provided holder type.
         *
         * @param defaultFilter        the attachment holder filter
         * @param defaultValueProvider the default value provider
         * @return the builder instance
         */
        RegistryBuilder<T, V> defaultValue(Predicate<T> defaultFilter, Function<RegistryAccess, V> defaultValueProvider);
    }

    /**
     * Attachment type builder for entities.
     *
     * @param <V> attachment value type
     */
    public interface EntityBuilder<V> extends RegistryBuilder<Entity, V> {

        @Override
        EntityBuilder<V> persistent(Codec<V> codec);

        @Override
        default EntityBuilder<V> defaultValue(V defaultValue) {
            return (EntityBuilder<V>) RegistryBuilder.super.defaultValue(defaultValue);
        }

        @Override
        default EntityBuilder<V> defaultValue(Function<RegistryAccess, V> defaultValueProvider) {
            return (EntityBuilder<V>) RegistryBuilder.super.defaultValue(defaultValueProvider);
        }

        @Override
        default EntityBuilder<V> defaultValue(Class<? extends Entity> holderType, V defaultValue) {
            return (EntityBuilder<V>) RegistryBuilder.super.defaultValue(holderType, defaultValue);
        }

        /**
         * Set a default value for the provided entity type.
         *
         * @param entityType   the entity type
         * @param defaultValue the default value
         * @return the builder instance
         */
        default EntityBuilder<V> defaultValue(EntityType<?> entityType, V defaultValue) {
            return this.defaultValue((Entity entity) -> entity.getType() == entityType, defaultValue);
        }

        @Override
        default EntityBuilder<V> defaultValue(Predicate<Entity> defaultFilter, V defaultValue) {
            return (EntityBuilder<V>) RegistryBuilder.super.defaultValue(defaultFilter, defaultValue);
        }

        @Override
        EntityBuilder<V> defaultValue(Predicate<Entity> defaultFilter, Function<RegistryAccess, V> defaultValueProvider);

        /**
         * Automatically synchronize the attachment value with remotes.
         * <p>
         * The internally used player set is equivalent to {@link PlayerSet#ofPlayer(ServerPlayer)}.
         *
         * @param streamCodec the attachment value stream codec
         * @return the builder instance
         */
        default EntityBuilder<V> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec) {
            return this.networkSynchronized(streamCodec, null);
        }

        /**
         * Automatically synchronize the attachment value with remotes.
         *
         * @param streamCodec            the attachment value stream codec
         * @param synchronizationTargets the player targets to synchronize the attachment value with, usually
         *                               {@link PlayerSet#nearEntity(Entity)}
         * @return the builder instance
         */
        EntityBuilder<V> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec, @Nullable Function<Entity, PlayerSet> synchronizationTargets);

        /**
         * Copy the attachment value when the entity dies.
         * <p>
         * Requires a persistent attachment type via {@link Builder#persistent(Codec)}.
         *
         * @return the builder instance
         */
        EntityBuilder<V> copyOnDeath();
    }

    /**
     * Attachment type builder for block entities.
     *
     * @param <V> attachment value type
     */
    public interface BlockEntityBuilder<V> extends RegistryBuilder<BlockEntity, V> {

        @Override
        default BlockEntityBuilder<V> defaultValue(V defaultValue) {
            return (BlockEntityBuilder<V>) RegistryBuilder.super.defaultValue(defaultValue);
        }

        @Override
        default BlockEntityBuilder<V> defaultValue(Function<RegistryAccess, V> defaultValueProvider) {
            return (BlockEntityBuilder<V>) RegistryBuilder.super.defaultValue(defaultValueProvider);
        }

        @Override
        default BlockEntityBuilder<V> defaultValue(Class<? extends BlockEntity> holderType, V defaultValue) {
            return (BlockEntityBuilder<V>) RegistryBuilder.super.defaultValue(holderType, defaultValue);
        }

        /**
         * Set a default value for the provided block entity type.
         *
         * @param blockEntityType the block entity type
         * @param defaultValue    the default value
         * @return the builder instance
         */
        default BlockEntityBuilder<V> defaultValue(BlockEntityType<?> blockEntityType, V defaultValue) {
            return this.defaultValue((BlockEntity blockEntity) -> blockEntity.getType() == blockEntityType,
                    defaultValue);
        }

        @Override
        default BlockEntityBuilder<V> defaultValue(Predicate<BlockEntity> defaultFilter, V defaultValue) {
            return (BlockEntityBuilder<V>) RegistryBuilder.super.defaultValue(defaultFilter, defaultValue);
        }

        @Override
        BlockEntityBuilder<V> defaultValue(Predicate<BlockEntity> defaultFilter, Function<RegistryAccess, V> defaultValueProvider);

        @Override
        BlockEntityBuilder<V> persistent(Codec<V> codec);
    }
}
