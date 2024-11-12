package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.core.v1.context.BuiltinModelItemRendererContext;
import fuzs.puzzleslib.api.client.init.v1.BuiltinItemRenderer;
import fuzs.puzzleslib.api.client.init.v1.ReloadingBuiltInItemRenderer;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
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
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public record BuiltinModelItemRendererContextNeoForgeImpl(BiConsumer<IClientItemExtensions, Item> consumer,
                                                          String modId,
                                                          List<ResourceManagerReloadListener> dynamicRenderers) implements BuiltinModelItemRendererContext {

    @Override
    public void registerItemRenderer(BuiltinItemRenderer renderer, ItemLike... items) {
        // copied from Forge, supposed to break data gen otherwise
        if (DatagenModLoader.isRunningDataGen()) return;
        // do not check for ContentRegistrationFlags#DYNAMIC_RENDERERS being properly set as not every built-in item renderer needs to reload
        Objects.requireNonNull(renderer, "renderer is null");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkState(items.length > 0, "items is empty");
        IClientItemExtensions clientItemExtensions = new ClientItemExtensionsImpl(renderer);
        for (ItemLike item : items) {
            Objects.requireNonNull(item, "item is null");
            this.consumer.accept(clientItemExtensions, item.asItem());
        }
    }

    @Override
    public void registerItemRenderer(ReloadingBuiltInItemRenderer renderer, ItemLike... items) {
        this.registerItemRenderer((BuiltinItemRenderer) renderer, items);
        // store this to enable listening to resource reloads
        String itemName = BuiltInRegistries.ITEM.getKey(items[0].asItem()).getPath();
        ResourceLocation resourceLocation = ResourceLocationHelper.fromNamespaceAndPath(this.modId,
                itemName + "_built_in_model_renderer"
        );
        this.dynamicRenderers.add(
                ForwardingReloadListenerHelper.fromResourceManagerReloadListener(resourceLocation, renderer));
    }

    private static class ClientItemExtensionsImpl implements IClientItemExtensions {
        private final BuiltinItemRenderer itemRenderer;
        @Nullable
        private BlockEntityWithoutLevelRenderer blockEntityRenderer;

        public ClientItemExtensionsImpl(BuiltinItemRenderer itemRenderer) {
            this.itemRenderer = itemRenderer;
        }

        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            if (this.blockEntityRenderer == null) {
                Minecraft minecraft = Minecraft.getInstance();
                return this.blockEntityRenderer = new BlockEntityWithoutLevelRenderer(
                        minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels()) {

                    @Override
                    public void renderByItem(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack matrices, MultiBufferSource buffer, int packedLight, int packedOverlay) {
                        ClientItemExtensionsImpl.this.itemRenderer.renderByItem(itemStack, displayContext, matrices,
                                buffer, packedLight, packedOverlay
                        );
                    }
                };
            } else {
                return this.blockEntityRenderer;
            }
        }
    }
}
