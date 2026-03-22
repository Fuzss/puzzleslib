package fuzs.puzzleslib.api.init.v3.family;

import fuzs.puzzleslib.impl.init.VanillaBlockSetVariant;
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
    BlockSetVariant CHISELED = new VanillaBlockSetVariant.Direct(BlockFamily.Variant.CHISELED,
            BlockFamily.Builder::chiseled);
    BlockSetVariant CRACKED = new VanillaBlockSetVariant.Direct(BlockFamily.Variant.CRACKED,
            BlockFamily.Builder::cracked);
    BlockSetVariant POLISHED = new VanillaBlockSetVariant.Direct(BlockFamily.Variant.POLISHED,
            BlockFamily.Builder::polished);
    BlockSetVariant CUT = new VanillaBlockSetVariant.Direct(BlockFamily.Variant.CUT, BlockFamily.Builder::cut);
    BlockSetVariant MOSAIC = new VanillaBlockSetVariant.Direct(BlockFamily.Variant.MOSAIC, BlockFamily.Builder::mosaic);
    BlockSetVariant STAIRS = new VanillaBlockSetVariant(BlockFamily.Variant.STAIRS, BlockFamily.Builder::stairs) {
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix("stairs"),
                                    (BlockBehaviour.Properties properties) -> new StairBlock(context.getBaseBlock()
                                            .value()
                                            .defaultBlockState(), properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofLegacyCopy(context.getBaseBlock().value());
                                    }));
            context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
        }
    };
    BlockSetVariant SLAB = new VanillaBlockSetVariant(BlockFamily.Variant.SLAB, BlockFamily.Builder::slab) {
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerBlock(this,
                    context.getRegistries().registerBlock(context.getNameWithSuffix("slab"), SlabBlock::new, () -> {
                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value());
                    }));
            context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
        }
    };
    BlockSetVariant WALL = new VanillaBlockSetVariant(BlockFamily.Variant.WALL, BlockFamily.Builder::wall) {
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerBlock(this,
                    context.getRegistries().registerBlock(context.getNameWithSuffix("wall"), WallBlock::new, () -> {
                        return BlockBehaviour.Properties.ofLegacyCopy(context.getBaseBlock().value()).forceSolidOn();
                    }));
            context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
        }
    };
    BlockSetVariant FENCE = new VanillaBlockSetVariant(BlockFamily.Variant.FENCE, BlockFamily.Builder::fence) {
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerBlock(this,
                    context.getRegistries().registerBlock(context.getNameWithSuffix("fence"), FenceBlock::new, () -> {
                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value());
                    }));
            context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
        }
    };
    BlockSetVariant FENCE_GATE = new VanillaBlockSetVariant(BlockFamily.Variant.FENCE_GATE,
            BlockFamily.Builder::fenceGate) {
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix("fence_gate"),
                                    (BlockBehaviour.Properties properties) -> new FenceGateBlock(context.getWoodType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .forceSolidOn();
                                    }));
            context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
        }
    };
    BlockSetVariant DOOR = new VanillaBlockSetVariant(BlockFamily.Variant.DOOR, BlockFamily.Builder::door) {
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix("door"),
                                    (BlockBehaviour.Properties properties) -> new DoorBlock(context.getBlockSetType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .noOcclusion()
                                                .pushReaction(PushReaction.DESTROY);
                                    }));
            context.registerItem(this,
                    context.getRegistries().registerBlockItem(context.getBlock(this), DoubleHighBlockItem::new));
        }
    };
    BlockSetVariant TRAPDOOR = new VanillaBlockSetVariant(BlockFamily.Variant.TRAPDOOR, BlockFamily.Builder::trapdoor) {
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix("trapdoor"),
                                    (BlockBehaviour.Properties properties) -> new TrapDoorBlock(context.getBlockSetType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .noOcclusion()
                                                .isValidSpawn(Blocks::never);
                                    }));
            context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
        }
    };
    BlockSetVariant BUTTON = new VanillaBlockSetVariant(BlockFamily.Variant.BUTTON, BlockFamily.Builder::button) {
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix("button"),
                                    (BlockBehaviour.Properties properties) -> new ButtonBlock(context.getBlockSetType(),
                                            30,
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .noCollision()
                                                .pushReaction(PushReaction.DESTROY);
                                    }));
            context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
        }
    };
    BlockSetVariant PRESSURE_PLATE = new VanillaBlockSetVariant(BlockFamily.Variant.PRESSURE_PLATE,
            BlockFamily.Builder::pressurePlate) {
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix("pressure_plate"),
                                    (BlockBehaviour.Properties properties) -> new PressurePlateBlock(context.getBlockSetType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .forceSolidOn()
                                                .noCollision()
                                                .pushReaction(PushReaction.DESTROY);
                                    }));
            context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
        }
    };
    BlockSetVariant SIGN = new StandaloneBlockSetVariant(BlockFamily.Variant.SIGN) {
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix("sign"),
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
                            .registerBlock(context.getNameWithSuffix("wall_sign"),
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
        public void generateFor(BlockSetFamily.Context context) {
            throw new UnsupportedOperationException();
        }
    };
    BlockSetVariant HANGING_SIGN = new StandaloneBlockSetVariant("hanging_sign") {
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(context.getNameWithSuffix("hanging_sign"),
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
                            .registerBlock(context.getNameWithSuffix("wall_hanging_sign"),
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
                                            context.getBlock(this).value(),
                                            properties),
                                    () -> new Item.Properties().stacksTo(16)));
        }
    };
    BlockSetVariant WALL_HANGING_SIGN = new StandaloneBlockSetVariant("wall_hanging_sign") {
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            throw new UnsupportedOperationException();
        }
    };
    BlockSetVariant SHELF = new StandaloneBlockSetVariant("shelf") {
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerBlock(this,
                    context.getRegistries().registerBlock(context.getNameWithSuffix("shelf"), ShelfBlock::new, () -> {
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
        public void generateFor(BlockSetFamily.Context context) {
            context.registerEntityType(this,
                    (Holder.Reference<EntityType<?>>) (Holder.Reference<?>) context.getRegistries()
                            .registerEntityType(context.getNameWithSuffix("boat"),
                                    () -> EntityType.Builder.of((EntityType<Boat> entityType, Level level) -> {
                                                return new Boat(entityType, level, () -> context.getItem(this).value());
                                            }, MobCategory.MISC)
                                            .noLootTable()
                                            .sized(1.375F, 0.5625F)
                                            .eyeHeight(0.5625F)
                                            .clientTrackingRange(10)));
            context.registerItem(this,
                    context.getRegistries()
                            .registerItem(context.getNameWithSuffix("boat"),
                                    (Item.Properties properties) -> new BoatItem((EntityType<? extends AbstractBoat>) context.getEntityType(
                                            this).value(), properties),
                                    () -> new Item.Properties().stacksTo(1)));
        }
    };
    BlockSetVariant CHEST_BOAT = new StandaloneBlockSetVariant("chest_boat") {
        @SuppressWarnings("unchecked")
        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerEntityType(this,
                    (Holder.Reference<EntityType<?>>) (Holder.Reference<?>) context.getRegistries()
                            .registerEntityType(context.getNameWithSuffix("chest_boat"),
                                    () -> EntityType.Builder.of((EntityType<ChestBoat> entityType, Level level) -> {
                                                return new ChestBoat(entityType, level, () -> context.getItem(this).value());
                                            }, MobCategory.MISC)
                                            .noLootTable()
                                            .sized(1.375F, 0.5625F)
                                            .eyeHeight(0.5625F)
                                            .clientTrackingRange(10)));
            context.registerItem(this,
                    context.getRegistries()
                            .registerItem(context.getNameWithSuffix("chest_boat"),
                                    (Item.Properties properties) -> new BoatItem((EntityType<? extends AbstractBoat>) context.getEntityType(
                                            this).value(), properties),
                                    () -> new Item.Properties().stacksTo(1)));
        }
    };

    void generateFor(BlockSetFamily.Context context);

    BlockFamily.@Nullable Variant toVanilla();
}
