package fuzs.puzzleslib.neoforge.impl.client.core;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.client.renderer.v1.RenderPropertyKey;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.impl.client.config.ConfigTranslationsManager;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.client.config.MultiConfigurationScreen;
import fuzs.puzzleslib.neoforge.impl.client.event.NeoForgeClientEventInvokers;
import fuzs.puzzleslib.neoforge.impl.client.key.NeoForgeKeyMappingHelper;
import fuzs.puzzleslib.neoforge.impl.core.NeoForgeCommonProxy;
import fuzs.puzzleslib.neoforge.impl.core.context.PayloadTypesContextNeoForgeImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class NeoForgeClientProxy extends NeoForgeCommonProxy implements ClientProxyImpl {
    private final Map<RenderPropertyKey<?>, ContextKey<?>> entityRenderStateKeys = new IdentityHashMap<>();

    @Override
    public void registerLoadingHandlers() {
        super.registerLoadingHandlers();
        NeoForgeClientEventInvokers.registerLoadingHandlers();
    }

    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();
        NeoForgeClientEventInvokers.registerEventHandlers();
    }

    @Override
    public PayloadTypesContext createPayloadTypesContext(String modId, RegisterPayloadHandlersEvent evt) {
        return new PayloadTypesContextNeoForgeImpl.ClientImpl(modId, evt);
    }

    @Override
    public ModConstructorImpl<ClientModConstructor> getClientModConstructorImpl() {
        return new NeoForgeClientModConstructor();
    }

    @Override
    public KeyMappingHelper getKeyMappingActivationHelper() {
        return new NeoForgeKeyMappingHelper();
    }

    @Override
    public <T> @Nullable T getRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key) {
        return entityRenderState.getRenderData(this.getContextKey(key));
    }

    @Override
    public <T> void setRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key, @Nullable T t) {
        entityRenderState.setRenderData(this.getContextKey(key), t);
    }

    @SuppressWarnings("unchecked")
    private <T> ContextKey<T> getContextKey(RenderPropertyKey<T> key) {
        return (ContextKey<T>) this.entityRenderStateKeys.computeIfAbsent(key,
                (RenderPropertyKey<?> keyX) -> new ContextKey<>(keyX.resourceLocation()));
    }

    @Override
    public void registerBuiltinResourcePack(ResourceLocation resourceLocation, Component displayName, boolean required) {
        NeoForgeModContainerHelper.getOptionalModEventBus(resourceLocation.getNamespace())
                .ifPresent((IEventBus eventBus) -> {
                    eventBus.addListener((final AddPackFindersEvent evt) -> {
                        if (evt.getPackType() == PackType.CLIENT_RESOURCES) {
                            evt.addPackFinders(resourceLocation.withPrefix("resourcepacks/"),
                                    PackType.CLIENT_RESOURCES,
                                    displayName,
                                    PackSource.BUILT_IN,
                                    required,
                                    Pack.Position.TOP);
                        }
                    });
                });
    }

    @Override
    public float getPartialTick(EntityRenderState renderState) {
        return renderState.partialTick;
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
        int[] vertices = bakedQuad.vertices();
        return new BakedQuad(Arrays.copyOf(vertices, vertices.length),
                bakedQuad.tintIndex(),
                bakedQuad.direction(),
                bakedQuad.sprite(),
                bakedQuad.shade(),
                bakedQuad.lightEmission(),
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
    public void registerConfigurationScreen(String modId, String... otherModIds) {
        NeoForgeModContainerHelper.getModContainer(modId)
                .registerExtensionPoint(IConfigScreenFactory.class,
                        MultiConfigurationScreen.getScreenFactory(otherModIds)::apply);
    }

    @Override
    public void registerConfigurationScreenForHolder(String modId) {
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final FMLClientSetupEvent evt) -> {
                evt.enqueueWork(() -> {
                    super.registerConfigurationScreenForHolder(modId);
                    ModConfigs.getModConfigs(modId).forEach((ModConfig modConfig) -> {
                        if (modConfig.getSpec() instanceof ModConfigSpec modConfigSpec) {
                            ConfigTranslationsManager.addModConfig(modConfig.getModId(),
                                    modConfig.getType().extension(),
                                    modConfig.getFileName(),
                                    modConfigSpec);
                        }
                    });
                });
            });
        });
    }

    @SuppressWarnings("deprecation")
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
    public void setGuiLeftHeight(Gui gui, int leftHeight) {
        gui.leftHeight = leftHeight;
    }

    @Override
    public void setGuiRightHeight(Gui gui, int rightHeight) {
        gui.rightHeight = rightHeight;
    }
}
