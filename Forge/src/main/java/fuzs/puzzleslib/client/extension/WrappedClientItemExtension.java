package fuzs.puzzleslib.client.extension;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a wrapper for {@link IItemRenderProperties}
 *
 * <p>we need this for {@link fuzs.puzzleslib.client.core.ClientModConstructor#onRegisterBuiltinModelItemRenderers}
 * in case an {@link IItemRenderProperties} is already present on the item
 */
public class WrappedClientItemExtension implements IItemRenderProperties {
    /**
     * the wrapped {@link IItemRenderProperties}
     */
    private final IItemRenderProperties wrapped;

    /**
     * @param wrapped create from wrapped {@link IItemRenderProperties}
     */
    public WrappedClientItemExtension(IItemRenderProperties wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public @Nullable Font getFont(ItemStack stack) {
        return this.wrapped.getFont(stack);
    }

    @Override
    public @NotNull HumanoidModel<?> getArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        return this.wrapped.getArmorModel(livingEntity, itemStack, equipmentSlot, original);
    }

    @Override
    public @NotNull Model getBaseArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        return this.wrapped.getBaseArmorModel(livingEntity, itemStack, equipmentSlot, original);
    }

    @Override
    public void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick) {
        this.wrapped.renderHelmetOverlay(stack, player, width, height, partialTick);
    }

    @Override
    public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
        return this.wrapped.getItemStackRenderer();
    }
}
