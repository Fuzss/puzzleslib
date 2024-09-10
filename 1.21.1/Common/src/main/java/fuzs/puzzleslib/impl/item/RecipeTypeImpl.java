package fuzs.puzzleslib.impl.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public record RecipeTypeImpl<T extends Recipe<?>>(String identifier) implements RecipeType<T> {

    public RecipeTypeImpl(ResourceLocation identifier) {
        this(identifier.toString());
    }

    @Override
    public String toString() {
        return this.identifier;
    }
}
