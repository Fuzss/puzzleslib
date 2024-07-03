package fuzs.puzzleslib.api.network.v3.serialization;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.network.serialization.RecordSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Main class for storing {@link MessageSerializer} implementations.
 *
 * <p>This implementation is heavily inspired by and largely based upon <a href="https://github.com/wisp-forest/owo-lib">Owo Lib</a> by <a href="https://github.com/gliscowo">Glisco</a>.
 */
public final class MessageSerializers {
    private static final Map<Class<?>, MessageSerializer<?>> SERIALIZERS = Collections.synchronizedMap(Maps.newIdentityHashMap());
    private static final Map<Class<?>, Function<Type[], MessageSerializer<?>>> CONTAINER_PROVIDERS = Collections.synchronizedMap(Maps.newLinkedHashMap());

    private MessageSerializers() {
        // NO-OP
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
    public static <T> void registerSerializer(Class<T> type, BiConsumer<FriendlyByteBuf, T> writer, Function<FriendlyByteBuf, T> reader) {
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
        Registry<T> registry;
        if (BuiltInRegistries.REGISTRY.containsKey(resourceKey.location())) {
            registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(resourceKey.location());
        } else {
            registry = null;
        }
        Objects.requireNonNull(registry, () -> "Registry for key %s not found".formatted(resourceKey.location()));
        registerSerializer((Class<T>) type, (FriendlyByteBuf friendlyByteBuf, T t) -> {
            friendlyByteBuf.writeVarInt(registry.getId(t));
        }, (FriendlyByteBuf friendlyByteBuf) -> {
            return registry.byId(friendlyByteBuf.readVarInt());
        });
    }

    private static <T> void registerSerializer(Class<T> type, MessageSerializer<T> value) {
        if (SERIALIZERS.put(type, value) != null) {
            PuzzlesLib.LOGGER.warn("Overriding serializer registered for type {}", type);
        }
    }

    private static <T> void registerSerializer(Class<T> type, EntityDataSerializer<T> entityDataSerializer) {
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec = entityDataSerializer.codec();
        registerSerializer(type, (friendlyByteBuf, t) -> codec.encode(friendlyByteBuf, t), friendlyByteBuf -> codec.decode(friendlyByteBuf));
    }

    private static <T> void registerSerializer(Class<T> type, StreamCodec<ByteBuf, T> streamCodec) {
        registerSerializer(type, streamCodec::encode, streamCodec::decode);
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
        if (CONTAINER_PROVIDERS.put(type, (Function<Type[], MessageSerializer<?>>) (Function<?, ?>) factory) != null) {
            PuzzlesLib.LOGGER.warn("Overriding collection provider registered for type {}", type);
        }
    }

    /**
     * Find a serializer for a given type. Some serializers (for records, arrays, and enums) can be created dynamically.
     *
     * @param type serializable type
     * @param <T>  data type
     * @return serializer for this type
     */
    @SuppressWarnings("unchecked")
    public static <T> MessageSerializer<T> findByType(Class<T> type) {
        // don't use Map::computeIfAbsent on map, it will throw java.lang.ConcurrentModificationException as during the Map::computeIfAbsent for the main record type
        // more calls to MessageSerializers::getByType can happen when the record contains other records / enums / arrays as fields
        MessageSerializer<T> serializer = (MessageSerializer<T>) SERIALIZERS.get(type);
        if (serializer == null) {
            serializer = computeIfAbsent(type);
            SERIALIZERS.put(type, serializer);
        }
        return serializer;
    }

    @SuppressWarnings("unchecked")
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
        return new MessageSerializerImpl<>((FriendlyByteBuf friendlyByteBuf, Map<K, V> o) -> {
            friendlyByteBuf.writeMap(o, keySerializer::write, valueSerializer::write);
        }, (FriendlyByteBuf friendlyByteBuf) -> {
            return friendlyByteBuf.readMap(keySerializer::read, valueSerializer::read);
        });
    }

