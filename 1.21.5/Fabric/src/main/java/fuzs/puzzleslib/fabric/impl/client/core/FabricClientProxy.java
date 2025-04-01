package fuzs.puzzleslib.fabric.impl.client.core;

import com.google.common.collect.ImmutableMap;
import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.client.renderer.v1.RenderPropertyKey;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import fuzs.puzzleslib.fabric.impl.client.config.MultiConfigurationScreen;
import fuzs.puzzleslib.fabric.impl.client.event.FabricClientEventInvokers;
import fuzs.puzzleslib.fabric.impl.client.event.FabricGuiEventHelper;
import fuzs.puzzleslib.fabric.impl.client.key.FabricKeyMappingHelper;
import fuzs.puzzleslib.fabric.impl.client.util.EntityRenderStateExtension;
import fuzs.puzzleslib.fabric.impl.core.FabricCommonProxy;
import fuzs.puzzleslib.fabric.impl.core.context.PayloadTypesContextFabricImpl;
import fuzs.puzzleslib.fabric.mixin.client.accessor.MultiPlayerGameModeFabricAccessor;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.client.config.ConfigTranslationsManager;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;

public class FabricClientProxy extends FabricCommonProxy implements ClientProxyImpl {
    private static final String KEY_GUI_LEFT_HEIGHT = PuzzlesLibMod.id("gui_left_height").toString();
    private static final String KEY_GUI_RIGHT_HEIGHT = PuzzlesLibMod.id("gui_right_height").toString();
    private static final Map<ResourceLocation, ResourceLocation> VANILLA_GUI_LAYERS = ImmutableMap.<ResourceLocation, ResourceLocation>builder()
            .put(RenderGuiLayerEvents.CAMERA_OVERLAYS, IdentifiedLayer.MISC_OVERLAYS)
            .put(RenderGuiLayerEvents.CROSSHAIR, IdentifiedLayer.CROSSHAIR)
//            .put(RenderGuiLayerEvents.HOTBAR, VanillaGuiLayers.HOTBAR)
//            .put(RenderGuiLayerEvents.JUMP_METER, VanillaGuiLayers.JUMP_METER)
//            .put(RenderGuiLayerEvents.EXPERIENCE_BAR, VanillaGuiLayers.EXPERIENCE_BAR)
//            .put(RenderGuiLayerEvents.PLAYER_HEALTH, VanillaGuiLayers.PLAYER_HEALTH)
//            .put(RenderGuiLayerEvents.ARMOR_LEVEL, VanillaGuiLayers.ARMOR_LEVEL)
//            .put(RenderGuiLayerEvents.FOOD_LEVEL, VanillaGuiLayers.FOOD_LEVEL)
//            .put(RenderGuiLayerEvents.VEHICLE_HEALTH, VanillaGuiLayers.VEHICLE_HEALTH)
//            .put(RenderGuiLayerEvents.AIR_LEVEL, VanillaGuiLayers.AIR_LEVEL)
//            .put(RenderGuiLayerEvents.SELECTED_ITEM_NAME, VanillaGuiLayers.SELECTED_ITEM_NAME)
//            .put(RenderGuiLayerEvents.SPECTATOR_TOOLTIP, VanillaGuiLayers.SPECTATOR_TOOLTIP)
            .put(RenderGuiLayerEvents.EXPERIENCE_LEVEL, IdentifiedLayer.EXPERIENCE_LEVEL)
            .put(RenderGuiLayerEvents.STATUS_EFFECTS, IdentifiedLayer.STATUS_EFFECTS)
            .put(RenderGuiLayerEvents.BOSS_BAR, IdentifiedLayer.BOSS_BAR)
            .put(RenderGuiLayerEvents.SLEEP_OVERLAY, IdentifiedLayer.SLEEP)
            .put(RenderGuiLayerEvents.DEMO_TIMER, IdentifiedLayer.DEMO_TIMER)
            .put(RenderGuiLayerEvents.DEBUG_OVERLAY, IdentifiedLayer.DEBUG)
            .put(RenderGuiLayerEvents.SCOREBOARD, IdentifiedLayer.SCOREBOARD)
            .put(RenderGuiLayerEvents.OVERLAY_MESSAGE, IdentifiedLayer.OVERLAY_MESSAGE)
            .put(RenderGuiLayerEvents.TITLE, IdentifiedLayer.TITLE_AND_SUBTITLE)
            .put(RenderGuiLayerEvents.CHAT, IdentifiedLayer.CHAT)
            .put(RenderGuiLayerEvents.PLAYER_LIST, IdentifiedLayer.PLAYER_LIST)
            .put(RenderGuiLayerEvents.SUBTITLES, IdentifiedLayer.SUBTITLES)
//            .put(RenderGuiLayerEvents.SAVING_INDICATOR, VanillaGuiLayers.SAVING_INDICATOR)
            .build();

