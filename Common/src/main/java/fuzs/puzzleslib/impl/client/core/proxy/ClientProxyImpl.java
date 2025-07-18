package fuzs.puzzleslib.impl.client.core.proxy;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.ClientComponentSplitter;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.client.renderer.v1.RenderPropertyKey;
import fuzs.puzzleslib.api.util.v1.ComponentHelper;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public interface ClientProxyImpl extends ProxyImpl {

    static ClientProxyImpl get() {
        return (ClientProxyImpl) ProxyImpl.INSTANCE;
    }

    ModConstructorImpl<ClientModConstructor> getClientModConstructorImpl();

    KeyMappingHelper getKeyMappingActivationHelper();

    @Nullable <T> T getRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key);

    <T> void setRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key, @Nullable T t);

    void registerBuiltinResourcePack(ResourceLocation resourceLocation, Component displayName, boolean required);

    float getPartialTick(EntityRenderState renderState);

    boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode);

    ClientTooltipComponent createImageComponent(TooltipComponent imageComponent);

    boolean onRenderTooltip(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner positioner);

    BakedQuad copyBakedQuad(BakedQuad bakedQuad);

    boolean isEffectVisibleInInventory(MobEffectInstance mobEffect);

    boolean isEffectVisibleInGui(MobEffectInstance mobEffect);

    void registerWoodType(WoodType woodType);

    int getLeftStatusBarHeight(ResourceLocation resourceLocation);

    int getRightStatusBarHeight(ResourceLocation resourceLocation);

    @Override
    default Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    default Level getClientLevel() {
        return Minecraft.getInstance().level;
    }

    @Override
    default ClientGamePacketListener getClientPacketListener() {
        ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
        Objects.requireNonNull(clientPacketListener, "client packet listener is null");
        return clientPacketListener;
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
