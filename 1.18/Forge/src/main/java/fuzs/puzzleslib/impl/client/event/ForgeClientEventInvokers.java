package fuzs.puzzleslib.impl.client.event;

import fuzs.puzzleslib.api.client.event.v1.*;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.*;
import fuzs.puzzleslib.impl.client.core.event.CreativeModeTabContentsEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static fuzs.puzzleslib.impl.event.ForgeEventInvokerRegistryImpl.INSTANCE;

@SuppressWarnings("unchecked")
public final class ForgeClientEventInvokers {

    public static void register() {
        INSTANCE.register(ClientTickEvents.Start.class, TickEvent.ClientTickEvent.class, (ClientTickEvents.Start callback, TickEvent.ClientTickEvent evt) -> {
            if (evt.phase == TickEvent.Phase.START) callback.onStartTick(Minecraft.getInstance());
        });
        INSTANCE.register(ClientTickEvents.End.class, TickEvent.ClientTickEvent.class, (ClientTickEvents.End callback, TickEvent.ClientTickEvent evt) -> {
            if (evt.phase == TickEvent.Phase.END) callback.onEndTick(Minecraft.getInstance());
        });
        INSTANCE.register(RenderGuiCallback.class, RenderGameOverlayEvent.Post.class, (RenderGuiCallback callback, RenderGameOverlayEvent.Post evt) -> {
            if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
            callback.onRenderGui(Minecraft.getInstance(), evt.getMatrixStack(), evt.getPartialTicks(), evt.getWindow().getGuiScaledWidth(), evt.getWindow().getGuiScaledHeight());
        });
        INSTANCE.register(ItemTooltipCallback.class, ItemTooltipEvent.class, (ItemTooltipCallback callback, ItemTooltipEvent evt) -> {
            callback.onItemTooltip(evt.getItemStack(), evt.getPlayer(), evt.getToolTip(), evt.getFlags());
        });
        INSTANCE.register(RenderNameTagCallback.class, RenderNameplateEvent.class, (RenderNameTagCallback callback, RenderNameplateEvent evt) -> {
            DefaultedValue<Component> content = DefaultedValue.fromEvent(evt::setContent, evt::getContent, evt::getOriginalContent);
            EventResult result = callback.onRenderNameTag(evt.getEntity(), content, evt.getEntityRenderer(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight(), evt.getPartialTick());
            if (result.isInterrupt()) evt.setResult(result.getAsBoolean() ? Event.Result.ALLOW : Event.Result.DENY);
        });
        INSTANCE.register(ContainerScreenEvents.Background.class, ContainerScreenEvent.DrawBackground.class, (ContainerScreenEvents.Background callback, ContainerScreenEvent.DrawBackground evt) -> {
            callback.onDrawBackground(evt.getContainerScreen(), evt.getPoseStack(), evt.getMouseX(), evt.getMouseY());
        });
        INSTANCE.register(ContainerScreenEvents.Foreground.class, ContainerScreenEvent.DrawForeground.class, (ContainerScreenEvents.Foreground callback, ContainerScreenEvent.DrawForeground evt) -> {
            callback.onDrawForeground(evt.getContainerScreen(), evt.getPoseStack(), evt.getMouseX(), evt.getMouseY());
        });
        INSTANCE.register(InventoryMobEffectsCallback.class, ScreenEvent.PotionSizeEvent.class, (InventoryMobEffectsCallback callback, ScreenEvent.PotionSizeEvent evt) -> {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) evt.getScreen();
            MutableInt horizontalOffset = MutableInt.fromValue(screen.getGuiLeft() + screen.getXSize() + 2);
            int availableSpace = screen.width - horizontalOffset.getAsInt();
            MutableBoolean fullSizeRendering = MutableBoolean.fromEvent(flag -> {
                evt.setResult(flag ? Event.Result.ALLOW : Event.Result.DENY);
            }, () -> {
                return availableSpace < 120 && evt.getResult() == Event.Result.DEFAULT || evt.getResult() == Event.Result.ALLOW;
            });
            EventResult result = callback.onInventoryMobEffects(evt.getScreen(), availableSpace, fullSizeRendering, horizontalOffset);
//            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(ScreenOpeningCallback.class, ScreenOpenEvent.class, (ScreenOpeningCallback callback, ScreenOpenEvent evt) -> {
            DefaultedValue<Screen> newScreen = DefaultedValue.fromEvent(evt::setScreen, evt::getScreen, evt::getScreen);
            Screen oldScreen = Minecraft.getInstance().screen;
            EventResult result = callback.onScreenOpening(oldScreen, newScreen);
            // setting current screen again already prevents Screen#remove from running as implemented by Forge, but Screen#init still runs again,
            // we just manually fully cancel the event to deal in a more 'proper' way with this, the same is implemented on Fabric
            if (result.isInterrupt() || newScreen.getAsOptional().filter(screen -> screen == oldScreen).isPresent()) evt.setCanceled(true);
        });
        INSTANCE.register(ComputeFovModifierCallback.class, FOVModifierEvent.class, (ComputeFovModifierCallback callback, FOVModifierEvent evt) -> {
            final float fovEffectScale = Minecraft.getInstance().options.fovEffectScale;
            if (fovEffectScale == 0.0F) return;
            // reverse fovEffectScale calculations applied by vanilla in return statement / by Forge when setting up the event
            // this approach is chosen so the callback may work with the actual fov modifier, and does not have to deal with the fovEffectScale option,
            // which is applied automatically regardless
            Consumer<Float> consumer = value -> evt.setNewfov(Mth.lerp(fovEffectScale, 1.0F, value));
            Supplier<Float> supplier = () -> (evt.getNewfov() - 1.0F) / fovEffectScale + 1.0F;
            callback.onComputeFovModifier(evt.getEntity(), DefaultedFloat.fromEvent(consumer, supplier, evt::getFov));
        });
        INSTANCE.register(ScreenEvents.BeforeInit.class, ScreenEvent.InitScreenEvent.Pre.class, (ScreenEvents.BeforeInit callback, ScreenEvent.InitScreenEvent.Pre evt) -> {
            callback.onBeforeInit(Minecraft.getInstance(), evt.getScreen(), evt.getScreen().width, evt.getScreen().height, new ForgeButtonList(evt.getScreen().renderables));
        });
        INSTANCE.register(ScreenEvents.AfterInit.class, ScreenEvent.InitScreenEvent.Post.class, (ScreenEvents.AfterInit callback, ScreenEvent.InitScreenEvent.Post evt) -> {
            callback.onAfterInit(Minecraft.getInstance(), evt.getScreen(), evt.getScreen().width, evt.getScreen().height, new ForgeButtonList(evt.getScreen().renderables), evt::addListener, evt::removeListener);
        });
        registerScreenEvent(ScreenEvents.Remove.class, ScreenCloseEvent.class, (callback, evt) -> {
            callback.onRemove(evt.getScreen());
        });
        registerScreenEvent(ScreenEvents.BeforeRender.class, ScreenEvent.DrawScreenEvent.Pre.class, (callback, evt) -> {
            callback.onBeforeRender(evt.getScreen(), evt.getPoseStack(), evt.getMouseX(), evt.getMouseY(), evt.getPartialTicks());
        });
        registerScreenEvent(ScreenEvents.AfterRender.class, ScreenEvent.DrawScreenEvent.Post.class, (callback, evt) -> {
            callback.onAfterRender(evt.getScreen(), evt.getPoseStack(), evt.getMouseX(), evt.getMouseY(), evt.getPartialTicks());
        });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseClick.class, ScreenEvent.MouseClickedEvent.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeMouseClick(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenMouseEvents.AfterMouseClick.class, ScreenEvent.MouseClickedEvent.Post.class, (callback, evt) -> {
            callback.onAfterMouseClick(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
        });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseRelease.class, ScreenEvent.MouseReleasedEvent.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeMouseRelease(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenMouseEvents.AfterMouseRelease.class, ScreenEvent.MouseReleasedEvent.Post.class, (callback, evt) -> {
            callback.onAfterMouseRelease(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
        });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseScroll.class, ScreenEvent.MouseScrollEvent.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeMouseScroll(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getScrollDelta(), evt.getScrollDelta());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenMouseEvents.AfterMouseScroll.class, ScreenEvent.MouseScrollEvent.Post.class, (callback, evt) -> {
            callback.onAfterMouseScroll(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getScrollDelta(), evt.getScrollDelta());
        });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseDrag.class, ScreenEvent.MouseDragEvent.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeMouseDrag(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getMouseButton(), evt.getDragX(), evt.getDragY());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenMouseEvents.AfterMouseDrag.class, ScreenEvent.MouseDragEvent.Post.class, (callback, evt) -> {
            callback.onAfterMouseDrag(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getMouseButton(), evt.getDragX(), evt.getDragY());
        });
        registerScreenEvent(ScreenKeyboardEvents.BeforeKeyPress.class, ScreenEvent.KeyboardKeyPressedEvent.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeKeyPress(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenKeyboardEvents.AfterKeyPress.class, ScreenEvent.KeyboardKeyPressedEvent.Post.class, (callback, evt) -> {
            callback.onAfterKeyPress(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
        });
        registerScreenEvent(ScreenKeyboardEvents.BeforeKeyRelease.class, ScreenEvent.KeyboardKeyReleasedEvent.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeKeyRelease(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenKeyboardEvents.AfterKeyRelease.class, ScreenEvent.KeyboardKeyReleasedEvent.Post.class, (callback, evt) -> {
            callback.onAfterKeyRelease(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
        });
        INSTANCE.register(RenderGuiElementEvents.Before.class, RenderGameOverlayEvent.PreLayer.class, (RenderGuiElementEvents.Before callback, RenderGameOverlayEvent.PreLayer evt, Object context) -> {
            Objects.requireNonNull(context, "context is null");
            RenderGuiElementEvents.GuiOverlay overlay = (RenderGuiElementEvents.GuiOverlay) context;
            OverlayRegistry.OverlayEntry entry = OverlayRegistry.getEntry(evt.getOverlay());
            Minecraft minecraft = Minecraft.getInstance();
            if (entry == null || !Objects.equals(overlay.id().toString(), entry.getDisplayName()) || !overlay.filter().test(minecraft)) return;
            EventResult result = callback.onBeforeRenderGuiElement(minecraft, evt.getMatrixStack(), evt.getPartialTicks(), evt.getWindow().getGuiScaledWidth(), evt.getWindow().getGuiScaledHeight());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(RenderGuiElementEvents.After.class, RenderGameOverlayEvent.PostLayer.class, (RenderGuiElementEvents.After callback, RenderGameOverlayEvent.PostLayer evt, Object context) -> {
            Objects.requireNonNull(context, "context is null");
            RenderGuiElementEvents.GuiOverlay overlay = (RenderGuiElementEvents.GuiOverlay) context;
            OverlayRegistry.OverlayEntry entry = OverlayRegistry.getEntry(evt.getOverlay());
            Minecraft minecraft = Minecraft.getInstance();
            if (entry == null || !Objects.equals(overlay.id().toString(), entry.getDisplayName()) || !overlay.filter().test(minecraft)) return;
            callback.onAfterRenderGuiElement(minecraft, evt.getMatrixStack(), evt.getPartialTicks(), evt.getWindow().getGuiScaledWidth(), evt.getWindow().getGuiScaledHeight());
        });
        INSTANCE.register(CustomizeChatPanelCallback.class, RenderGameOverlayEvent.Chat.class, (CustomizeChatPanelCallback callback, RenderGameOverlayEvent.Chat evt) -> {
            MutableInt posX = MutableInt.fromEvent(evt::setPosX, evt::getPosX);
            MutableInt posY = MutableInt.fromEvent(evt::setPosY, evt::getPosY);
            callback.onRenderChatPanel(evt.getWindow(), evt.getMatrixStack(), evt.getPartialTicks(), posX, posY);
        });
        INSTANCE.register(ClientEntityLevelEvents.Load.class, EntityJoinWorldEvent.class, (ClientEntityLevelEvents.Load callback, EntityJoinWorldEvent evt) -> {
            if (!evt.getWorld().isClientSide) return;
            EventResult result = callback.onEntityLoad(evt.getEntity(), (ClientLevel) evt.getWorld());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(ClientEntityLevelEvents.Unload.class, EntityLeaveWorldEvent.class, (ClientEntityLevelEvents.Unload callback, EntityLeaveWorldEvent evt) -> {
            if (!evt.getWorld().isClientSide) return;
            callback.onEntityUnload(evt.getEntity(), (ClientLevel) evt.getWorld());
        });
        INSTANCE.register(InputEvents.BeforeMouseAction.class, InputEvent.RawMouseEvent.class, (InputEvents.BeforeMouseAction callback, InputEvent.RawMouseEvent evt) -> {
            EventResult result = callback.onBeforeMouseAction(evt.getButton(), evt.getAction(), evt.getModifiers());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(InputEvents.AfterMouseAction.class, InputEvent.MouseInputEvent.class, (InputEvents.AfterMouseAction callback, InputEvent.MouseInputEvent evt) -> {
            callback.onAfterMouseAction(evt.getButton(), evt.getAction(), evt.getModifiers());
        });
        INSTANCE.register(InputEvents.BeforeMouseScroll.class, InputEvent.MouseScrollEvent.class, (InputEvents.BeforeMouseScroll callback, InputEvent.MouseScrollEvent evt) -> {
            EventResult result = callback.onBeforeMouseScroll(evt.isLeftDown(), evt.isMiddleDown(), evt.isRightDown(), evt.getScrollDelta(), evt.getScrollDelta());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(InputEvents.AfterMouseScroll.class, InputEvent.MouseScrollEvent.class, (InputEvents.AfterMouseScroll callback, InputEvent.MouseScrollEvent evt) -> {
            // Forge doesn't have this, but shouldn't be important really
            callback.onAfterMouseScroll(evt.isLeftDown(), evt.isMiddleDown(), evt.isRightDown(), evt.getScrollDelta(), evt.getScrollDelta());
        });
        INSTANCE.register(InputEvents.BeforeKeyAction.class, InputEvent.KeyInputEvent.class, (InputEvents.BeforeKeyAction callback, InputEvent.KeyInputEvent evt) -> {
            // result is ignored, as before event doesn't exist on Forge, so there is nothing to cancel the input
            callback.onBeforeKeyAction(evt.getKey(), evt.getScanCode(), evt.getAction(), evt.getModifiers());
        });
        INSTANCE.register(InputEvents.AfterKeyAction.class, InputEvent.KeyInputEvent.class, (InputEvents.AfterKeyAction callback, InputEvent.KeyInputEvent evt) -> {
            callback.onAfterKeyAction(evt.getKey(), evt.getScanCode(), evt.getAction(), evt.getModifiers());
        });
        INSTANCE.register(ComputeCameraAnglesCallback.class, EntityViewRenderEvent.CameraSetup.class, (ComputeCameraAnglesCallback callback, EntityViewRenderEvent.CameraSetup evt) -> {
            MutableFloat pitch = MutableFloat.fromEvent(evt::setPitch, evt::getPitch);
            MutableFloat yaw = MutableFloat.fromEvent(evt::setYaw, evt::getYaw);
            MutableFloat roll = MutableFloat.fromEvent(evt::setRoll, evt::getRoll);
            callback.onComputeCameraAngles(evt.getRenderer(), evt.getCamera(), evt.getPitch(), pitch, yaw, roll);
        });
        INSTANCE.register(RenderPlayerEvents.Before.class, RenderPlayerEvent.Pre.class, (RenderPlayerEvents.Before callback, RenderPlayerEvent.Pre evt) -> {
            EventResult result = callback.onBeforeRenderPlayer(evt.getPlayer(), evt.getRenderer(), evt.getPartialTick(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(RenderPlayerEvents.After.class, RenderPlayerEvent.Post.class, (RenderPlayerEvents.After callback, RenderPlayerEvent.Post evt) -> {
            callback.onAfterRenderPlayer(evt.getPlayer(), evt.getRenderer(), evt.getPartialTick(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight());
        });
        INSTANCE.register(RenderHandCallback.class, RenderHandEvent.class, (RenderHandCallback callback, RenderHandEvent evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            EventResult result = callback.onRenderHand(minecraft.player, evt.getHand(), evt.getItemStack(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight(), evt.getPartialTicks(), evt.getInterpolatedPitch(), evt.getSwingProgress(), evt.getEquipProgress());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(ClientLevelTickEvents.Start.class, TickEvent.WorldTickEvent.class, (ClientLevelTickEvents.Start callback, TickEvent.WorldTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.START || !(evt.world instanceof ClientLevel level)) return;
            callback.onStartLevelTick(Minecraft.getInstance(), level);
        });
        INSTANCE.register(ClientLevelTickEvents.End.class, TickEvent.WorldTickEvent.class, (ClientLevelTickEvents.End callback, TickEvent.WorldTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.END || !(evt.world instanceof ClientLevel level)) return;
            callback.onEndLevelTick(Minecraft.getInstance(), level);
        });
        INSTANCE.register(ClientChunkEvents.Load.class, ChunkEvent.Load.class, (ClientChunkEvents.Load callback, ChunkEvent.Load evt) -> {
            if (!(evt.getWorld() instanceof ClientLevel level)) return;
            callback.onChunkLoad(level, evt.getChunk());
        });
        INSTANCE.register(ClientChunkEvents.Unload.class, ChunkEvent.Unload.class, (ClientChunkEvents.Unload callback, ChunkEvent.Unload evt) -> {
            if (!(evt.getWorld() instanceof ClientLevel level)) return;
            callback.onChunkUnload(level, evt.getChunk());
        });
        INSTANCE.register(ClientPlayerEvents.LoggedIn.class, ClientPlayerNetworkEvent.LoggedInEvent.class, (ClientPlayerEvents.LoggedIn callback, ClientPlayerNetworkEvent.LoggedInEvent evt) -> {
            callback.onLoggedIn(evt.getPlayer(), evt.getMultiPlayerGameMode(), evt.getConnection());
        });
        INSTANCE.register(ClientPlayerEvents.LoggedOut.class, ClientPlayerNetworkEvent.LoggedOutEvent.class, (ClientPlayerEvents.LoggedOut callback, ClientPlayerNetworkEvent.LoggedOutEvent evt) -> {
            if (evt.getPlayer() == null || evt.getMultiPlayerGameMode() == null) return;
            Objects.requireNonNull(evt.getConnection(), "connection is null");
            callback.onLoggedOut(evt.getPlayer(), evt.getMultiPlayerGameMode(), evt.getConnection());
        });
        INSTANCE.register(ClientPlayerEvents.Copy.class, ClientPlayerNetworkEvent.RespawnEvent.class, (ClientPlayerEvents.Copy callback, ClientPlayerNetworkEvent.RespawnEvent evt) -> {
            callback.onCopy(evt.getOldPlayer(), evt.getNewPlayer(), evt.getMultiPlayerGameMode(), evt.getConnection());
        });
        INSTANCE.register(BuildCreativeContentsCallback.class, CreativeModeTabContentsEvent.class, (BuildCreativeContentsCallback callback, CreativeModeTabContentsEvent evt) -> {
            ResourceLocation identifier = BuildCreativeContentsCallback.tryCreateIdentifier(evt.getTab());
            callback.onBuildCreativeContents(identifier, evt.getTab(), evt.getOutput());
        });
    }

    private static <T, E extends ScreenEvent> void registerScreenEvent(Class<T> clazz, Class<E> event, BiConsumer<T, E> converter) {
        INSTANCE.register(clazz, event, (callback, evt, context) -> {
            Objects.requireNonNull(context, "context is null");
            if (!((Class<?>) context).isInstance(evt.getScreen())) return;
            converter.accept(callback, evt);
        });
    }

}