    @Override
    public void registerLoadingHandlers() {
        super.registerLoadingHandlers();
        FabricClientEventInvokers.registerLoadingHandlers();
    }

    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();
        FabricClientEventInvokers.registerEventHandlers();
    }

    @Override
    public boolean hasChannel(PacketListener packetListener, CustomPacketPayload.Type<?> type) {
        if (super.hasChannel(packetListener, type)) {
            return true;
        } else if (packetListener instanceof ClientConfigurationPacketListenerImpl) {
            return ClientConfigurationNetworking.canSend(type);
        } else if (packetListener instanceof ClientPacketListener) {
            return ClientPlayNetworking.canSend(type);
        } else {
            return false;
        }
    }

    @Override
    public Connection getConnection(PacketListener packetListener) {
        return packetListener instanceof ClientCommonPacketListenerImpl clientPacketListener ?
                clientPacketListener.connection : super.getConnection(packetListener);
    }

    @Override
    public PayloadTypesContext createPayloadTypesContext(String modId) {
        return new PayloadTypesContextFabricImpl.ClientImpl(modId);
    }

    @Override
    public boolean shouldStartDestroyBlock(BlockPos blockPos) {
        MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
        Objects.requireNonNull(gameMode, "game mode is null");
        return !gameMode.isDestroying() ||
                !((MultiPlayerGameModeFabricAccessor) gameMode).puzzleslib$callSameDestroyTarget(blockPos);
    }

    @Override
    public void startClientPrediction(Level level, IntFunction<Packet<ServerGamePacketListener>> predictiveAction) {
        MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
        Objects.requireNonNull(gameMode, "game mode is null");
        ((MultiPlayerGameModeFabricAccessor) gameMode).puzzleslib$callStartPrediction((ClientLevel) level,
                predictiveAction::apply);
    }

    @Override
    public ModConstructorImpl<ClientModConstructor> getClientModConstructorImpl() {
        return new FabricClientModConstructor();
    }

    @Override
    public KeyMappingHelper getKeyMappingActivationHelper() {
        return new FabricKeyMappingHelper();
    }

    @Override
    public <T> @Nullable T getRenderProperty(EntityRenderState renderState, RenderPropertyKey<T> key) {
        return ((EntityRenderStateExtension) renderState).puzzleslib$getRenderProperty(key);
    }

    @Override
    public <T> void setRenderProperty(EntityRenderState renderState, RenderPropertyKey<T> key, @Nullable T t) {
        ((EntityRenderStateExtension) renderState).puzzleslib$setRenderProperty(key, t);
    }

    @Override
    public void registerBuiltinResourcePack(ResourceLocation resourceLocation, Component displayName, boolean required) {
        FabricLoader.getInstance()
                .getModContainer(resourceLocation.getNamespace())
                .ifPresent((ModContainer modContainer) -> {
                    ResourceManagerHelper.registerBuiltinResourcePack(resourceLocation,
                            modContainer,
                            displayName,
                            required ? ResourcePackActivationType.ALWAYS_ENABLED : ResourcePackActivationType.NORMAL);
                });
    }

    @Override
    public float getPartialTick(EntityRenderState renderState) {
        return Mth.frac(renderState.ageInTicks);
    }

    @Override
    public boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
        return keyMapping.matches(keyCode, scanCode);
    }

    @Override
    public ClientTooltipComponent createImageComponent(TooltipComponent imageComponent) {
        ClientTooltipComponent component = TooltipComponentCallback.EVENT.invoker().getComponent(imageComponent);
        return Objects.requireNonNullElseGet(component, () -> ClientTooltipComponent.create(imageComponent));
    }

    @Override
    public boolean onRenderTooltip(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner positioner) {
        return FabricGuiEvents.RENDER_TOOLTIP.invoker()
                .onRenderTooltip(guiGraphics, font, mouseX, mouseY, components, positioner)
                .isInterrupt();
    }

    @Override
    public BakedQuad copyBakedQuad(BakedQuad bakedQuad) {
        int[] vertices = bakedQuad.vertices();
        return new BakedQuad(Arrays.copyOf(vertices, vertices.length),
                bakedQuad.tintIndex(),
                bakedQuad.direction(),
                bakedQuad.sprite(),
                bakedQuad.shade(),
                bakedQuad.lightEmission());
    }

    @Override
    public boolean isEffectVisibleInInventory(MobEffectInstance mobEffect) {
        return true;
    }

    @Override
    public boolean isEffectVisibleInGui(MobEffectInstance mobEffect) {
        return true;
    }

    @Override
    public void registerWoodType(WoodType woodType) {
        // this might register fine, but if another mod loads the Sheets class too early it will be missing
        // also wrap this in the event, so we ourselves do not load the Sheets class too early
        ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft minecraft) -> {
            Sheets.SIGN_MATERIALS.put(woodType, Sheets.createSignMaterial(woodType));
            Sheets.HANGING_SIGN_MATERIALS.put(woodType, Sheets.createHangingSignMaterial(woodType));
        });
    }

    @Override
    public ResourceLocation getModLoaderGuiLayer(ResourceLocation resourceLocation) {
        // TODO implement this properly
//        return VANILLA_GUI_LAYERS.get(resourceLocation);
        return resourceLocation;
    }

    @Override
    public void registerConfigurationScreen(String modId, String... otherModIds) {
        ConfigScreenFactoryRegistry.INSTANCE.register(modId, MultiConfigurationScreen.getFactory(otherModIds));
    }

    @Override
    public void registerConfigTranslations(String modId) {
        ModConfigs.getModConfigs(modId).forEach((ModConfig modConfig) -> {
            if (modConfig.getSpec() instanceof ModConfigSpec modConfigSpec) {
                ConfigTranslationsManager.addModConfig(modConfig.getModId(),
                        modConfig.getType().extension(),
                        modConfig.getFileName(),
                        modConfigSpec);
            }
        });
    }

    @Override
    public void registerRenderType(Block block, RenderType renderType) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
    }

    @Override
    public void registerRenderType(Fluid fluid, RenderType renderType) {
        BlockRenderLayerMap.INSTANCE.putFluid(fluid, renderType);
    }

    @Override
    public int getGuiLeftHeight(Gui gui) {
        return FabricLoader.getInstance().getObjectShare().get(KEY_GUI_LEFT_HEIGHT) instanceof Integer i ? i : 0;
    }

    @Override
    public int getGuiRightHeight(Gui gui) {
        return FabricLoader.getInstance().getObjectShare().get(KEY_GUI_RIGHT_HEIGHT) instanceof Integer i ? i : 0;
    }

    @Override
    public void setGuiLeftHeight(Gui gui, int leftHeight) {
        FabricLoader.getInstance().getObjectShare().put(KEY_GUI_LEFT_HEIGHT, leftHeight);
    }

    @Override
    public void setGuiRightHeight(Gui gui, int rightHeight) {
        FabricLoader.getInstance().getObjectShare().put(KEY_GUI_RIGHT_HEIGHT, rightHeight);
    }

    @Override
    public void registerProvidedEventHandlers() {
        super.registerProvidedEventHandlers();
        FabricGuiEventHelper.registerEventHandlers();
    }
}
