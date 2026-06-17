package fuzs.puzzleslib.common.impl.init;

import fuzs.puzzleslib.common.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetVariant;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

public abstract class VanillaBlockSetVariant implements BlockSetVariant {
    private final BlockFamily.Variant variant;
    final BiConsumer<BlockFamily.Builder, net.minecraft.world.level.block.Block> variantBuilder;

    public VanillaBlockSetVariant(BlockFamily.Variant variant, BiConsumer<BlockFamily.Builder, net.minecraft.world.level.block.Block> variantBuilder) {
        this.variant = variant;
        this.variantBuilder = variantBuilder;
    }

    @Override
    public final void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
        this.registerBlock(context, baseNameOverride);
        this.registerItem(context, baseNameOverride);
    }

    public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
        context.registerBlock(this,
                context.getRegistries()
                        .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride), () -> {
                            return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value());
                        }));
    }

    public void registerItem(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
        context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
    }

    public abstract String getName(BlockSetFamily.Context context, String variantName, @Nullable String baseNameOverride);

    @Override
    public BlockFamily.Variant toVanilla() {
        return this.variant;
    }

    @Override
    public String toString() {
        return "Vanilla[" + this.getSerializedName() + "]";
    }

    @Override
    public String getSerializedName() {
        return this.variant.getRecipeGroup();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof BlockSetVariant variant)) {
            return false;
        } else {
            return Objects.equals(this.getSerializedName(), variant.getSerializedName());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getSerializedName());
    }

    public static class Prefix extends VanillaBlockSetVariant {

        public Prefix(BlockFamily.Variant variant, BiConsumer<BlockFamily.Builder, net.minecraft.world.level.block.Block> variantBuilder) {
            super(variant, variantBuilder);
        }

        @Override
        public String getName(BlockSetFamily.Context context, String variantName, @Nullable String baseNameOverride) {
            return context.getNameWithPrefix(variantName, baseNameOverride);
        }
    }

    public static class Suffix extends VanillaBlockSetVariant {

        public Suffix(BlockFamily.Variant variant, BiConsumer<BlockFamily.Builder, net.minecraft.world.level.block.Block> variantBuilder) {
            super(variant, variantBuilder);
        }

        @Override
        public String getName(BlockSetFamily.Context context, String variantName, @Nullable String baseNameOverride) {
            return context.getNameWithSuffix(variantName, baseNameOverride);
        }
    }
}
