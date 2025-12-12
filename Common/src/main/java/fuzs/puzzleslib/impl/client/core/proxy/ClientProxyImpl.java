package fuzs.puzzleslib.impl.client.core.proxy;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.TickTask;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public interface ClientProxyImpl extends ProxyImpl {

    static ClientProxyImpl get() {
        return (ClientProxyImpl) ProxyImpl.INSTANCE;
    }

    ModConstructorImpl<ClientModConstructor> getClientModConstructorImpl();

    KeyMappingHelper getKeyMappingActivationHelper();

    @Nullable <T> T getRenderProperty(EntityRenderState entityRenderState, ContextKey<T> key);

    <T> void setRenderProperty(EntityRenderState entityRenderState, ContextKey<T> key, @Nullable T t);

    boolean isKeyActiveAndMatches(KeyMapping keyMapping, KeyEvent keyEvent);

    ClientTooltipComponent createImageComponent(TooltipComponent imageComponent);

    boolean onRenderTooltip(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner positioner);

    BakedQuad copyBakedQuad(BakedQuad bakedQuad);

    boolean isEffectVisibleInInventory(MobEffectInstance mobEffect);

    boolean isEffectVisibleInGui(MobEffectInstance mobEffect);

    void registerWoodType(WoodType woodType);

    int getLeftStatusBarHeight(Identifier identifier);

    int getRightStatusBarHeight(Identifier identifier);

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
    default ClientGamePacketListener getClientPacketListener() {
        ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
        Objects.requireNonNull(clientPacketListener, "client packet listener is null");
        return clientPacketListener;
    }

    @Override
    default boolean hasControlDown() {
        return Minecraft.getInstance().hasControlDown();
    }

    @Override
    default boolean hasShiftDown() {
        return Minecraft.getInstance().hasShiftDown();
    }

    @Override
    default boolean hasAltDown() {
        return Minecraft.getInstance().hasAltDown();
    }
}
