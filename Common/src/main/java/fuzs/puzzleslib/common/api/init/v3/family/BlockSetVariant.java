package fuzs.puzzleslib.common.api.init.v3.family;

import fuzs.puzzleslib.common.impl.init.VanillaBlockSetVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.BlockFamily;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import org.jspecify.annotations.Nullable;

public interface BlockSetVariant extends StringRepresentable {
    BlockSetVariant CHISELED = new VanillaBlockSetVariant.Prefix(BlockFamily.Variant.CHISELED,
            BlockFamily.Builder::chiseled);
    BlockSetVariant CRACKED = new VanillaBlockSetVariant.Prefix(BlockFamily.Variant.CRACKED,
            BlockFamily.Builder::cracked);
    BlockSetVariant CUT = new VanillaBlockSetVariant.Prefix(BlockFamily.Variant.CUT, BlockFamily.Builder::cut);
    BlockSetVariant MOSAIC = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.MOSAIC, BlockFamily.Builder::mosaic);
    BlockSetVariant POLISHED = new VanillaBlockSetVariant.Prefix(BlockFamily.Variant.POLISHED,
            BlockFamily.Builder::polished);
    BlockSetVariant BRICKS = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.BRICKS, BlockFamily.Builder::bricks);
    BlockSetVariant COBBLED = new VanillaBlockSetVariant.Prefix(BlockFamily.Variant.COBBLED,
            BlockFamily.Builder::cobbled);
    BlockSetVariant TILES = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.TILES, BlockFamily.Builder::tiles);
    BlockSetVariant PILLAR = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.PILLAR,
            BlockFamily.Builder::pillar) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride),
                                    RotatedPillarBlock::new,
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value());
                                    }));
        }
    };
    BlockSetVariant LOG = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.LOG, BlockFamily.Builder::log) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride),
                                    RotatedPillarBlock::new,
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .strength(2.0F);
                                    }));
        }
    };
    BlockSetVariant WOOD = new StandaloneBlockSetVariant("wood") {
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix(this.getSerializedName(), baseNameOverride),
                                    RotatedPillarBlock::new,
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .strength(2.0F);
                                    }));
            context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
        }
    };
    BlockSetVariant STRIPPED_LOG = new VanillaBlockSetVariant(BlockFamily.Variant.STRIPPED_LOG,
            BlockFamily.Builder::strippedLog) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride),
                                    RotatedPillarBlock::new,
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .strength(2.0F);
                                    }));
        }

        @Override
        public String getName(BlockSetFamily.Context context, String variantName, @Nullable String baseNameOverride) {
            return context.getName((String baseName) -> "stripped_" + baseName + "_log", baseNameOverride);
        }
    };
    BlockSetVariant STRIPPED_WOOD = new StandaloneBlockSetVariant("stripped_wood") {
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(context.getName((String baseName) -> "stripped_" + baseName + "_wood",
                                    baseNameOverride), RotatedPillarBlock::new, () -> {
                                return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                        .strength(2.0F);
                            }));
            context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
        }
    };
    BlockSetVariant STAIRS = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.STAIRS,
            BlockFamily.Builder::stairs) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new StairBlock(context.getBaseBlock()
                                            .value()
                                            .defaultBlockState(), properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofLegacyCopy(context.getBaseBlock().value());
                                    }));
        }
    };
    BlockSetVariant SLAB = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.SLAB, BlockFamily.Builder::slab) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride),
                                    SlabBlock::new,
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value());
                                    }));
        }
    };
    BlockSetVariant WALL = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.WALL, BlockFamily.Builder::wall) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride),
                                    WallBlock::new,
                                    () -> {
                                        return BlockBehaviour.Properties.ofLegacyCopy(context.getBaseBlock().value())
                                                .forceSolidOn();
                                    }));
        }
    };
    BlockSetVariant FENCE = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.FENCE, BlockFamily.Builder::fence) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride),
                                    FenceBlock::new,
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value());
                                    }));
        }
    };
    BlockSetVariant FENCE_GATE = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.FENCE_GATE,
            BlockFamily.Builder::fenceGate) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new FenceGateBlock(context.getWoodType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .forceSolidOn();
                                    }));
        }
    };
    BlockSetVariant DOOR = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.DOOR, BlockFamily.Builder::door) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new DoorBlock(context.getBlockSetType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .noOcclusion()
                                                .pushReaction(PushReaction.DESTROY);
                                    }));
        }

        @Override
        public void registerItem(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerItem(this,
                    context.getRegistries().registerBlockItem(context.getBlock(this), DoubleHighBlockItem::new));
        }
    };
    BlockSetVariant TRAPDOOR = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.TRAPDOOR,
            BlockFamily.Builder::trapdoor) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new TrapDoorBlock(context.getBlockSetType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .noOcclusion()
                                                .isValidSpawn(Blocks::never);
                                    }));
        }
    };
    BlockSetVariant BUTTON = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.BUTTON,
            BlockFamily.Builder::button) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new ButtonBlock(context.getBlockSetType(),
                                            30,
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .noCollision()
                                                .pushReaction(PushReaction.DESTROY);
                                    }));
        }
    };
    BlockSetVariant PRESSURE_PLATE = new VanillaBlockSetVariant.Suffix(BlockFamily.Variant.PRESSURE_PLATE,
            BlockFamily.Builder::pressurePlate) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new PressurePlateBlock(context.getBlockSetType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .forceSolidOn()
                                                .noCollision()
                                                .pushReaction(PushReaction.DESTROY);
                                    }));
        }
    };
    BlockSetVariant SIGN = new StandaloneBlockSetVariant(BlockFamily.Variant.SIGN) {
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix(this.getSerializedName(), baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new StandingSignBlock(context.getWoodType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .forceSolidOn()
                                                .noCollision();
                                    }));
            Holder<Block> signHolder = context.getBlock(this);
            context.registerBlock(WALL_SIGN,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix(BlockSetVariant.WALL_SIGN.getSerializedName(),
                                            baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new WallSignBlock(context.getWoodType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .overrideLootTable(signHolder.value().getLootTable())
                                                .overrideDescription(signHolder.value().getDescriptionId())
                                                .forceSolidOn()
                                                .noCollision();
                                    }));
            context.registerItem(this,
                    context.getRegistries()
                            .registerBlockItem(signHolder,
                                    (Block block, Item.Properties properties) -> new SignItem(block,
                                            context.getBlock(WALL_SIGN).value(),
                                            properties),
                                    () -> new Item.Properties().stacksTo(16)));
        }
    };
    BlockSetVariant WALL_SIGN = new StandaloneBlockSetVariant(BlockFamily.Variant.WALL_SIGN) {
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            throw new UnsupportedOperationException();
        }
    };
    BlockSetVariant HANGING_SIGN = new StandaloneBlockSetVariant(BlockFamily.Variant.HANGING_SIGN) {
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix(this.getSerializedName(), baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new CeilingHangingSignBlock(context.getWoodType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .forceSolidOn()
                                                .noCollision();
                                    }));
            Holder<Block> hangingSignHolder = context.getBlock(this);
            context.registerBlock(WALL_HANGING_SIGN,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix(BlockSetVariant.WALL_HANGING_SIGN.getSerializedName(),
                                            baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new WallHangingSignBlock(context.getWoodType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .overrideLootTable(hangingSignHolder.value().getLootTable())
                                                .overrideDescription(hangingSignHolder.value().getDescriptionId())
                                                .forceSolidOn()
                                                .noCollision();
                                    }));
            context.registerItem(this,
                    context.getRegistries()
                            .registerBlockItem(hangingSignHolder,
                                    (Block block, Item.Properties properties) -> new HangingSignItem(block,
                                            context.getBlock(WALL_HANGING_SIGN).value(),
                                            properties),
                                    () -> new Item.Properties().stacksTo(16)));
        }
    };
    BlockSetVariant WALL_HANGING_SIGN = new StandaloneBlockSetVariant(BlockFamily.Variant.WALL_HANGING_SIGN) {
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            throw new UnsupportedOperationException();
        }
    };
    BlockSetVariant SHELF = new StandaloneBlockSetVariant("shelf") {
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix(this.getSerializedName(), baseNameOverride),
                                    ShelfBlock::new,
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .sound(SoundType.SHELF);
                                    }));
            context.registerItem(this,
                    context.getRegistries()
                            .registerBlockItem(context.getBlock(this),
                                    () -> new Item.Properties().component(DataComponents.CONTAINER,
                                            ItemContainerContents.EMPTY)));
        }
    };
    BlockSetVariant BOAT = new StandaloneBlockSetVariant("boat") {
        @SuppressWarnings("unchecked")
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerEntityType(this,
                    (Holder.Reference<EntityType<?>>) (Holder.Reference<?>) context.getRegistries()
                            .registerEntityType(context.getNameWithSuffix(this.getSerializedName(), baseNameOverride),
                                    () -> EntityType.Builder.of((EntityType<Boat> entityType, Level level) -> {
                                                return new Boat(entityType, level, () -> context.getItem(this).value());
                                            }, MobCategory.MISC)
                                            .noLootTable()
                                            .sized(1.375F, 0.5625F)
                                            .eyeHeight(0.5625F)
                                            .clientTrackingRange(10)));
            context.registerItem(this,
                    context.getRegistries()
                            .registerItem(context.getNameWithSuffix(this.getSerializedName(), baseNameOverride),
                                    (Item.Properties properties) -> new BoatItem((EntityType<? extends AbstractBoat>) context.getEntityType(
                                            this).value(), properties),
                                    () -> new Item.Properties().stacksTo(1)));
        }
    };
    BlockSetVariant CHEST_BOAT = new StandaloneBlockSetVariant("chest_boat") {
        @SuppressWarnings("unchecked")
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerEntityType(this,
                    (Holder.Reference<EntityType<?>>) (Holder.Reference<?>) context.getRegistries()
                            .registerEntityType(context.getNameWithSuffix(this.getSerializedName(), baseNameOverride),
                                    () -> EntityType.Builder.of((EntityType<ChestBoat> entityType, Level level) -> {
                                                return new ChestBoat(entityType, level, () -> context.getItem(this).value());
                                            }, MobCategory.MISC)
                                            .noLootTable()
                                            .sized(1.375F, 0.5625F)
                                            .eyeHeight(0.5625F)
                                            .clientTrackingRange(10)));
            context.registerItem(this,
                    context.getRegistries()
                            .registerItem(context.getNameWithSuffix(this.getSerializedName(), baseNameOverride),
                                    (Item.Properties properties) -> new BoatItem((EntityType<? extends AbstractBoat>) context.getEntityType(
                                            this).value(), properties),
                                    () -> new Item.Properties().stacksTo(1)));
        }
    };

    void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride);

    BlockFamily.@Nullable Variant toVanilla();
}
