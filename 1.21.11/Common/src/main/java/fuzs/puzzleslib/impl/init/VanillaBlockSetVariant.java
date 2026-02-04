package fuzs.puzzleslib.impl.init;

import fuzs.puzzleslib.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.api.init.v3.family.BlockSetVariant;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.state.BlockBehaviour;

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

    public static class Direct extends VanillaBlockSetVariant {

        public Direct(BlockFamily.Variant variant, BiConsumer<BlockFamily.Builder, net.minecraft.world.level.block.Block> variantBuilder) {
            super(variant, variantBuilder);
        }

        @Override
        public void generateFor(BlockSetFamily.Context context) {
            context.registerBlock(this,
                    context.getRegistries().registerBlock(context.getNameWithPrefix(this.getSerializedName()), () -> {
                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value());
                    }));
            context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
        }
    }
}
