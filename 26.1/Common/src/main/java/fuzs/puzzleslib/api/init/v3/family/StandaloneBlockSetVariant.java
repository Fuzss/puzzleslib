package fuzs.puzzleslib.api.init.v3.family;

import net.minecraft.data.BlockFamily;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public abstract class StandaloneBlockSetVariant implements BlockSetVariant {
    private final String name;

    public StandaloneBlockSetVariant(String name) {
        this.name = name;
    }

    public StandaloneBlockSetVariant(BlockFamily.Variant variant) {
        this(variant.getRecipeGroup());
    }

    @Override
    public BlockFamily.@Nullable Variant toVanilla() {
        return null;
    }

    @Override
    public String toString() {
        return "Standalone[" + this.getSerializedName() + "]";
    }

    @Override
    public String getSerializedName() {
        return this.name;
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
}
