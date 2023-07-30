package fuzs.puzzleslib.impl.client.event;

import com.mojang.blaze3d.shaders.FogShape;
import fuzs.puzzleslib.api.client.event.v1.*;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
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
            if (evt.phase == TickEvent.Phase.START) callback.onStartClientTick(Minecraft.getInstance());
        });
        INSTANCE.register(ClientTickEvents.End.class, TickEvent.ClientTickEvent.class, (ClientTickEvents.End callback, TickEvent.ClientTickEvent evt) -> {
            if (evt.phase == TickEvent.Phase.END) callback.onEndClientTick(Minecraft.getInstance());
        });
        INSTANCE.register(RenderGuiCallback.class, RenderGuiEvent.Post.class, (RenderGuiCallback callback, RenderGuiEvent.Post evt) -> {
            callback.onRenderGui(Minecraft.getInstance(), evt.getGuiGraphics(), evt.getPartialTick(), evt.getWindow().getGuiScaledWidth(), evt.getWindow().getGuiScaledHeight());
        });
        INSTANCE.register(ItemTooltipCallback.class, ItemTooltipEvent.class, (ItemTooltipCallback callback, ItemTooltipEvent evt) -> {
            callback.onItemTooltip(evt.getItemStack(), evt.getEntity(), evt.getToolTip(), evt.getFlags());
        });
        INSTANCE.register(RenderNameTagCallback.class, RenderNameTagEvent.class, (RenderNameTagCallback callback, RenderNameTagEvent evt) -> {
            DefaultedValue<Component> content = DefaultedValue.fromEvent(evt::setContent, evt::getContent, evt::getOriginalContent);
            EventResult result = callback.onRenderNameTag(evt.getEntity(), content, evt.getEntityRenderer(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight(), evt.getPartialTick());
            if (result.isInterrupt()) evt.setResult(result.getAsBoolean() ? Event.Result.ALLOW : Event.Result.DENY);
        });
        INSTANCE.register(ContainerScreenEvents.Background.class, ContainerScreenEvent.Render.Background.class, (ContainerScreenEvents.Background callback, ContainerScreenEvent.Render.Background evt) -> {
            callback.onDrawBackground(evt.getContainerScreen(), evt.getGuiGraphics(), evt.getMouseX(), evt.getMouseY());
        });
        INSTANCE.register(ContainerScreenEvents.Foreground.class, ContainerScreenEvent.Render.Foreground.class, (ContainerScreenEvents.Foreground callback, ContainerScreenEvent.Render.Foreground evt) -> {
            callback.onDrawForeground(evt.getContainerScreen(), evt.getGuiGraphics(), evt.getMouseX(), evt.getMouseY());
        });
        INSTANCE.register(InventoryMobEffectsCallback.class, ScreenEvent.RenderInventoryMobEffects.class, (InventoryMobEffectsCallback callback, ScreenEvent.RenderInventoryMobEffects evt) -> {
            MutableBoolean fullSizeRendering = MutableBoolean.fromEvent(evt::setCompact, evt::isCompact);
            MutableInt horizontalOffset = MutableInt.fromEvent(evt::setHorizontalOffset, evt::getHorizontalOffset);
            EventResult result = callback.onInventoryMobEffects(evt.getScreen(), evt.getAvailableSpace(), fullSizeRendering, horizontalOffset);
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(ScreenOpeningCallback.class, ScreenEvent.Opening.class, (ScreenOpeningCallback callback, ScreenEvent.Opening evt) -> {
            DefaultedValue<Screen> newScreen = DefaultedValue.fromEvent(evt::setNewScreen, evt::getNewScreen, evt::getScreen);
            EventResult result = callback.onScreenOpening(evt.getCurrentScreen(), newScreen);
            // setting current screen again already prevents Screen#remove from running as implemented by Forge, but Screen#init still runs again,
            // we just manually fully cancel the event to deal in a more 'proper' way with this, the same is implemented on Fabric
            if (result.isInterrupt() || newScreen.getAsOptional().filter(screen -> screen == evt.getCurrentScreen()).isPresent()) evt.setCanceled(true);
        });
        INSTANCE.register(ComputeFovModifierCallback.class, ComputeFovModifierEvent.class, (ComputeFovModifierCallback callback, ComputeFovModifierEvent evt) -> {
            final float fovEffectScale = Minecraft.getInstance().options.fovEffectScale().get().floatValue();
            if (fovEffectScale == 0.0F) return;
            // reverse fovEffectScale calculations applied by vanilla in return statement / by Forge when setting up the event
            // this approach is chosen so the callback may work with the actual fov modifier, and does not have to deal with the fovEffectScale option,
            // which is applied automatically regardless
            Consumer<Float> consumer = value -> evt.setNewFovModifier(Mth.lerp(fovEffectScale, 1.0F, value));
            Supplier<Float> supplier = () -> (evt.getNewFovModifier() - 1.0F) / fovEffectScale + 1.0F;
            callback.onComputeFovModifier(evt.getPlayer(), DefaultedFloat.fromEvent(consumer, supplier, evt::getFovModifier));
        });
        INSTANCE.register(ScreenEvents.BeforeInit.class, ScreenEvent.Init.Pre.class, (ScreenEvents.BeforeInit callback, ScreenEvent.Init.Pre evt) -> {
            callback.onBeforeInit(Minecraft.getInstance(), evt.getScreen(), evt.getScreen().width, evt.getScreen().height, new ForgeButtonList(evt.getScreen().renderables));
        });
        INSTANCE.register(ScreenEvents.AfterInit.class, ScreenEvent.Init.Post.class, (ScreenEvents.AfterInit callback, ScreenEvent.Init.Post evt) -> {
            callback.onAfterInit(Minecraft.getInstance(), evt.getScreen(), evt.getScreen().width, evt.getScreen().height, new ForgeButtonList(evt.getScreen().renderables), evt::addListener, evt::removeListener);
        });
        registerScreenEvent(ScreenEvents.Remove.class, ScreenEvent.Closing.class, (callback, evt) -> {
            callback.onRemove(evt.getScreen());
        });
        registerScreenEvent(ScreenEvents.BeforeRender.class, ScreenEvent.Render.Pre.class, (callback, evt) -> {
            callback.onBeforeRender(evt.getScreen(), evt.getGuiGraphics(), evt.getMouseX(), evt.getMouseY(), evt.getPartialTick());
        });
        registerScreenEvent(ScreenEvents.AfterRender.class, ScreenEvent.Render.Post.class, (callback, evt) -> {
            callback.onAfterRender(evt.getScreen(), evt.getGuiGraphics(), evt.getMouseX(), evt.getMouseY(), evt.getPartialTick());
        });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseClick.class, ScreenEvent.MouseButtonPressed.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeMouseClick(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenMouseEvents.AfterMouseClick.class, ScreenEvent.MouseButtonPressed.Post.class, (callback, evt) -> {
            callback.onAfterMouseClick(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
        });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseRelease.class, ScreenEvent.MouseButtonReleased.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeMouseRelease(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenMouseEvents.AfterMouseRelease.class, ScreenEvent.MouseButtonReleased.Post.class, (callback, evt) -> {
            callback.onAfterMouseRelease(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
        });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseScroll.class, ScreenEvent.MouseScrolled.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeMouseScroll(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getScrollDelta(), evt.getScrollDelta());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenMouseEvents.AfterMouseScroll.class, ScreenEvent.MouseScrolled.Post.class, (callback, evt) -> {
            callback.onAfterMouseScroll(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getScrollDelta(), evt.getScrollDelta());
        });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseDrag.class, ScreenEvent.MouseDragged.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeMouseDrag(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getMouseButton(), evt.getDragX(), evt.getDragY());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenMouseEvents.AfterMouseDrag.class, ScreenEvent.MouseDragged.Post.class, (callback, evt) -> {
            callback.onAfterMouseDrag(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getMouseButton(), evt.getDragX(), evt.getDragY());
        });
        registerScreenEvent(ScreenKeyboardEvents.BeforeKeyPress.class, ScreenEvent.KeyPressed.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeKeyPress(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenKeyboardEvents.AfterKeyPress.class, ScreenEvent.KeyPressed.Post.class, (callback, evt) -> {
            callback.onAfterKeyPress(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
        });
        registerScreenEvent(ScreenKeyboardEvents.BeforeKeyRelease.class, ScreenEvent.KeyReleased.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeKeyRelease(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenKeyboardEvents.AfterKeyRelease.class, ScreenEvent.KeyReleased.Post.class, (callback, evt) -> {
            callback.onAfterKeyRelease(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
        });
        INSTANCE.register(RenderGuiElementEvents.Before.class, RenderGuiOverlayEvent.Pre.class, (RenderGuiElementEvents.Before callback, RenderGuiOverlayEvent.Pre evt, Object context) -> {
            Objects.requireNonNull(context, "context is null");
            RenderGuiElementEvents.GuiOverlay overlay = (RenderGuiElementEvents.GuiOverlay) context;
            Minecraft minecraft = Minecraft.getInstance();
            if (!evt.getOverlay().id().equals(overlay.id()) || !overlay.filter().test(minecraft)) return;
            EventResult result = callback.onBeforeRenderGuiElement(minecraft, evt.getGuiGraphics(), evt.getPartialTick(), evt.getWindow().getGuiScaledWidth(), evt.getWindow().getGuiScaledHeight());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(RenderGuiElementEvents.After.class, RenderGuiOverlayEvent.Post.class, (RenderGuiElementEvents.After callback, RenderGuiOverlayEvent.Post evt, Object context) -> {
            Objects.requireNonNull(context, "context is null");
            RenderGuiElementEvents.GuiOverlay overlay = (RenderGuiElementEvents.GuiOverlay) context;
            Minecraft minecraft = Minecraft.getInstance();
            if (!evt.getOverlay().id().equals(overlay.id()) || !overlay.filter().test(minecraft)) return;
            callback.onAfterRenderGuiElement(minecraft, evt.getGuiGraphics(), evt.getPartialTick(), evt.getWindow().getGuiScaledWidth(), evt.getWindow().getGuiScaledHeight());
        });
        INSTANCE.register(CustomizeChatPanelCallback.class, CustomizeGuiOverlayEvent.Chat.class, (CustomizeChatPanelCallback callback, CustomizeGuiOverlayEvent.Chat evt) -> {
            MutableInt posX = MutableInt.fromEvent(evt::setPosX, evt::getPosX);
            MutableInt posY = MutableInt.fromEvent(evt::setPosY, evt::getPosY);
            callback.onRenderChatPanel(evt.getWindow(), evt.getGuiGraphics(), evt.getPartialTick(), posX, posY);
        });
        INSTANCE.register(ClientEntityLevelEvents.Load.class, EntityJoinLevelEvent.class, (ClientEntityLevelEvents.Load callback, EntityJoinLevelEvent evt) -> {
            if (!evt.getLevel().isClientSide) return;
            if (callback.onEntityLoad(evt.getEntity(), (ClientLevel) evt.getLevel()).isInterrupt()) {
                if (evt.getEntity() instanceof Player) {
                    // we do not support players as it isn't as straight-forward to implement for the server event on Fabric
                    throw new UnsupportedOperationException("Cannot prevent player from spawning in!");
                } else {
                    evt.setCanceled(true);
                }
            }
        });
        INSTANCE.register(ClientEntityLevelEvents.Unload.class, EntityLeaveLevelEvent.class, (ClientEntityLevelEvents.Unload callback, EntityLeaveLevelEvent evt) -> {
            if (!evt.getLevel().isClientSide) return;
            callback.onEntityUnload(evt.getEntity(), (ClientLevel) evt.getLevel());
        });
        INSTANCE.register(InputEvents.BeforeMouseAction.class, InputEvent.MouseButton.Pre.class, (InputEvents.BeforeMouseAction callback, InputEvent.MouseButton.Pre evt) -> {
            EventResult result = callback.onBeforeMouseAction(evt.getButton(), evt.getAction(), evt.getModifiers());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(InputEvents.AfterMouseAction.class, InputEvent.MouseButton.Post.class, (InputEvents.AfterMouseAction callback, InputEvent.MouseButton.Post evt) -> {
            callback.onAfterMouseAction(evt.getButton(), evt.getAction(), evt.getModifiers());
        });
        INSTANCE.register(InputEvents.BeforeMouseScroll.class, InputEvent.MouseScrollingEvent.class, (InputEvents.BeforeMouseScroll callback, InputEvent.MouseScrollingEvent evt) -> {
            EventResult result = callback.onBeforeMouseScroll(evt.isLeftDown(), evt.isMiddleDown(), evt.isRightDown(), evt.getScrollDelta(), evt.getScrollDelta());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(InputEvents.AfterMouseScroll.class, InputEvent.MouseScrollingEvent.class, (InputEvents.AfterMouseScroll callback, InputEvent.MouseScrollingEvent evt) -> {
            // Forge doesn't have this, but shouldn't be important really
            callback.onAfterMouseScroll(evt.isLeftDown(), evt.isMiddleDown(), evt.isRightDown(), evt.getScrollDelta(), evt.getScrollDelta());
        });
        INSTANCE.register(InputEvents.BeforeKeyAction.class, InputEvent.Key.class, (InputEvents.BeforeKeyAction callback, InputEvent.Key evt) -> {
            // result is ignored, as before event doesn't exist on Forge, so there is nothing to cancel the input
            callback.onBeforeKeyAction(evt.getKey(), evt.getScanCode(), evt.getAction(), evt.getModifiers());
        });
        INSTANCE.register(InputEvents.AfterKeyAction.class, InputEvent.Key.class, (InputEvents.AfterKeyAction callback, InputEvent.Key evt) -> {
            callback.onAfterKeyAction(evt.getKey(), evt.getScanCode(), evt.getAction(), evt.getModifiers());
        });
        INSTANCE.register(ComputeCameraAnglesCallback.class, ViewportEvent.ComputeCameraAngles.class, (ComputeCameraAnglesCallback callback, ViewportEvent.ComputeCameraAngles evt) -> {
            MutableFloat pitch = MutableFloat.fromEvent(evt::setPitch, evt::getPitch);
            MutableFloat yaw = MutableFloat.fromEvent(evt::setYaw, evt::getYaw);
            MutableFloat roll = MutableFloat.fromEvent(evt::setRoll, evt::getRoll);
            callback.onComputeCameraAngles(evt.getRenderer(), evt.getCamera(), (float) evt.getPartialTick(), pitch, yaw, roll);
        });
        INSTANCE.register(RenderPlayerEvents.Before.class, RenderPlayerEvent.Pre.class, (RenderPlayerEvents.Before callback, RenderPlayerEvent.Pre evt) -> {
            EventResult result = callback.onBeforeRenderPlayer(evt.getEntity(), evt.getRenderer(), evt.getPartialTick(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(RenderPlayerEvents.After.class, RenderPlayerEvent.Post.class, (RenderPlayerEvents.After callback, RenderPlayerEvent.Post evt) -> {
            callback.onAfterRenderPlayer(evt.getEntity(), evt.getRenderer(), evt.getPartialTick(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight());
        });
        INSTANCE.register(RenderHandCallback.class, RenderHandEvent.class, (RenderHandCallback callback, RenderHandEvent evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            EventResult result = callback.onRenderHand(minecraft.player, evt.getHand(), evt.getItemStack(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight(), evt.getPartialTick(), evt.getInterpolatedPitch(), evt.getSwingProgress(), evt.getEquipProgress());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(ClientLevelTickEvents.Start.class, TickEvent.LevelTickEvent.class, (ClientLevelTickEvents.Start callback, TickEvent.LevelTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.START || !(evt.level instanceof ClientLevel level)) return;
            callback.onStartLevelTick(Minecraft.getInstance(), level);
        });
        INSTANCE.register(ClientLevelTickEvents.End.class, TickEvent.LevelTickEvent.class, (ClientLevelTickEvents.End callback, TickEvent.LevelTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.END || !(evt.level instanceof ClientLevel level)) return;
            callback.onEndLevelTick(Minecraft.getInstance(), level);
        });
        INSTANCE.register(ClientChunkEvents.Load.class, ChunkEvent.Load.class, (ClientChunkEvents.Load callback, ChunkEvent.Load evt) -> {
            if (!(evt.getLevel() instanceof ClientLevel level)) return;
            callback.onChunkLoad(level, evt.getChunk());
        });
        INSTANCE.register(ClientChunkEvents.Unload.class, ChunkEvent.Unload.class, (ClientChunkEvents.Unload callback, ChunkEvent.Unload evt) -> {
            if (!(evt.getLevel() instanceof ClientLevel level)) return;
            callback.onChunkUnload(level, evt.getChunk());
        });
        INSTANCE.register(ClientPlayerEvents.LoggedIn.class, ClientPlayerNetworkEvent.LoggingIn.class, (ClientPlayerEvents.LoggedIn callback, ClientPlayerNetworkEvent.LoggingIn evt) -> {
            callback.onLoggedIn(evt.getPlayer(), evt.getMultiPlayerGameMode(), evt.getConnection());
        });
        INSTANCE.register(ClientPlayerEvents.LoggedOut.class, ClientPlayerNetworkEvent.LoggingOut.class, (ClientPlayerEvents.LoggedOut callback, ClientPlayerNetworkEvent.LoggingOut evt) -> {
            if (evt.getPlayer() == null || evt.getMultiPlayerGameMode() == null) return;
            Objects.requireNonNull(evt.getConnection(), "connection is null");
            callback.onLoggedOut(evt.getPlayer(), evt.getMultiPlayerGameMode(), evt.getConnection());
        });
        INSTANCE.register(ClientPlayerEvents.Copy.class, ClientPlayerNetworkEvent.Clone.class, (ClientPlayerEvents.Copy callback, ClientPlayerNetworkEvent.Clone evt) -> {
            callback.onCopy(evt.getOldPlayer(), evt.getNewPlayer(), evt.getMultiPlayerGameMode(), evt.getConnection());
        });
        INSTANCE.register(InteractionInputEvents.Attack.class, InputEvent.InteractionKeyMappingTriggered.class, (InteractionInputEvents.Attack callback, InputEvent.InteractionKeyMappingTriggered evt) -> {
            if (!evt.isAttack()) return;
            Minecraft minecraft = Minecraft.getInstance();
            if (callback.onAttackInteraction(minecraft, minecraft.player).isInterrupt()) {
                // set this to achieve same behavior as Fabric where the methods are cancelled at head without additional processing
                // just manually send swing hand packet if necessary
                evt.setSwingHand(false);
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(ClientLevelEvents.Load.class, LevelEvent.Load.class, (ClientLevelEvents.Load callback, LevelEvent.Load evt) -> {
            if (!(evt.getLevel() instanceof ClientLevel level)) return;
            callback.onLevelLoad(Minecraft.getInstance(), level);
        });
        INSTANCE.register(ClientLevelEvents.Unload.class, LevelEvent.Unload.class, (ClientLevelEvents.Unload callback, LevelEvent.Unload evt) -> {
            if (!(evt.getLevel() instanceof ClientLevel level)) return;
            callback.onLevelUnload(Minecraft.getInstance(), level);
        });
        INSTANCE.register(MovementInputUpdateCallback.class, MovementInputUpdateEvent.class, (MovementInputUpdateCallback callback, MovementInputUpdateEvent evt) -> {
            callback.onMovementInputUpdate((LocalPlayer) evt.getEntity(), evt.getInput());
        });
        INSTANCE.register(ModelEvents.ModifyBakingResult.class, ModelEvent.ModifyBakingResult.class, (ModelEvents.ModifyBakingResult callback, ModelEvent.ModifyBakingResult evt) -> {
            callback.onModifyBakingResult(evt.getModels(), evt::getModelBakery);
        });
        INSTANCE.register(ModelEvents.BakingCompleted.class, ModelEvent.BakingCompleted.class, (ModelEvents.BakingCompleted callback, ModelEvent.BakingCompleted evt) -> {
            callback.onBakingCompleted(evt::getModelManager, evt.getModels(), evt::getModelBakery);
        });
        INSTANCE.register(RenderBlockOverlayCallback.class, RenderBlockScreenEffectEvent.class, (RenderBlockOverlayCallback callback, RenderBlockScreenEffectEvent evt) -> {
            EventResult result = callback.onRenderBlockOverlay((LocalPlayer) evt.getPlayer(), evt.getPoseStack(), evt.getBlockState());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(FogEvents.Render.class, ViewportEvent.RenderFog.class, (FogEvents.Render callback, ViewportEvent.RenderFog evt) -> {
            MutableFloat nearPlaneDistance = MutableFloat.fromEvent(t -> {
                evt.setNearPlaneDistance(t);
                evt.setCanceled(true);
            }, evt::getNearPlaneDistance);
            MutableFloat farPlaneDistance = MutableFloat.fromEvent(t -> {
                evt.setFarPlaneDistance(t);
                evt.setCanceled(true);
            }, evt::getFarPlaneDistance);
            MutableValue<FogShape> fogShape = MutableValue.fromEvent(t -> {
                evt.setFogShape(t);
                evt.setCanceled(true);
            }, evt::getFogShape);
            callback.onRenderFog(evt.getRenderer(), evt.getCamera(), (float) evt.getPartialTick(), evt.getMode(), evt.getType(), nearPlaneDistance, farPlaneDistance, fogShape);
        });
        INSTANCE.register(FogEvents.ComputeColor.class, ViewportEvent.ComputeFogColor.class, (FogEvents.ComputeColor callback, ViewportEvent.ComputeFogColor evt) -> {
            MutableFloat red = MutableFloat.fromEvent(evt::setRed, evt::getRed);
            MutableFloat green = MutableFloat.fromEvent(evt::setGreen, evt::getGreen);
            MutableFloat blue = MutableFloat.fromEvent(evt::setBlue, evt::getBlue);
            callback.onComputeFogColor(evt.getRenderer(), evt.getCamera(), (float) evt.getPartialTick(), red, green, blue);
        });
        INSTANCE.register(ScreenTooltipEvents.Render.class, RenderTooltipEvent.Pre.class, (ScreenTooltipEvents.Render callback, RenderTooltipEvent.Pre evt) -> {
            EventResult result = callback.onRenderTooltip(evt.getGraphics(), evt.getX(), evt.getY(), evt.getScreenWidth(), evt.getScreenHeight(), evt.getFont(), evt.getComponents(), evt.getTooltipPositioner());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(RenderHighlightCallback.class, RenderHighlightEvent.class, (RenderHighlightCallback callback, RenderHighlightEvent evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (!(minecraft.getCameraEntity() instanceof Player) || minecraft.options.hideGui) return;
            EventResult result = callback.onRenderHighlight(evt.getLevelRenderer(), evt.getCamera(), minecraft.gameRenderer, evt.getTarget(), evt.getPartialTick(), evt.getPoseStack(), evt.getMultiBufferSource(), minecraft.level);
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(RenderLevelEvents.AfterTerrain.class, RenderLevelStageEvent.class, (RenderLevelEvents.AfterTerrain callback, RenderLevelStageEvent evt) -> {
            // Forge has multiple stages here, but this is the last one which mirrors Fabric the best
            if (evt.getStage() != RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) return;
            Minecraft minecraft = Minecraft.getInstance();
            callback.onRenderLevelAfterTerrain(evt.getLevelRenderer(), evt.getCamera(), minecraft.gameRenderer, evt.getPartialTick(), evt.getPoseStack(), evt.getProjectionMatrix(), evt.getFrustum(), minecraft.level);
        });
        INSTANCE.register(RenderLevelEvents.AfterEntities.class, RenderLevelStageEvent.class, (RenderLevelEvents.AfterEntities callback, RenderLevelStageEvent evt) -> {
            if (evt.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
            Minecraft minecraft = Minecraft.getInstance();
            callback.onRenderLevelAfterEntities(evt.getLevelRenderer(), evt.getCamera(), minecraft.gameRenderer, evt.getPartialTick(), evt.getPoseStack(), evt.getProjectionMatrix(), evt.getFrustum(), minecraft.level);
        });
        INSTANCE.register(RenderLevelEvents.AfterTranslucent.class, RenderLevelStageEvent.class, (RenderLevelEvents.AfterTranslucent callback, RenderLevelStageEvent evt) -> {
            // Forge has multiple stages here, but this is the last one which mirrors Fabric the best
            if (evt.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
            Minecraft minecraft = Minecraft.getInstance();
            callback.onRenderLevelAfterTranslucent(evt.getLevelRenderer(), evt.getCamera(), minecraft.gameRenderer, evt.getPartialTick(), evt.getPoseStack(), evt.getProjectionMatrix(), evt.getFrustum(), minecraft.level);
        });
        INSTANCE.register(RenderLevelEvents.AfterLevel.class, RenderLevelStageEvent.class, (RenderLevelEvents.AfterLevel callback, RenderLevelStageEvent evt) -> {
            if (evt.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
            Minecraft minecraft = Minecraft.getInstance();
            callback.onRenderLevelAfterLevel(evt.getLevelRenderer(), evt.getCamera(), minecraft.gameRenderer, evt.getPartialTick(), evt.getPoseStack(), evt.getProjectionMatrix(), evt.getFrustum(), minecraft.level);
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
