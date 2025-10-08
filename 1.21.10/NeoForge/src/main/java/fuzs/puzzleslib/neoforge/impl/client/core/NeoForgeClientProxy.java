package fuzs.puzzleslib.neoforge.impl.client.core;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.renderstate.RenderStateExtensions;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

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
    public PayloadTypesContext createPayloadTypesContext(String modId, RegisterPayloadHandlersEvent event) {
        return new PayloadTypesContextNeoForgeImpl.ClientImpl(modId, event);
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
    public <T> @Nullable T getRenderProperty(EntityRenderState entityRenderState, ContextKey<T> key) {
        return entityRenderState.getRenderData(key);
    }

    @Override
    public <T> void setRenderProperty(EntityRenderState entityRenderState, ContextKey<T> key, @Nullable T t) {
        entityRenderState.setRenderData(key, t);
    }

    @Override
    public float getPartialTick(EntityRenderState renderState) {
        return renderState.partialTick;
    }

    @Override
    public boolean isKeyActiveAndMatches(KeyMapping keyMapping, KeyEvent keyEvent) {
        return keyMapping.isActiveAndMatches(InputConstants.getKey(keyEvent));
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
            eventBus.addListener((final FMLClientSetupEvent event) -> {
                event.enqueueWork(() -> {
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

    @Override
    public int getLeftStatusBarHeight(ResourceLocation resourceLocation) {
        return Minecraft.getInstance().gui.leftHeight;
    }

    @Override
    public int getRightStatusBarHeight(ResourceLocation resourceLocation) {
        return Minecraft.getInstance().gui.rightHeight;
    }

    @Override
    public <E extends Entity, S extends EntityRenderState> void onUpdateEntityRenderState(EntityRenderer<E, S> renderer, E entity, S renderState, float partialTick) {
        RenderStateExtensions.onUpdateEntityRenderState(renderer, entity, renderState);
    }
}