    @SuppressWarnings("unchecked")
    private static <T, C extends Collection<T>> MessageSerializer<C> createCollectionSerializer(Type[] typeArguments, IntFunction<C> factory) {
        MessageSerializer<T> serializer = findByType((Class<T>) typeArguments[0]);
        return new MessageSerializerImpl<>((FriendlyByteBuf friendlyByteBuf, C o) -> {
            friendlyByteBuf.writeCollection(o, serializer::write);
        }, (FriendlyByteBuf friendlyByteBuf) -> {
            return friendlyByteBuf.readCollection(factory, serializer::read);
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> MessageSerializer<Optional<T>> createOptionalSerializer(Type[] typeArguments) {
        MessageSerializer<T> serializer = findByType((Class<T>) typeArguments[0]);
        return new MessageSerializerImpl<>((FriendlyByteBuf friendlyByteBuf, Optional<T> o) -> {
            friendlyByteBuf.writeOptional(o, serializer::write);
        }, (FriendlyByteBuf friendlyByteBuf) -> {
            return friendlyByteBuf.readOptional(serializer::read);
        });
    }

    @SuppressWarnings("unchecked")
    private static MessageSerializer<?> createArraySerializer(Class<?> clazz) {
        MessageSerializer<Object> serializer = (MessageSerializer<Object>) findByType(clazz);
        return new MessageSerializerImpl<>((FriendlyByteBuf buf, Object t) -> {
            final int length = Array.getLength(t);
            buf.writeVarInt(length);
            for (int i = 0; i < length; i++) {
                serializer.write(buf, Array.get(t, i));
            }
        }, (FriendlyByteBuf buf) -> {
            final int length = buf.readVarInt();
            Object array = Array.newInstance(clazz, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, serializer.read(buf));
            }
            return array;
        });
    }

    private static <E extends Enum<E>> MessageSerializer<E> createEnumSerializer(Class<E> clazz) {
        return new MessageSerializerImpl<>(FriendlyByteBuf::writeEnum, (FriendlyByteBuf buf) -> buf.readEnum(clazz));
    }

    private record MessageSerializerImpl<T>(BiConsumer<FriendlyByteBuf, T> writer, Function<FriendlyByteBuf, T> reader) implements MessageSerializer<T> {

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
        registerSerializer(byte.class, (BiConsumer<FriendlyByteBuf, Byte>) FriendlyByteBuf::writeByte, FriendlyByteBuf::readByte);
        registerSerializer(Byte.class, (BiConsumer<FriendlyByteBuf, Byte>) FriendlyByteBuf::writeByte, FriendlyByteBuf::readByte);
        registerSerializer(short.class, (BiConsumer<FriendlyByteBuf, Short>) FriendlyByteBuf::writeShort, FriendlyByteBuf::readShort);
        registerSerializer(Short.class, (BiConsumer<FriendlyByteBuf, Short>) FriendlyByteBuf::writeShort, FriendlyByteBuf::readShort);
        registerSerializer(char.class, (BiConsumer<FriendlyByteBuf, Character>) FriendlyByteBuf::writeChar, FriendlyByteBuf::readChar);
        registerSerializer(Character.class, (BiConsumer<FriendlyByteBuf, Character>) FriendlyByteBuf::writeChar, FriendlyByteBuf::readChar);

        registerSerializer(String.class, EntityDataSerializers.STRING);
        registerSerializer(Date.class, FriendlyByteBuf::writeDate, FriendlyByteBuf::readDate);
        registerSerializer(Instant.class, FriendlyByteBuf::writeInstant, FriendlyByteBuf::readInstant);
        registerSerializer(UUID.class, (FriendlyByteBuf friendlyByteBuf, UUID uuid) -> {
            friendlyByteBuf.writeUUID(uuid);
        }, (FriendlyByteBuf friendlyByteBuf) -> friendlyByteBuf.readUUID());

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
        registerSerializer(GameProfile.class, ByteBufCodecs.GAME_PROFILE);

        registerSerializer(Vec3.class, (FriendlyByteBuf friendlyByteBuf, Vec3 vec3) -> {
            friendlyByteBuf.writeDouble(vec3.x());
            friendlyByteBuf.writeDouble(vec3.y());
            friendlyByteBuf.writeDouble(vec3.z());
        }, (FriendlyByteBuf friendlyByteBuf) -> {
            return new Vec3(friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble());
        });
        registerSerializer(Vector3f.class, (FriendlyByteBuf friendlyByteBuf, Vector3f vec3) -> {
            friendlyByteBuf.writeFloat(vec3.x());
            friendlyByteBuf.writeFloat(vec3.y());
            friendlyByteBuf.writeFloat(vec3.z());
        }, (FriendlyByteBuf friendlyByteBuf) -> {
            return new Vector3f(friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat());
        });
        registerSerializer(FriendlyByteBuf.class, (FriendlyByteBuf newBuf, FriendlyByteBuf buf) -> {
            newBuf.writeBytes(buf.copy());
            buf.release();
        }, (FriendlyByteBuf buf) -> {
            FriendlyByteBuf newBuf = new FriendlyByteBuf(Unpooled.buffer());
            newBuf.writeBytes(buf.copy());
            buf.skipBytes(buf.readableBytes());
            return newBuf;
        });

        registerSerializer(SoundEvent.class, Registries.SOUND_EVENT);
        registerSerializer(Fluid.class, Registries.FLUID);
        registerSerializer(MobEffect.class, Registries.MOB_EFFECT);
        registerSerializer(Block.class, Registries.BLOCK);
        registerSerializer(Enchantment.class, Registries.ENCHANTMENT);
        registerSerializer(EntityType.class, Registries.ENTITY_TYPE);
        registerSerializer(Item.class, Registries.ITEM);
        registerSerializer(Potion.class, Registries.POTION);
        registerSerializer(ParticleType.class, Registries.PARTICLE_TYPE);
        registerSerializer(BlockEntityType.class, Registries.BLOCK_ENTITY_TYPE);
        registerSerializer(MenuType.class, Registries.MENU);
        registerSerializer(Attribute.class, Registries.ATTRIBUTE);
        registerSerializer(GameEvent.class, Registries.GAME_EVENT);
        registerSerializer(VillagerType.class, Registries.VILLAGER_TYPE);
        registerSerializer(VillagerProfession.class, Registries.VILLAGER_PROFESSION);
        registerSerializer(PoiType.class, Registries.POINT_OF_INTEREST_TYPE);

        registerContainerProvider(Map.class, MessageSerializers::createMapSerializer);
        registerContainerProvider(List.class, (Type[] typeArguments) -> createCollectionSerializer(typeArguments, Lists::newArrayListWithExpectedSize));
        registerContainerProvider(Optional.class, MessageSerializers::createOptionalSerializer);
    }
}
