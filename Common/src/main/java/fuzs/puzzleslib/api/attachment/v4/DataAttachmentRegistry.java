package fuzs.puzzleslib.api.attachment.v4;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
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
     * @param <A> attachment value type
     * @return entity attachment type builder
     */
    public static <A> EntityBuilder<A> entityBuilder() {
        return DataAttachmentRegistryImpl.INSTANCE.getEntityTypeBuilder();
    }

    /**
     * @param <A> attachment value type
     * @return block entity attachment type builder
     */
    public static <A> Builder<BlockEntity, A> blockEntityBuilder() {
        return DataAttachmentRegistryImpl.INSTANCE.getBlockEntityTypeBuilder();
    }

    /**
     * @param <A> attachment value type
     * @return level chunk attachment type builder
     */
    public static <A> Builder<LevelChunk, A> levelChunkBuilder() {
        return DataAttachmentRegistryImpl.INSTANCE.getLevelChunkBuilder();
    }

    /**
     * @param <A> attachment value type
     * @return level attachment type builder
     */
    public static <A> Builder<Level, A> levelBuilder() {
        return DataAttachmentRegistryImpl.INSTANCE.getLevelBuilder();
    }

    /**
     * General attachment type builder.
     *
     * @param <T> attachment holder type
     * @param <A> attachment value type
     */
    public interface Builder<T, A> {

        /**
         * Set a default value for all attachment holders.
         *
         * @param defaultValue the default value
         * @return the builder instance
         */
        Builder<T, A> defaultValue(A defaultValue);

        /**
         * Allow the attachment type to be serialized.
         *
         * @param codec the attachment value codec
         * @return the builder instance
         */
        Builder<T, A> persistent(Codec<A> codec);

        /**
         * Build the attachment type.
         * <p>
         * Can be called multiple times with different inputs.
         *
         * @param resourceLocation the resource location
         * @return the attachment type
         */
        DataAttachmentType<T, A> build(ResourceLocation resourceLocation);
    }

    /**
     * Attachment type builder for multiple possible holder types.
     *
     * @param <T> attachment holder type
     * @param <A> attachment value type
     */
    public interface RegistryBuilder<T, A> extends Builder<T, A> {

        @Override
        default RegistryBuilder<T, A> defaultValue(A defaultValue) {
            return this.defaultValue(Predicates.alwaysTrue(), defaultValue);
        }

        /**
         * Set a default value for the provided attachment holder type.
         *
         * @param type         the attachment holder type
         * @param defaultValue the default value
         * @return the builder instance
         */
        default RegistryBuilder<T, A> defaultValue(Class<? extends T> type, A defaultValue) {
            return this.defaultValue(type::isInstance, defaultValue);
        }

        /**
         * Set a default value for the provided attachment holder filter.
         *
         * @param defaultFilter the attachment holder filter
         * @param defaultValue  the default value
         * @return the builder instance
         */
        RegistryBuilder<T, A> defaultValue(Predicate<T> defaultFilter, A defaultValue);
    }

    /**
     * Attachment type builder for entities.
     *
     * @param <A> attachment value type
     */
    public interface EntityBuilder<A> extends RegistryBuilder<Entity, A> {

        @Override
        EntityBuilder<A> persistent(Codec<A> codec);

        @Override
        default EntityBuilder<A> defaultValue(A defaultValue) {
            return (EntityBuilder<A>) RegistryBuilder.super.defaultValue(defaultValue);
        }

        @Override
        default EntityBuilder<A> defaultValue(Class<? extends Entity> type, A defaultValue) {
            return (EntityBuilder<A>) RegistryBuilder.super.defaultValue(type, defaultValue);
        }

        /**
         * Set a default value for the provided entity type.
         *
         * @param type         the entity type
         * @param defaultValue the default value
         * @return the builder instance
         */
        default EntityBuilder<A> defaultValue(EntityType<?> type, A defaultValue) {
            return this.defaultValue((Entity entity) -> entity.getType() == type, defaultValue);
        }

        @Override
        EntityBuilder<A> defaultValue(Predicate<Entity> defaultFilter, A defaultValue);

        /**
         * Automatically synchronize the attachment value with remotes.
         *
         * @param streamCodec the attachment value stream codec
         * @return the builder instance
         */
        default EntityBuilder<A> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, A> streamCodec) {
            return this.networkSynchronized(streamCodec, null);
        }

        /**
         * Automatically synchronize the attachment value with remotes.
         *
         * @param streamCodec            the attachment value stream codec
         * @param synchronizationTargets the player targets to synchronize the attachment value with
         * @return the builder instance
         */
        EntityBuilder<A> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, A> streamCodec, @Nullable Function<Entity, PlayerSet> synchronizationTargets);

        /**
         * Copy the attachment value when the entity dies.
         * <p>
         * Requires a persistent attachment type via {@link #persistent(Codec)}.
         *
         * @return the builder instance
         */
        EntityBuilder<A> copyOnDeath();
    }

    /**
     * Attachment type builder for block entities.
     *
     * @param <A> attachment value type
     */
    public interface BlockEntityBuilder<A> extends RegistryBuilder<BlockEntity, A> {

        @Override
        default BlockEntityBuilder<A> defaultValue(A defaultValue) {
            return (BlockEntityBuilder<A>) RegistryBuilder.super.defaultValue(defaultValue);
        }

        @Override
        default BlockEntityBuilder<A> defaultValue(Class<? extends BlockEntity> type, A defaultValue) {
            return (BlockEntityBuilder<A>) RegistryBuilder.super.defaultValue(type, defaultValue);
        }

        /**
         * Set a default value for the provided block entity type.
         *
         * @param type         the block entity type
         * @param defaultValue the default value
         * @return the builder instance
         */
        default BlockEntityBuilder<A> defaultValue(BlockEntityType<?> type, A defaultValue) {
            return this.defaultValue((BlockEntity blockEntity) -> blockEntity.getType() == type, defaultValue);
        }

        @Override
        BlockEntityBuilder<A> defaultValue(Predicate<BlockEntity> defaultFilter, A defaultValue);

        @Override
        BlockEntityBuilder<A> persistent(Codec<A> codec);
    }
}
