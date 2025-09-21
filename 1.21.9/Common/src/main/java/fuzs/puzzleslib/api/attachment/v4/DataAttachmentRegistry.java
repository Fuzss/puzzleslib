package fuzs.puzzleslib.api.attachment.v4;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;

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
    public static <V> LevelChunkBuilder<V> levelChunkBuilder() {
        return DataAttachmentRegistryImpl.INSTANCE.getLevelChunkBuilder();
    }

    /**
     * @param <V> attachment value type
     * @return the level attachment type builder
     */
    public static <V> LevelBuilder<V> levelBuilder() {
        return DataAttachmentRegistryImpl.INSTANCE.getLevelBuilder();
    }

    /**
     * General attachment type builder.
     *
     * @param <T> attachment holder type
     * @param <V> attachment value type
     */
    public interface Builder<T, V, B extends Builder<T, V, B>> {

        /**
         * @return the builder instance
         */
        B getThis();

        /**
         * Set a default value for all attachment holders.
         *
         * @param defaultValue the default value
         * @return the builder instance
         */
        default B defaultValue(V defaultValue) {
            return this.defaultValue((RegistryAccess registries) -> defaultValue);
        }

        /**
         * Set a default value for all attachment holders.
         *
         * @param defaultValueProvider the default value provider
         * @return the builder instance
         */
        B defaultValue(Function<RegistryAccess, V> defaultValueProvider);

        /**
         * Allow the attachment type to be serialised.
         *
         * @param codec the attachment value codec
         * @return the builder instance
         */
        B persistent(Codec<V> codec);

        /**
         * Automatically synchronise the attachment value with remotes.
         *
         * @param streamCodec            the attachment value stream codec
         * @param synchronizationTargets the remotes to synchronise the attachment value with; the following are
         *                               recommended:
         *                               <ul>
         *                               <li>{@link PlayerSet#ofEntity(Entity)}</li>
         *                               <li>{@link PlayerSet#nearEntity(Entity)}</li>
         *                               <li>{@link PlayerSet#nearBlockEntity(BlockEntity)}</li>
         *                               <li>{@link PlayerSet#nearChunk(LevelChunk)}</li>
         *                               <li>{@link PlayerSet#inLevel(ServerLevel)}</li>
         *                               </ul>
         * @return the builder instance
         */
        B networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec, Function<T, PlayerSet> synchronizationTargets);

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
    public interface RegistryBuilder<T, V, B extends RegistryBuilder<T, V, B>> extends Builder<T, V, B> {

        @Override
        default B defaultValue(Function<RegistryAccess, V> defaultValueProvider) {
            return this.defaultValue(Predicates.alwaysTrue(), defaultValueProvider);
        }

        /**
         * Set a default value for the provided holder type.
         *
         * @param holderType   the attachment holder type
         * @param defaultValue the default value
         * @return the builder instance
         */
        default B defaultValue(Class<? extends T> holderType, V defaultValue) {
            return this.defaultValue(holderType::isInstance, defaultValue);
        }

        /**
         * Set a default value for the provided holder type.
         *
         * @param defaultFilter the attachment holder filter
         * @param defaultValue  the default value
         * @return the builder instance
         */
        default B defaultValue(Predicate<T> defaultFilter, V defaultValue) {
            return this.defaultValue(defaultFilter, (RegistryAccess registries) -> defaultValue);
        }

        /**
         * Set a default value for the provided holder type.
         *
         * @param defaultFilter        the attachment holder filter
         * @param defaultValueProvider the default value provider
         * @return the builder instance
         */
        B defaultValue(Predicate<T> defaultFilter, Function<RegistryAccess, V> defaultValueProvider);
    }

    /**
     * Attachment type builder for entities.
     *
     * @param <V> attachment value type
     */
    public interface EntityBuilder<V> extends RegistryBuilder<Entity, V, EntityBuilder<V>> {

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
    public interface BlockEntityBuilder<V> extends RegistryBuilder<BlockEntity, V, BlockEntityBuilder<V>> {

        /**
         * Set a default value for the provided block entity type.
         *
         * @param blockEntityType the block entity type
         * @param defaultValue    the default value
         * @return the builder instance
         */
        default BlockEntityBuilder<V> defaultValue(BlockEntityType<?> blockEntityType, V defaultValue) {
            return this.defaultValue((BlockEntity blockEntity) -> {
                return blockEntity.getType() == blockEntityType;
            }, defaultValue);
        }
    }

    /**
     * Attachment type builder for chunks.
     *
     * @param <V> attachment value type
     */
    public interface LevelChunkBuilder<V> extends Builder<LevelChunk, V, LevelChunkBuilder<V>> {

    }

    /**
     * Attachment type builder for levels.
     *
     * @param <V> attachment value type
     */
    public interface LevelBuilder<V> extends Builder<Level, V, LevelBuilder<V>> {

    }
}
