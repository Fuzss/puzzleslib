package fuzs.puzzleslib.api.init.v3.family;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.context.GameplayContentContext;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.impl.init.BlockSetFamilyRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface BlockSetFamily {
    /**
     * @see #registerFor(BiConsumer, Map)
     */
    Map<BlockSetVariant, Holder<BlockEntityType<?>>> VARIANT_BLOCK_ENTITY_TYPE = ImmutableMap.of(BlockSetVariant.SIGN,
            BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityType.SIGN),
            BlockSetVariant.WALL_SIGN,
            BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityType.SIGN),
            BlockSetVariant.HANGING_SIGN,
            BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityType.HANGING_SIGN),
            BlockSetVariant.WALL_HANGING_SIGN,
            BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityType.HANGING_SIGN),
            BlockSetVariant.SHELF,
            BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityType.SHELF));
    /**
     * @see #registerFor(GameplayContentContext, Map)
     */
    Map<BlockSetVariant, Vector2ic> VARIANT_WOODEN_FLAMMABLE = ImmutableMap.of(BlockSetVariant.STAIRS,
            new Vector2i(5, 20),
            BlockSetVariant.SLAB,
            new Vector2i(5, 20),
            BlockSetVariant.FENCE,
            new Vector2i(5, 20),
            BlockSetVariant.FENCE_GATE,
            new Vector2i(5, 20),
            BlockSetVariant.SHELF,
            new Vector2i(30, 20));
    /**
     * @see #registerFor(Map)
     */
    @SuppressWarnings("unchecked")
    Map<BlockSetVariant, Function<Holder<EntityType<?>>, DispenseItemBehavior>> VARIANT_DISPENSE_BEHAVIOR = ImmutableMap.of(
            BlockSetVariant.BOAT,
            (Holder<EntityType<?>> holder) -> {
                return new BoatDispenseItemBehavior((EntityType<? extends AbstractBoat>) holder.value());
            },
            BlockSetVariant.CHEST_BOAT,
            (Holder<EntityType<?>> holder) -> {
                return new BoatDispenseItemBehavior((EntityType<? extends AbstractBoat>) holder.value());
            });

    static Writable base(RegistryManager registries, Holder.Reference<Block> baseBlock, String basePath) {
        BlockSetType blockSetType = new BlockSetType(registries.makeKey(basePath).toString());
        WoodType woodType = new WoodType(registries.makeKey(basePath).toString(), blockSetType);
        return new BlockSetFamilyRegistrar(registries, baseBlock, basePath, blockSetType, woodType);
    }

    static Writable any(RegistryManager registries, Holder.Reference<Block> baseBlock, String basePath) {
        return base(registries, baseBlock, basePath).generateFor(BlockSetVariant.STAIRS)
                .generateFor(BlockSetVariant.SLAB)
                .generateFor(BlockSetVariant.WALL);
    }

    static Writable metal(RegistryManager registries, Holder.Reference<Block> baseBlock, String basePath) {
        return base(registries, baseBlock, basePath).generateFor(BlockSetVariant.STAIRS)
                .generateFor(BlockSetVariant.SLAB)
                .generateFor(BlockSetVariant.DOOR)
                .generateFor(BlockSetVariant.TRAPDOOR)
                .generateFor(BlockSetVariant.PRESSURE_PLATE);
    }

    static Writable wooden(RegistryManager registries, Holder.Reference<Block> baseBlock, String basePath) {
        return base(registries, baseBlock, basePath).configureBlockFamily((BlockFamily.Builder blockFamily) -> {
                    blockFamily.recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks");
                })
                .generateFor(BlockSetVariant.STAIRS)
                .generateFor(BlockSetVariant.SLAB)
                .generateFor(BlockSetVariant.FENCE)
                .generateFor(BlockSetVariant.FENCE_GATE)
                .generateFor(BlockSetVariant.DOOR)
                .generateFor(BlockSetVariant.TRAPDOOR)
                .generateFor(BlockSetVariant.PRESSURE_PLATE)
                .generateFor(BlockSetVariant.BUTTON)
                .generateFor(BlockSetVariant.SIGN)
                .generateFor(BlockSetVariant.HANGING_SIGN)
                .generateFor(BlockSetVariant.SHELF)
                .generateFor(BlockSetVariant.BOAT)
                .generateFor(BlockSetVariant.CHEST_BOAT);
    }

    Holder.Reference<Block> getBaseBlock();

    BlockSetType getBlockSetType();

    WoodType getWoodType();

    BlockFamily getBlockFamily();

    Map<BlockSetVariant, Holder.Reference<Block>> getBlockVariants();

    Map<BlockSetVariant, Holder.Reference<Item>> getItemVariants();

    Map<BlockSetVariant, Holder.Reference<EntityType<?>>> getEntityVariants();

    default Holder.Reference<Block> getBlock(BlockSetVariant variant) {
        return this.getBlockVariants().get(variant);
    }

    default Holder.Reference<Item> getItem(BlockSetVariant variant) {
        return this.getItemVariants().get(variant);
    }

    default Holder.Reference<EntityType<?>> getEntityType(BlockSetVariant variant) {
        return this.getEntityVariants().get(variant);
    }

    default void register() {
        BlockSetType.register(this.getBlockSetType());
        WoodType.register(this.getWoodType());
    }

    default void registerFor(BiConsumer<BlockEntityType<?>, Block> consumer, Map<BlockSetVariant, Holder<BlockEntityType<?>>> variants) {
        this.getBlockVariants().forEach((BlockSetVariant variant, Holder.Reference<Block> holder) -> {
            Holder<BlockEntityType<?>> blockEntity = variants.get(variant);
            if (blockEntity != null) {
                consumer.accept(blockEntity.value(), holder.value());
            }
        });
    }

    default void registerFor(GameplayContentContext context, Map<BlockSetVariant, Vector2ic> variants) {
        this.getBlockVariants().forEach((BlockSetVariant variant, Holder.Reference<Block> holder) -> {
            Vector2ic flammable = variants.get(variant);
            if (flammable != null) {
                context.registerFlammable(holder, flammable.x(), flammable.y());
            }
        });
    }

    default void registerFor(Map<BlockSetVariant, Function<Holder<EntityType<?>>, DispenseItemBehavior>> variants) {
        this.getEntityVariants().forEach((BlockSetVariant variant, Holder.Reference<EntityType<?>> holder) -> {
            Function<Holder<EntityType<?>>, DispenseItemBehavior> behaviorFactory = variants.get(variant);
            if (behaviorFactory != null) {
                DispenserBlock.registerBehavior(this.getItem(variant).value(), behaviorFactory.apply(holder));
            }
        });
    }

    interface Writable extends BlockSetFamily {
        Writable generateFor(BlockSetVariant variant);

        Writable configureBlockFamily(Consumer<BlockFamily.Builder> blockFamilyConsumer);
    }

    interface Context extends BlockSetFamily {
        String getName(UnaryOperator<String> name);

        default String getNameWithPrefix(String prefix) {
            return this.getName((String string) -> prefix + "_" + string);
        }

        default String getNameWithSuffix(String suffix) {
            return this.getName((String string) -> string + "_" + suffix);
        }

        RegistryManager getRegistries();

        void registerBlock(BlockSetVariant variant, Holder.Reference<Block> holder);

        void registerItem(BlockSetVariant variant, Holder.Reference<Item> holder);

        void registerEntityType(BlockSetVariant variant, Holder.Reference<EntityType<?>> holder);
    }
}
