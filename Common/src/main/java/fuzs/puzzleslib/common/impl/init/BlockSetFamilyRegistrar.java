package fuzs.puzzleslib.common.impl.init;

import fuzs.puzzleslib.common.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetVariant;
import fuzs.puzzleslib.common.api.init.v3.registry.RegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class BlockSetFamilyRegistrar implements BlockSetFamily, BlockSetFamily.Writable, BlockSetFamily.Context {
    private final Map<BlockSetVariant, Holder.Reference<Block>> blockVariants = new LinkedHashMap<>();
    private final Map<BlockSetVariant, Holder.Reference<Item>> itemVariants = new LinkedHashMap<>();
    private final Map<BlockSetVariant, Holder.Reference<EntityType<?>>> entityVariants = new LinkedHashMap<>();
    private final Map<BlockSetVariant, Holder.Reference<Block>> blockVariantsView = Collections.unmodifiableMap(this.blockVariants);
    private final Map<BlockSetVariant, Holder.Reference<Item>> itemVariantsView = Collections.unmodifiableMap(this.itemVariants);
    private final Map<BlockSetVariant, Holder.Reference<EntityType<?>>> entityVariantsView = Collections.unmodifiableMap(
            this.entityVariants);
    private final RegistryManager registries;
    private final Holder.Reference<Block> baseBlock;
    private final String baseName;
    private final BlockSetType blockSetType;
    private final WoodType woodType;
    private Consumer<BlockFamily.Builder> blockFamilyConsumer = Function.identity()::apply;

    public BlockSetFamilyRegistrar(RegistryManager registries, Holder.Reference<Block> baseBlock, String baseName, BlockSetType blockSetType, WoodType woodType) {
        this.registries = registries;
        this.baseBlock = baseBlock;
        this.baseName = baseName;
        this.blockSetType = blockSetType;
        this.woodType = woodType;
    }

    @Override
    public Holder.Reference<Block> getBaseBlock() {
        return this.baseBlock;
    }

    @Override
    public BlockSetType getBlockSetType() {
        return this.blockSetType;
    }

    @Override
    public WoodType getWoodType() {
        return this.woodType;
    }

    @Override
    public BlockFamily getBlockFamily() {
        BlockFamily.Builder blockFamily = new BlockFamily.Builder(this.getBaseBlock().value());
        this.getBlockVariants().forEach((BlockSetVariant variant, Holder.Reference<Block> holder) -> {
            if (variant instanceof VanillaBlockSetVariant vanillaVariant) {
                vanillaVariant.variantBuilder.accept(blockFamily, holder.value());
            }
        });

        if (this.getBlockVariants().containsKey(BlockSetVariant.SIGN) && this.getBlockVariants()
                .containsKey(BlockSetVariant.WALL_SIGN)) {
            blockFamily.sign(this.getBlock(BlockSetVariant.SIGN).value(),
                    this.getBlock(BlockSetVariant.WALL_SIGN).value());
        }

        if (this.getBlockVariants().containsKey(BlockSetVariant.HANGING_SIGN) && this.getBlockVariants()
                .containsKey(BlockSetVariant.WALL_HANGING_SIGN)) {
            blockFamily.hangingSign(this.getBlock(BlockSetVariant.HANGING_SIGN).value(),
                    this.getBlock(BlockSetVariant.WALL_HANGING_SIGN).value());
        }

        this.blockFamilyConsumer.accept(blockFamily);
        return blockFamily.getFamily();
    }

    @Override
    public Map<BlockSetVariant, Holder.Reference<Block>> getBlockVariants() {
        return this.blockVariantsView;
    }

    @Override
    public Map<BlockSetVariant, Holder.Reference<Item>> getItemVariants() {
        return this.itemVariantsView;
    }

    @Override
    public Map<BlockSetVariant, Holder.Reference<EntityType<?>>> getEntityVariants() {
        return this.entityVariantsView;
    }

    @Override
    public String getName(UnaryOperator<String> name, @Nullable String baseNameOverride) {
        return Objects.requireNonNullElseGet(baseNameOverride, () -> name.apply(this.baseName));
    }

    @Override
    public RegistryManager getRegistries() {
        return this.registries;
    }

    @Override
    public Writable registerBlock(BlockSetVariant variant, Holder.Reference<Block> holder) {
        Objects.requireNonNull(holder, "holder is null");
        if (this.blockVariants.put(variant, holder) != null) {
            throw new IllegalStateException(variant + " already present");
        }

        return this;
    }

    @Override
    public Writable registerItem(BlockSetVariant variant, Holder.Reference<Item> holder) {
        Objects.requireNonNull(holder, "holder is null");
        if (this.itemVariants.put(variant, holder) != null) {
            throw new IllegalStateException(variant + " already present");
        }

        return this;
    }

    @Override
    public Writable registerEntityType(BlockSetVariant variant, Holder.Reference<EntityType<?>> holder) {
        Objects.requireNonNull(holder, "holder is null");
        if (this.entityVariants.put(variant, holder) != null) {
            throw new IllegalStateException(variant + " already present");
        }

        return this;
    }

    @Override
    public Writable generateFor(BlockSetVariant variant, @Nullable String baseNameOverride) {
        variant.generateFor(this, baseNameOverride);
        return this;
    }

    @Override
    public Writable configureBlockFamily(Consumer<BlockFamily.Builder> blockFamilyConsumer) {
        Objects.requireNonNull(blockFamilyConsumer, "consumer is null");
        this.blockFamilyConsumer = blockFamilyConsumer;
        return this;
    }
}
