package fuzs.puzzleslib.fabric.impl.client.core;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import fuzs.puzzleslib.fabric.impl.client.event.FabricClientEventInvokers;
import fuzs.puzzleslib.fabric.impl.client.event.FabricGuiEventHelper;
import fuzs.puzzleslib.fabric.impl.client.init.FabricItemDisplayOverrides;
import fuzs.puzzleslib.fabric.impl.client.key.FabricKeyMappingHelper;
import fuzs.puzzleslib.fabric.impl.core.FabricCommonProxy;
import fuzs.puzzleslib.fabric.mixin.client.accessor.MultiPlayerGameModeFabricAccessor;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;
import fuzs.puzzleslib.impl.config.ConfigTranslationsManager;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.network.codec.CustomPacketPayloadAdapter;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public class FabricClientProxy extends FabricCommonProxy implements ClientProxyImpl {

    @Override
    public void registerAllLoadingHandlers() {
        super.registerAllLoadingHandlers();
        FabricClientEventInvokers.registerLoadingHandlers();
    }

    @Override
    public void registerAllEventHandlers() {
        super.registerAllEventHandlers();
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
    public <M1, M2> void registerClientReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<M1>> type, BiConsumer<Throwable, Consumer<Component>> disconnectExceptionally, Function<M1, ClientboundMessage<M2>> messageAdapter) {
        ClientPlayNetworking.registerGlobalReceiver(type,
                (CustomPacketPayloadAdapter<M1> payload, ClientPlayNetworking.Context context) -> {
                    try {
                        Objects.requireNonNull(context.player(), "player is null");
                        ClientboundMessage<M2> message = messageAdapter.apply(payload.unwrap());
                        message.getHandler()
                                .handle(message.unwrap(),
                                        context.client(),
                                        context.player().connection,
                                        context.player(),
                                        context.client().level);
                    } catch (Throwable throwable) {
                        disconnectExceptionally.accept(throwable, context.responseSender()::disconnect);
                    }
                });
    }

    @Override
    public void setupHandshakePayload(CustomPacketPayload.Type<BrandPayload> payloadType) {
        super.setupHandshakePayload(payloadType);
        ClientPlayNetworking.registerGlobalReceiver(payloadType,
                (BrandPayload payload, ClientPlayNetworking.Context context) -> {
                    // NO-OP
                });
    }

    @Override
    public boolean shouldStartDestroyBlock(BlockPos blockPos) {
        MultiPlayerGameModeFabricAccessor gameMode = (MultiPlayerGameModeFabricAccessor) Minecraft.getInstance().gameMode;
        return !gameMode.puzzleslib$getIsDestroying() || !gameMode.puzzleslib$callSameDestroyTarget(blockPos);
    }

    @Override
    public void startClientPrediction(Level level, IntFunction<Packet<ServerGamePacketListener>> predictiveAction) {
        ((MultiPlayerGameModeFabricAccessor) Minecraft.getInstance().gameMode).puzzleslib$callStartPrediction((ClientLevel) level,
                predictiveAction::apply);
    }

    @Override
    public ModConstructorImpl<ClientModConstructor> getClientModConstructorImpl() {
        return new FabricClientModConstructor();
    }

    @Override
    public ItemDisplayOverridesImpl<?> getItemModelDisplayOverrides() {
        return new FabricItemDisplayOverrides();
    }

    @Override
    public KeyMappingHelper getKeyMappingActivationHelper() {
        return new FabricKeyMappingHelper();
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
        int[] vertices = bakedQuad.getVertices();
        return new BakedQuad(Arrays.copyOf(vertices, vertices.length),
                bakedQuad.getTintIndex(),
                bakedQuad.getDirection(),
                bakedQuad.getSprite(),
                bakedQuad.isShade());
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
    public BakedModel getBakedModel(ModelManager modelManager, ResourceLocation resourceLocation) {
        return modelManager.getModel(resourceLocation);
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
        return FabricGuiEventHelper.getGuiLeftHeight();
    }

    @Override
    public int getGuiRightHeight(Gui gui) {
        return FabricGuiEventHelper.getGuiRightHeight();
    }

    @Override
    public void addGuiLeftHeight(Gui gui, int leftHeight) {
        FabricGuiEventHelper.setGuiLeftHeight(this.getGuiLeftHeight(gui) + leftHeight);
    }

    @Override
    public void addGuiRightHeight(Gui gui, int rightHeight) {
        FabricGuiEventHelper.setGuiRightHeight(this.getGuiRightHeight(gui) + rightHeight);
    }

    @Override
    public void registerConfigurationScreen(String modId) {
        ConfigScreenFactoryRegistry.INSTANCE.register(modId, ConfigurationScreen::new);
        ModConfigs.getModConfigs(modId).forEach((ModConfig modConfig) -> {
            if (modConfig.getSpec() instanceof ModConfigSpec modConfigSpec) {
                ConfigTranslationsManager.addModConfig(modConfig.getModId(),
                        modConfig.getType().extension(),
                        modConfig.getFileName(),
                        modConfigSpec);
            }
        });
    }

    @MustBeInvokedByOverriders
    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();
        FabricGuiEventHelper.registerEventHandlers();
    }
}
