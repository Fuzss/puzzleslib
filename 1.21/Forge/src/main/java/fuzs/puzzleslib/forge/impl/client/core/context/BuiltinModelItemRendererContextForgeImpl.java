package fuzs.puzzleslib.forge.impl.client.core.context;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.init.v1.BuiltinItemRenderer;
import fuzs.puzzleslib.api.client.init.v1.ReloadingBuiltInItemRenderer;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.forge.impl.client.core.ForgeClientItemExtensionsImpl;
import fuzs.puzzleslib.forge.mixin.client.accessor.ItemForgeAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public record BuiltinModelItemRendererContextForgeImpl(String modId,
        List<ResourceManagerReloadListener> dynamicRenderers) implements BuiltinModelItemRendererContext {

    @Override
    public void registerItemRenderer(BuiltinItemRenderer renderer, ItemLike... items) {
        // copied from Forge, supposed to break data gen otherwise
        if (FMLLoader.getLaunchHandler().isData()) return;
        // do not check for ContentRegistrationFlags#DYNAMIC_RENDERERS being properly set as not every built-in item renderer needs to reload
        Objects.requireNonNull(renderer, "renderer is null");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkState(items.length > 0, "items is empty");
        IClientItemExtensions itemExtensions = new IClientItemExtensions() {
            @Nullable
            private BlockEntityWithoutLevelRenderer blockEntityWithoutLevelRenderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.blockEntityWithoutLevelRenderer == null) {
                    this.blockEntityWithoutLevelRenderer = new ForwardingBlockEntityWithoutLevelRenderer(Minecraft.getInstance(), renderer);
                }
                return this.blockEntityWithoutLevelRenderer;
            }
        };

        for (ItemLike item : items) {
            Objects.requireNonNull(item, "item is null");
            setClientItemExtensions(item.asItem(), itemExtensions);
        }
    }

    @Override
    public void registerItemRenderer(ReloadingBuiltInItemRenderer renderer, ItemLike... items) {
        this.registerItemRenderer((BuiltinItemRenderer) renderer, items);
        // store this to enable listening to resource reloads
        String itemName = BuiltInRegistries.ITEM.getKey(items[0].asItem()).getPath();
        ResourceLocation identifier = new ResourceLocation(this.modId, itemName + "_built_in_model_renderer");
        this.dynamicRenderers.add(ForwardingReloadListenerHelper.fromResourceManagerReloadListener(identifier, renderer));
    }

    private static void setClientItemExtensions(Item item, IClientItemExtensions itemExtensions) {
        // this solution is very dangerous as it relies on internal stuff in Forge
        // but there is no other way for multi-loader and without making this a huge inconvenience so ¯\_(ツ)_/¯
        Object renderProperties = ((ItemForgeAccessor) item).puzzleslib$getRenderProperties();
        ((ItemForgeAccessor) item).puzzleslib$setRenderProperties(renderProperties != null ? new ForgeClientItemExtensionsImpl((IClientItemExtensions) renderProperties) {

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return itemExtensions.getCustomRenderer();
            }
        } : itemExtensions);
    }

    private static class ForwardingBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
        private final BuiltinItemRenderer renderer;

        public ForwardingBlockEntityWithoutLevelRenderer(Minecraft minecraft, BuiltinItemRenderer renderer) {
            super(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
            this.renderer = renderer;
        }

        @Override
        public void renderByItem(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
            this.renderer.renderByItem(stack, mode, matrices, vertexConsumers, light, overlay);
        }
    }
}
