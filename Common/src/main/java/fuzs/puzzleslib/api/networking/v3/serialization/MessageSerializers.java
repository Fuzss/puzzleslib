package fuzs.puzzleslib.api.networking.v3.serialization;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Vector3f;
import fuzs.puzzleslib.impl.networking.serialization.RecordSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Main class for storing {@link MessageSerializer} implementations.
 *
 * <p>This implementation is heavily inspired by and largely based upon <a href="https://github.com/wisp-forest/owo-lib">Owo Lib</a> by <a href="https://github.com/gliscowo">Glisco</a>.
 */
public final class MessageSerializers {
    private static final Map<Class<?>, MessageSerializer<?>> SERIALIZERS = Collections.synchronizedMap(Maps.newHashMap());
    private static final Map<Class<?>, Function<Type[], MessageSerializer<?>>> CONTAINER_PROVIDERS = Collections.synchronizedMap(Maps.newLinkedHashMap());

    private MessageSerializers() {

    }

    /**
     * Register a new {@link MessageSerializer} by providing a {@link net.minecraft.network.FriendlyByteBuf.Writer} and a {@link net.minecraft.network.FriendlyByteBuf.Reader},
     * similarly to vanilla's {@link EntityDataSerializer}
     *
     * @param type type to serialize, inheritance is not supported
     * @param writer writer to byte buffer
     * @param reader reader from byte buffer
     * @param <T> data type
     */
    public static <T> void registerSerializer(Class<T> type, FriendlyByteBuf.Writer<T> writer, FriendlyByteBuf.Reader<T> reader) {
        registerSerializer(type, new MessageSerializerImpl<T>(writer, reader));
    }

    /**
     * Register a serializer for a data type handled by vanilla's registry system.
     *
     * @param type registry content type to serialize
     * @param resourceKey registry resource key
     * @param <T> data type
     */
    @SuppressWarnings("unchecked")
    public static <T> void registerSerializer(Class<? super T> type, ResourceKey<Registry<T>> resourceKey) {
        Registry<T> registry = (Registry<T>) Registry.REGISTRY.get(resourceKey.location());
        Objects.requireNonNull(registry, "Registry for key %s not found".formatted(registry));
        registerSerializer((Class<T>) type, (friendlyByteBuf, t) -> {
            friendlyByteBuf.writeVarInt(registry.getId(t));
        }, friendlyByteBuf -> {
            return registry.byId(friendlyByteBuf.readVarInt());
        });
    }

    private static <T> void registerSerializer(Class<T> type, MessageSerializer<T> value) {
        if (SERIALIZERS.put(type, value) != null) throw new IllegalStateException("Duplicate serializer registered for type %s".formatted(type));
    }

    private static <T> void registerSerializer(Class<T> type, EntityDataSerializer<T> entityDataSerializer) {
        registerSerializer(type, entityDataSerializer::write, entityDataSerializer::read);
    }

    /**
     * Register a custom serializer for container types. Subclasses are supported, meaning e.g. any map implementation will be handled by a provider registered for {@link Map}.
     *
     * <p>All types extending collection are by default deserialized in a {@link LinkedHashSet}. To enable a specific collection type, a unique serializer must be registered.
     * This is already done for {@link List}s, which are deserialized as {@link ArrayList}.
     *
     * @param type container type
     * @param factory new empty collection provider (preferable with pre-configured size)
     * @param <T> container type
     */
    @SuppressWarnings("unchecked")
    public static <T> void registerContainerProvider(Class<T> type, Function<Type[], MessageSerializer<? extends T>> factory) {
        if (CONTAINER_PROVIDERS.put(type, (Function<Type[], MessageSerializer<?>>) (Function<?, ?>) factory) != null) throw new IllegalStateException("Duplicate collection provider registered for type %s".formatted(type));
    }

