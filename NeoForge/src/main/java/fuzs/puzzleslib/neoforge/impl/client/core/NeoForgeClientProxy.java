package fuzs.puzzleslib.neoforge.impl.client.core;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;
import fuzs.puzzleslib.impl.config.ConfigTranslationsManager;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.network.codec.CustomPacketPayloadAdapter;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.client.event.NeoForgeClientEventInvokers;
import fuzs.puzzleslib.neoforge.impl.client.init.NeoForgeItemDisplayOverrides;
import fuzs.puzzleslib.neoforge.impl.client.key.NeoForgeKeyMappingHelper;
import fuzs.puzzleslib.neoforge.impl.core.NeoForgeCommonProxy;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class NeoForgeClientProxy extends NeoForgeCommonProxy implements ClientProxyImpl {

    @Override
    public void registerAllLoadingHandlers() {
        super.registerAllLoadingHandlers();
        NeoForgeClientEventInvokers.registerLoadingHandlers();
    }

    @Override
    public void registerAllEventHandlers() {
        super.registerAllEventHandlers();
        NeoForgeClientEventInvokers.registerEventHandlers();
    }

    @Override
    public <M1, M2> CompletableFuture<Void> registerClientReceiver(CustomPacketPayloadAdapter<M1> payload, IPayloadContext context, Function<M1, ClientboundMessage<M2>> adapter) {
        return context.enqueueWork(() -> {
            LocalPlayer player = (LocalPlayer) context.player();
            Objects.requireNonNull(player, "player is null");
            ClientboundMessage<M2> message = adapter.apply(payload.unwrap());
            message.getHandler()
                    .handle(message.unwrap(), Minecraft.getInstance(), player.connection, player, player.clientLevel);
        });
    }

    @Override
    public ModConstructorImpl<ClientModConstructor> getClientModConstructorImpl() {
        return new NeoForgeClientModConstructor();
    }

    @Override
    public ItemDisplayOverridesImpl<?> getItemModelDisplayOverrides() {
        return new NeoForgeItemDisplayOverrides();
    }

    @Override
    public KeyMappingHelper getKeyMappingActivationHelper() {
        return new NeoForgeKeyMappingHelper();
    }

    @Override
    public boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
        return keyMapping.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode));
    }

    @Override
    public ClientTooltipComponent createImageComponent(TooltipComponent imageComponent) {
        return ClientTooltipComponent.create(imageComponent);
    }

    @Override
    public boolean onRenderTooltip(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner positioner) {
        return ClientHooks.onRenderTooltipPre(ItemStack.EMPTY,
                guiGraphics,
                mouseX,
                mouseY,
                guiGraphics.guiWidth(),
                guiGraphics.guiHeight(),
                components,
                font,
                positioner).isCanceled();
    }

    @Override
    public BakedQuad copyBakedQuad(BakedQuad bakedQuad) {
        int[] vertices = bakedQuad.getVertices();
        return new BakedQuad(Arrays.copyOf(vertices, vertices.length),
                bakedQuad.getTintIndex(),
                bakedQuad.getDirection(),
                bakedQuad.getSprite(),
                bakedQuad.isShade(),
                bakedQuad.hasAmbientOcclusion());
    }

    @Override
    public boolean isEffectVisibleInInventory(MobEffectInstance mobEffect) {
        return IClientMobEffectExtensions.of(mobEffect).isVisibleInInventory(mobEffect);
    }

    @Override
    public boolean isEffectVisibleInGui(MobEffectInstance mobEffect) {
        return IClientMobEffectExtensions.of(mobEffect).isVisibleInGui(mobEffect);
    }

    @Override
    public void registerWoodType(WoodType woodType) {
        Sheets.addWoodType(woodType);
    }

    @Override
    public BakedModel getBakedModel(ModelManager modelManager, ResourceLocation resourceLocation) {
        return modelManager.getModel(ModelResourceLocation.standalone(resourceLocation));
    }

    @Override
    public void registerRenderType(Block block, RenderType renderType) {
        ItemBlockRenderTypes.setRenderLayer(block, renderType);
    }

    @Override
    public void registerRenderType(Fluid fluid, RenderType renderType) {
        ItemBlockRenderTypes.setRenderLayer(fluid, renderType);
    }

    @Override
    public int getGuiLeftHeight(Gui gui) {
        return gui.leftHeight;
    }

    @Override
    public int getGuiRightHeight(Gui gui) {
        return gui.rightHeight;
    }

    @Override
    public void addGuiLeftHeight(Gui gui, int leftHeight) {
        gui.leftHeight += leftHeight;
    }

    @Override
    public void addGuiRightHeight(Gui gui, int rightHeight) {
        gui.rightHeight += rightHeight;
    }

    @Override
    public void registerConfigurationScreen(String modId) {
        ModContainer modContainer = NeoForgeModContainerHelper.getModContainer(modId);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        ModConfigs.getModConfigs(modId).forEach((ModConfig modConfig) -> {
            if (modConfig.getSpec() instanceof ModConfigSpec modConfigSpec) {
                ConfigTranslationsManager.addModConfig(modConfig.getModId(),
                        modConfig.getType().extension(),
                        modConfig.getFileName(),
                        modConfigSpec);
            }
        });
    }
}
