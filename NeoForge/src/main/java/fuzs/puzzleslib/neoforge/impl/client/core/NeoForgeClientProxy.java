package fuzs.puzzleslib.neoforge.impl.client.core;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.client.renderer.v1.model.MutableBakedQuad;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.client.config.CustomConfigurationScreen;
import fuzs.puzzleslib.neoforge.impl.client.event.NeoForgeClientEventInvokers;
import fuzs.puzzleslib.neoforge.impl.client.key.NeoForgeKeyMappingHelper;
import fuzs.puzzleslib.neoforge.impl.client.renderer.NeoForgeMutableBakedQuad;
import fuzs.puzzleslib.neoforge.impl.core.NeoForgeCommonProxy;
import fuzs.puzzleslib.neoforge.impl.core.context.PayloadTypesContextNeoForgeImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.jspecify.annotations.Nullable;

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
    public MutableBakedQuad getMutableBakedQuad(BakedQuad bakedQuad) {
        return new NeoForgeMutableBakedQuad(bakedQuad);
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
                .registerExtensionPoint(IConfigScreenFactory.class, (ModContainer modContainer, Screen screen) -> {
                    return new CustomConfigurationScreen(modContainer.getModId(), screen, otherModIds);
                });
    }

    @Override
    public int getLeftStatusBarHeight(Identifier identifier) {
        return Minecraft.getInstance().gui.leftHeight;
    }

    @Override
    public int getRightStatusBarHeight(Identifier identifier) {
        return Minecraft.getInstance().gui.rightHeight;
    }
}