    @SuppressWarnings("unchecked")
    public static <T> MessageSerializer<T> findByType(Class<T> type) {
        return (MessageSerializer<T>) SERIALIZERS.computeIfAbsent(type, MessageSerializers::computeIfAbsent);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private static <T, E extends Enum<E>> MessageSerializer<T> computeIfAbsent(Class<T> clazz) {
        if (Record.class.isAssignableFrom(clazz)) {
            return (MessageSerializer<T>) RecordSerializer.createRecordSerializer((Class<? extends Record>) clazz);
        } else if (clazz.isArray()) {
            return (MessageSerializer<T>) createArraySerializer(clazz.getComponentType());
        } else if (clazz.isEnum()) {
            return (MessageSerializer<T>) createEnumSerializer((Class<E>) clazz);
        } else {
            throw new RuntimeException("Missing serializer for type %s".formatted(clazz));
        }
    }

    @ApiStatus.Internal
    public static MessageSerializer<?> findByGenericType(Type type) {

        if (type instanceof Class<?> clazz) {
            return findByType(clazz);
        }

        ParameterizedType parameterizedType = (ParameterizedType) type;
        Class<?> clazz = (Class<?>) parameterizedType.getRawType();
        Type[] typeArguments = parameterizedType.getActualTypeArguments();

        for (Map.Entry<Class<?>, Function<Type[], MessageSerializer<?>>> entry : CONTAINER_PROVIDERS.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) return entry.getValue().apply(typeArguments);
        }

        // fallback for collections, don't include in providers to allow for handling more concrete collection implementations
        if (Collection.class.isAssignableFrom(clazz)) {
            return createCollectionSerializer(typeArguments, Sets::newLinkedHashSetWithExpectedSize);
        }

        return findByType(clazz);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> MessageSerializer<Map<K, V>> createMapSerializer(Type[] typeArguments) {
        MessageSerializer<K> keySerializer = findByType((Class<K>) typeArguments[0]);
        MessageSerializer<V> valueSerializer = findByType((Class<V>) typeArguments[1]);
        return new MessageSerializerImpl<>((friendlyByteBuf, o) -> {
            friendlyByteBuf.writeMap(o, keySerializer::write, valueSerializer::write);
        }, friendlyByteBuf -> {
            return friendlyByteBuf.readMap(keySerializer::read, valueSerializer::read);
        });
    }

    @SuppressWarnings("unchecked")
    private static <T, C extends Collection<T>> MessageSerializer<C> createCollectionSerializer(Type[] typeArguments, IntFunction<C> factory) {
        MessageSerializer<T> serializer = findByType((Class<T>) typeArguments[0]);
        return new MessageSerializerImpl<>((friendlyByteBuf, o) -> {
            friendlyByteBuf.writeCollection(o, serializer::write);
        }, friendlyByteBuf -> {
            return friendlyByteBuf.readCollection(factory, serializer::read);
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> MessageSerializer<Optional<T>> createOptionalSerializer(Type[] typeArguments) {
        MessageSerializer<T> serializer = findByType((Class<T>) typeArguments[0]);
        return new MessageSerializerImpl<>((friendlyByteBuf, o) -> {
            friendlyByteBuf.writeOptional(o, serializer::write);
        }, friendlyByteBuf -> {
            return friendlyByteBuf.readOptional(serializer::read);
        });
    }

    @SuppressWarnings("unchecked")
    private static <L, R> MessageSerializer<Either<L, R>> createEitherSerializer(Type[] typeArguments) {
        MessageSerializer<L> leftSerializer = findByType((Class<L>) typeArguments[0]);
        MessageSerializer<R> rightSerializer = findByType((Class<R>) typeArguments[1]);
        return new MessageSerializerImpl<>((friendlyByteBuf, o) -> {
            friendlyByteBuf.writeEither(o, leftSerializer::write, rightSerializer::write);
        }, friendlyByteBuf -> {
            return friendlyByteBuf.readEither(leftSerializer::read, rightSerializer::read);
        });
    }

    @SuppressWarnings("unchecked")
    private static MessageSerializer<?> createArraySerializer(Class<?> clazz) {
        MessageSerializer<Object> serializer = (MessageSerializer<Object>) findByType(clazz);
        return new MessageSerializerImpl<>((buf, t) -> {
            final int length = Array.getLength(t);
            buf.writeVarInt(length);
            for (int i = 0; i < length; i++) {
                serializer.write(buf, Array.get(t, i));
            }
        }, buf -> {
            final int length = buf.readVarInt();
            Object array = Array.newInstance(clazz, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, serializer.read(buf));
            }
            return array;
        });
    }

    private static <E extends Enum<E>> MessageSerializer<E> createEnumSerializer(Class<E> clazz) {
        return new MessageSerializerImpl<>(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(clazz));
    }

    private record MessageSerializerImpl<T>(FriendlyByteBuf.Writer<T> writer, FriendlyByteBuf.Reader<T> reader) implements MessageSerializer<T> {

        @Override
        public void write(FriendlyByteBuf buf, T instance) {
            this.writer.accept(buf, instance);
        }

        @Override
        public T read(FriendlyByteBuf buf) {
            return this.reader.apply(buf);
        }
    }

    static {
        registerSerializer(boolean.class, FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean);
        registerSerializer(Boolean.class, FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean);
        registerSerializer(int.class, FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt);
        registerSerializer(Integer.class, FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt);
        registerSerializer(long.class, FriendlyByteBuf::writeVarLong, FriendlyByteBuf::readVarLong);
        registerSerializer(Long.class, FriendlyByteBuf::writeVarLong, FriendlyByteBuf::readVarLong);
        registerSerializer(float.class, FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat);
        registerSerializer(Float.class, FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat);
        registerSerializer(double.class, FriendlyByteBuf::writeDouble, FriendlyByteBuf::readDouble);
        registerSerializer(Double.class, FriendlyByteBuf::writeDouble, FriendlyByteBuf::readDouble);
        registerSerializer(byte.class, (FriendlyByteBuf.Writer<Byte>) FriendlyByteBuf::writeByte, FriendlyByteBuf::readByte);
        registerSerializer(Byte.class, (FriendlyByteBuf.Writer<Byte>) FriendlyByteBuf::writeByte, FriendlyByteBuf::readByte);
        registerSerializer(short.class, (FriendlyByteBuf.Writer<Short>) FriendlyByteBuf::writeShort, FriendlyByteBuf::readShort);
        registerSerializer(Short.class, (FriendlyByteBuf.Writer<Short>) FriendlyByteBuf::writeShort, FriendlyByteBuf::readShort);
        registerSerializer(char.class, (FriendlyByteBuf.Writer<Character>) FriendlyByteBuf::writeChar, FriendlyByteBuf::readChar);
        registerSerializer(Character.class, (FriendlyByteBuf.Writer<Character>) FriendlyByteBuf::writeChar, FriendlyByteBuf::readChar);

        registerSerializer(String.class, EntityDataSerializers.STRING);
        registerSerializer(Date.class, FriendlyByteBuf::writeDate, FriendlyByteBuf::readDate);
        registerSerializer(Instant.class, FriendlyByteBuf::writeInstant, FriendlyByteBuf::readInstant);
        registerSerializer(UUID.class, FriendlyByteBuf::writeUUID, FriendlyByteBuf::readUUID);

        registerSerializer(Component.class, EntityDataSerializers.COMPONENT);
        registerSerializer(ItemStack.class, EntityDataSerializers.ITEM_STACK);
        registerSerializer(Rotations.class, EntityDataSerializers.ROTATIONS);
        registerSerializer(BlockPos.class, EntityDataSerializers.BLOCK_POS);
        registerSerializer(Direction.class, EntityDataSerializers.DIRECTION);
        registerSerializer(CompoundTag.class, EntityDataSerializers.COMPOUND_TAG);
        registerSerializer(ParticleOptions.class, EntityDataSerializers.PARTICLE);
        registerSerializer(VillagerData.class, EntityDataSerializers.VILLAGER_DATA);
        registerSerializer(Pose.class, EntityDataSerializers.POSE);
        registerSerializer(ChunkPos.class, FriendlyByteBuf::writeChunkPos, FriendlyByteBuf::readChunkPos);
        registerSerializer(ResourceLocation.class, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::readResourceLocation);
        registerSerializer(BlockHitResult.class, FriendlyByteBuf::writeBlockHitResult, FriendlyByteBuf::readBlockHitResult);
        registerSerializer(BitSet.class, FriendlyByteBuf::writeBitSet, FriendlyByteBuf::readBitSet);
        registerSerializer(GameProfile.class, FriendlyByteBuf::writeGameProfile, FriendlyByteBuf::readGameProfile);

        registerSerializer(Vec3.class, (friendlyByteBuf, vec3) -> {
            friendlyByteBuf.writeDouble(vec3.x());
            friendlyByteBuf.writeDouble(vec3.y());
            friendlyByteBuf.writeDouble(vec3.z());
        }, friendlyByteBuf -> {
            return new Vec3(friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble());
        });
        registerSerializer(Vector3f.class, (friendlyByteBuf, vec3) -> {
            friendlyByteBuf.writeFloat(vec3.x());
            friendlyByteBuf.writeFloat(vec3.y());
            friendlyByteBuf.writeFloat(vec3.z());
        }, friendlyByteBuf -> {
            return new Vector3f(friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat());
        });
        registerSerializer(FriendlyByteBuf.class, (buf, other) -> {
            buf.writeVarInt(other.readableBytes());
            buf.writeBytes(other);
            buf.release();
        }, buf -> new FriendlyByteBuf(buf.readBytes(buf.readVarInt())));

        registerSerializer(SoundEvent.class, Registry.SOUND_EVENT_REGISTRY);
        registerSerializer(Fluid.class, Registry.FLUID_REGISTRY);
        registerSerializer(MobEffect.class, Registry.MOB_EFFECT_REGISTRY);
        registerSerializer(Block.class, Registry.BLOCK_REGISTRY);
        registerSerializer(Enchantment.class, Registry.ENCHANTMENT_REGISTRY);
        registerSerializer(EntityType.class, Registry.ENTITY_TYPE_REGISTRY);
        registerSerializer(Item.class, Registry.ITEM_REGISTRY);
        registerSerializer(Potion.class, Registry.POTION_REGISTRY);
        registerSerializer(ParticleType.class, Registry.PARTICLE_TYPE_REGISTRY);
        registerSerializer(BlockEntityType.class, Registry.BLOCK_ENTITY_TYPE_REGISTRY);
        registerSerializer(MenuType.class, Registry.MENU_REGISTRY);
        registerSerializer(Attribute.class, Registry.ATTRIBUTE_REGISTRY);
        registerSerializer(GameEvent.class, Registry.GAME_EVENT_REGISTRY);
        registerSerializer(VillagerType.class, Registry.VILLAGER_TYPE_REGISTRY);
        registerSerializer(VillagerProfession.class, Registry.VILLAGER_PROFESSION_REGISTRY);
        registerSerializer(PoiType.class, Registry.POINT_OF_INTEREST_TYPE_REGISTRY);
        registerSerializer(DimensionType.class, Registry.DIMENSION_TYPE_REGISTRY);
        registerSerializer(Level.class, Registry.DIMENSION_REGISTRY);
        registerSerializer(Feature.class, Registry.FEATURE_REGISTRY);
        registerSerializer(WorldPreset.class, Registry.WORLD_PRESET_REGISTRY);
        registerSerializer(Biome.class, Registry.BIOME_REGISTRY);

        registerContainerProvider(Map.class, MessageSerializers::createMapSerializer);
        registerContainerProvider(List.class, typeArguments -> createCollectionSerializer(typeArguments, Lists::newArrayListWithExpectedSize));
        registerContainerProvider(Optional.class, MessageSerializers::createOptionalSerializer);
        registerContainerProvider(Either.class, MessageSerializers::createEitherSerializer);
    }
}
