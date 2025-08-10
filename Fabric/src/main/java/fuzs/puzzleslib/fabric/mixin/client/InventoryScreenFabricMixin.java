package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.api.client.renderer.v1.RenderPropertyKey;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InventoryScreen.class)
abstract class InventoryScreenFabricMixin extends AbstractRecipeBookScreen<InventoryMenu> {

    public InventoryScreenFabricMixin(InventoryMenu menu, RecipeBookComponent<?> recipeBookComponent, Inventory playerInventory, Component title) {
        super(menu, recipeBookComponent, playerInventory, title);
    }

    @ModifyVariable(method = "renderEntityInInventory", at = @At("STORE"))
    private static EntityRenderState renderEntityInInventory(EntityRenderState entityRenderState, @Local(argsOnly = true) LivingEntity entity, @Local EntityRenderer<? super LivingEntity, ?> entityRenderer) {
        RenderPropertyKey.onUpdateEntityRenderState((EntityRenderer<? super LivingEntity, ? super EntityRenderState>) entityRenderer,
                entity,
                entityRenderState,
                1.0F);
        return entityRenderState;
    }
}
