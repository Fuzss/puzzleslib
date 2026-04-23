package fuzs.puzzleslib.impl.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;

public interface CustomTransmuteRecipe {
    Ingredient getInput();

    default void transmuteInput(ItemStack result, CraftingInput craftingInput) {
        for (int i = 0; i < craftingInput.size(); i++) {
            ItemStack itemStack = craftingInput.getItem(i);
            if (this.getInput().test(itemStack)) {
                result.applyComponents(itemStack.getComponentsPatch());
                return;
            }
        }
    }
}
