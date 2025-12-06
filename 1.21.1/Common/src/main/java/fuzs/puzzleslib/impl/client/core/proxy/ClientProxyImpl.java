package fuzs.puzzleslib.impl.client.core.proxy;

import fuzs.puzzleslib.api.chat.v1.ComponentHelper;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.gui.v2.components.tooltip.ClientComponentSplitter;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Objects;

public interface ClientProxyImpl extends ProxyImpl {

    static ClientProxyImpl get() {
        return (ClientProxyImpl) ProxyImpl.INSTANCE;
    }

    ModConstructorImpl<ClientModConstructor> getClientModConstructorImpl();

    ItemDisplayOverridesImpl<?> getItemModelDisplayOverrides();

    KeyMappingHelper getKeyMappingActivationHelper();

    boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode);

    ClientTooltipComponent createImageComponent(TooltipComponent imageComponent);

    boolean onRenderTooltip(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner positioner);

    BakedQuad copyBakedQuad(BakedQuad bakedQuad);

    boolean isEffectVisibleInInventory(MobEffectInstance mobEffect);

    boolean isEffectVisibleInGui(MobEffectInstance mobEffect);

    void registerWoodType(WoodType woodType);

    BakedModel getBakedModel(ModelManager modelManager, ResourceLocation resourceLocation);

    void registerRenderType(Block block, RenderType renderType);

    void registerRenderType(Fluid fluid, RenderType renderType);

    int getGuiLeftHeight(Gui gui);

    int getGuiRightHeight(Gui gui);

    void addGuiLeftHeight(Gui gui, int leftHeight);

    void addGuiRightHeight(Gui gui, int rightHeight);

    @Override
    default BlockableEventLoop<? super TickTask> getBlockableEventLoop(Level level) {
        if (level.isClientSide()) {
            return Minecraft.getInstance();
        } else {
            return ProxyImpl.super.getBlockableEventLoop(level);
        }
    }

    @Override
    default RegistryAccess getRegistryAccess() {
        return Minecraft.getInstance().getConnection() != null ?
                Minecraft.getInstance().getConnection().registryAccess() : null;
    }

    @Override
    default Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    default Level getClientLevel() {
        return Minecraft.getInstance().level;
    }

    @Override
    default ClientPacketListener getClientPacketListener() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        Objects.requireNonNull(connection, "client packet listener is null");
        return connection;
    }

    @Override
    default boolean hasControlDown() {
        return Screen.hasControlDown();
    }

    @Override
    default boolean hasShiftDown() {
        return Screen.hasShiftDown();
    }

    @Override
    default boolean hasAltDown() {
        return Screen.hasAltDown();
    }

    @Override
    default List<Component> splitTooltipLines(Component component) {
        return ClientComponentSplitter.splitTooltipLines(component).map(ComponentHelper::toComponent).toList();
    }
}
