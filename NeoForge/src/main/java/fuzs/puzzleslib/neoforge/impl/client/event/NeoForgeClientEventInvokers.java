package fuzs.puzzleslib.neoforge.impl.client.event;

import com.mojang.blaze3d.shaders.FogShape;
import fuzs.puzzleslib.api.client.event.v1.*;
import fuzs.puzzleslib.api.client.event.v1.entity.ClientEntityLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.*;
import fuzs.puzzleslib.api.client.event.v1.gui.*;
import fuzs.puzzleslib.api.client.event.v1.level.ClientChunkEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelTickEvents;
import fuzs.puzzleslib.api.client.event.v1.model.ModelBakingCompleteCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.*;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.*;
import fuzs.puzzleslib.impl.client.event.ScreenButtonList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppingEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static fuzs.puzzleslib.neoforge.api.event.v1.core.NeoForgeEventInvokerRegistry.INSTANCE;

@SuppressWarnings("unchecked")
public final class NeoForgeClientEventInvokers {

    public static void registerLoadingHandlers() {
        INSTANCE.register(AddResourcePackReloadListenersCallback.class, AddClientReloadListenersEvent.class, (AddResourcePackReloadListenersCallback callback, AddClientReloadListenersEvent evt) -> {
            callback.onAddResourcePackReloadListeners((ResourceLocation resourceLocation, PreparableReloadListener reloadListener) -> {
                evt.addListener(resourceLocation, ForwardingReloadListenerHelper.fromReloadListener(resourceLocation, reloadListener));
            });
        });
        INSTANCE.register(ScreenOpeningCallback.class, ScreenEvent.Opening.class, (ScreenOpeningCallback callback, ScreenEvent.Opening evt) -> {
            EventResultHolder<Screen> eventResult = callback.onScreenOpening(evt.getCurrentScreen(), evt.getNewScreen());
            // returning the current screen should ideally cause no change at all,
            // which is implemented fine on NeoForge via cancelling the event,
            // on Fabric though the screen will be initialized again, after Screen::remove having been called
            eventResult.ifInterrupt((Screen screen) -> {
                if (screen == evt.getCurrentScreen()) {
                    evt.setCanceled(true);
                } else {
                    evt.setNewScreen(screen);
                }
            });
        });
        INSTANCE.register(ModelBakingCompleteCallback.class, ModelEvent.BakingCompleted.class, (ModelBakingCompleteCallback callback, ModelEvent.BakingCompleted evt) -> {
            callback.onModelBakingComplete(evt.getModelManager(), evt.getBakingResult());
        });
        INSTANCE.register(ExtractRenderStateCallback.class, RegisterRenderStateModifiersEvent.class, (ExtractRenderStateCallback callback, RegisterRenderStateModifiersEvent evt) -> {
            evt.registerEntityModifier((Class<? extends EntityRenderer<? extends Entity, ? extends EntityRenderState>>) (Class<?>) EntityRenderer.class, (Entity entity, EntityRenderState entityRenderState) -> {
                callback.onExtractRenderState(entity, entityRenderState, entityRenderState.partialTick);
            });
        });
        INSTANCE.register(ClientLifecycleEvents.Started.class, ClientStartedEvent.class, (ClientLifecycleEvents.Started callback, ClientStartedEvent evt) -> {
            callback.onClientStarted(Minecraft.getInstance());
        });
        INSTANCE.register(ClientLifecycleEvents.Stopping.class, ClientStoppingEvent.class, (ClientLifecycleEvents.Stopping callback, ClientStoppingEvent evt) -> {
            callback.onClientStopping(evt.getClient());
        });
        INSTANCE.register(ClientSetupCallback.class, FMLClientSetupEvent.class, (ClientSetupCallback callback, FMLClientSetupEvent evt) -> {
            evt.enqueueWork(callback::onClientSetup);
        });
    }

    public static void registerEventHandlers() {
        INSTANCE.register(ClientTickEvents.Start.class, ClientTickEvent.Pre.class, (ClientTickEvents.Start callback, ClientTickEvent.Pre evt) -> {
            callback.onStartClientTick(Minecraft.getInstance());
        });
        INSTANCE.register(ClientTickEvents.End.class, ClientTickEvent.Post.class, (ClientTickEvents.End callback, ClientTickEvent.Post evt) -> {
            callback.onEndClientTick(Minecraft.getInstance());
        });
        INSTANCE.register(RenderGuiEvents.Before.class, RenderGuiEvent.Pre.class, (RenderGuiEvents.Before callback, RenderGuiEvent.Pre evt) -> {
            callback.onBeforeRenderGui(Minecraft.getInstance().gui, evt.getGuiGraphics(), evt.getPartialTick());
        });
        INSTANCE.register(RenderGuiEvents.After.class, RenderGuiEvent.Post.class, (RenderGuiEvents.After callback, RenderGuiEvent.Post evt) -> {
            callback.onAfterRenderGui(Minecraft.getInstance().gui, evt.getGuiGraphics(), evt.getPartialTick());
        });
        INSTANCE.register(ItemTooltipCallback.class, ItemTooltipEvent.class, (ItemTooltipCallback callback, ItemTooltipEvent evt) -> {
            callback.onItemTooltip(evt.getItemStack(), evt.getToolTip(), evt.getContext(), evt.getEntity(), evt.getFlags());
        });
        INSTANCE.register(
                RenderNameTagCallback.class, RenderNameTagEvent.DoRender.class, (RenderNameTagCallback callback, RenderNameTagEvent.DoRender evt) -> {
                    EventResult eventResult = callback.onRenderNameTag(evt.getEntityRenderState(), evt.getContent(), evt.getEntityRenderer(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight());
                    if (eventResult.isInterrupt()) evt.setCanceled(true);
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
            EventResult eventResult = callback.onInventoryMobEffects(evt.getScreen(), evt.getAvailableSpace(), fullSizeRendering, horizontalOffset);
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(ComputeFovModifierCallback.class, ComputeFovModifierEvent.class, (ComputeFovModifierCallback callback, ComputeFovModifierEvent evt) -> {
            float fovEffectScale = Minecraft.getInstance().options.fovEffectScale().get().floatValue();
            if (fovEffectScale == 0.0F) return;
            // reverse fovEffectScale calculations applied by vanilla in return statement / by Forge when setting up the event
            // this approach is chosen so the callback may work with the actual fov modifier, and does not have to deal with the fovEffectScale option,
            // which is applied automatically regardless
            Consumer<Float> consumer = value -> evt.setNewFovModifier(Mth.lerp(fovEffectScale, 1.0F, value));
            Supplier<Float> supplier = () -> (evt.getNewFovModifier() - 1.0F) / fovEffectScale + 1.0F;
            callback.onComputeFovModifier(evt.getPlayer(), DefaultedFloat.fromEvent(consumer, supplier, evt::getFovModifier));
        });
        registerScreenEvent(ScreenEvents.BeforeInit.class, ScreenEvent.Init.Pre.class, (callback, evt) -> {
            callback.onBeforeInit(Minecraft.getInstance(), evt.getScreen(), evt.getScreen().width, evt.getScreen().height, new ScreenButtonList(evt.getScreen().renderables));
        });
        registerScreenEvent(ScreenEvents.AfterInit.class, ScreenEvent.Init.Post.class, (callback, evt) -> {
            callback.onAfterInit(Minecraft.getInstance(), evt.getScreen(), evt.getScreen().width, evt.getScreen().height, new ScreenButtonList(evt.getScreen().renderables), (UnaryOperator<AbstractWidget>) (AbstractWidget abstractWidget) -> {
                evt.addListener(abstractWidget);
                return abstractWidget;
            }, (Consumer<AbstractWidget>) evt::removeListener);
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
            EventResult eventResult = callback.onBeforeMouseClick(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenMouseEvents.AfterMouseClick.class, ScreenEvent.MouseButtonPressed.Post.class, (callback, evt) -> {
            callback.onAfterMouseClick(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
        });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseRelease.class, ScreenEvent.MouseButtonReleased.Pre.class, (callback, evt) -> {
            EventResult eventResult = callback.onBeforeMouseRelease(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenMouseEvents.AfterMouseRelease.class, ScreenEvent.MouseButtonReleased.Post.class, (callback, evt) -> {
            callback.onAfterMouseRelease(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
        });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseScroll.class, ScreenEvent.MouseScrolled.Pre.class, (callback, evt) -> {
            EventResult eventResult = callback.onBeforeMouseScroll(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getScrollDeltaX(), evt.getScrollDeltaY());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenMouseEvents.AfterMouseScroll.class, ScreenEvent.MouseScrolled.Post.class, (callback, evt) -> {
            callback.onAfterMouseScroll(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getScrollDeltaX(), evt.getScrollDeltaY());
        });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseDrag.class, ScreenEvent.MouseDragged.Pre.class, (callback, evt) -> {
            EventResult eventResult = callback.onBeforeMouseDrag(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getMouseButton(), evt.getDragX(), evt.getDragY());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenMouseEvents.AfterMouseDrag.class, ScreenEvent.MouseDragged.Post.class, (callback, evt) -> {
            callback.onAfterMouseDrag(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getMouseButton(), evt.getDragX(), evt.getDragY());
        });
        registerScreenEvent(ScreenKeyboardEvents.BeforeKeyPress.class, ScreenEvent.KeyPressed.Pre.class, (callback, evt) -> {
            EventResult eventResult = callback.onBeforeKeyPress(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenKeyboardEvents.AfterKeyPress.class, ScreenEvent.KeyPressed.Post.class, (callback, evt) -> {
            callback.onAfterKeyPress(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
        });
        registerScreenEvent(ScreenKeyboardEvents.BeforeKeyRelease.class, ScreenEvent.KeyReleased.Pre.class, (callback, evt) -> {
            EventResult eventResult = callback.onBeforeKeyRelease(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenKeyboardEvents.AfterKeyRelease.class, ScreenEvent.KeyReleased.Post.class, (callback, evt) -> {
            callback.onAfterKeyRelease(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
        });
        INSTANCE.register(RenderGuiLayerEvents.Before.class, RenderGuiLayerEvent.Pre.class, (RenderGuiLayerEvents.Before callback, RenderGuiLayerEvent.Pre evt, @Nullable Object context) -> {
            Objects.requireNonNull(context, "context is null");
            ResourceLocation resourceLocation = (ResourceLocation) context;
            if (!evt.getName().equals(resourceLocation) || Minecraft.getInstance().options.hideGui) return;
            EventResult eventResult = callback.onBeforeRenderGuiLayer(Minecraft.getInstance().gui, evt.getGuiGraphics(), evt.getPartialTick());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(RenderGuiLayerEvents.After.class, RenderGuiLayerEvent.Post.class, (RenderGuiLayerEvents.After callback, RenderGuiLayerEvent.Post evt, @Nullable Object context) -> {
            Objects.requireNonNull(context, "context is null");
            ResourceLocation resourceLocation = (ResourceLocation) context;
            if (!evt.getName().equals(resourceLocation) || Minecraft.getInstance().options.hideGui) return;
            callback.onAfterRenderGuiLayer(Minecraft.getInstance().gui, evt.getGuiGraphics(), evt.getPartialTick());
        });
        INSTANCE.register(CustomizeChatPanelCallback.class, CustomizeGuiOverlayEvent.Chat.class, (CustomizeChatPanelCallback callback, CustomizeGuiOverlayEvent.Chat evt) -> {
            MutableInt posX = MutableInt.fromEvent(evt::setPosX, evt::getPosX);
            MutableInt posY = MutableInt.fromEvent(evt::setPosY, evt::getPosY);
            callback.onRenderChatPanel(evt.getGuiGraphics(), evt.getPartialTick(), posX, posY);
        });
        INSTANCE.register(ClientEntityLevelEvents.Load.class, EntityJoinLevelEvent.class, (ClientEntityLevelEvents.Load callback, EntityJoinLevelEvent evt) -> {
            if (!(evt.getLevel() instanceof ClientLevel clientLevel)) return;
            EventResult eventResult = callback.onEntityLoad(evt.getEntity(), clientLevel);
            if (eventResult.isInterrupt()) {
                if (evt.getEntity() instanceof Player) {
                    // we do not support players as it isn't as straight-forward to implement for the server event on Fabric
                    throw new UnsupportedOperationException("Cannot prevent player from spawning in!");
                } else {
                    evt.setCanceled(true);
                }
            }
        });
        INSTANCE.register(ClientEntityLevelEvents.Unload.class, EntityLeaveLevelEvent.class, (ClientEntityLevelEvents.Unload callback, EntityLeaveLevelEvent evt) -> {
            if (!(evt.getLevel() instanceof ClientLevel clientLevel)) return;
            callback.onEntityUnload(evt.getEntity(), clientLevel);
        });
        INSTANCE.register(
                InputEvents.MouseClick.class, InputEvent.MouseButton.Pre.class, (InputEvents.MouseClick callback, InputEvent.MouseButton.Pre evt) -> {
            EventResult eventResult = callback.onMouseClick(evt.getButton(), evt.getAction(), evt.getModifiers());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(
                InputEvents.MouseScroll.class, InputEvent.MouseScrollingEvent.class, (InputEvents.MouseScroll callback, InputEvent.MouseScrollingEvent evt) -> {
            EventResult eventResult = callback.onMouseScroll(evt.isLeftDown(), evt.isMiddleDown(), evt.isRightDown(), evt.getScrollDeltaX(), evt.getScrollDeltaY());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(InputEvents.KeyPress.class, InputEvent.Key.class, (InputEvents.KeyPress callback, InputEvent.Key evt) -> {
            // eventResult is ignored, as before event doesn't exist on NeoForge, so there is nothing to cancel the input
            callback.onKeyPress(evt.getKey(), evt.getScanCode(), evt.getAction(), evt.getModifiers());
        });
        INSTANCE.register(ComputeCameraAnglesCallback.class, ViewportEvent.ComputeCameraAngles.class, (ComputeCameraAnglesCallback callback, ViewportEvent.ComputeCameraAngles evt) -> {
            MutableFloat pitch = MutableFloat.fromEvent(evt::setPitch, evt::getPitch);
            MutableFloat yaw = MutableFloat.fromEvent(evt::setYaw, evt::getYaw);
            MutableFloat roll = MutableFloat.fromEvent(evt::setRoll, evt::getRoll);
            callback.onComputeCameraAngles(evt.getRenderer(), evt.getCamera(), (float) evt.getPartialTick(), pitch, yaw, roll);
        });
        INSTANCE.register(RenderLivingEvents.Before.class, RenderLivingEvent.Pre.class, (callback, evt) -> {
            EventResult eventResult = callback.onBeforeRenderEntity(evt.getRenderState(), evt.getRenderer(), evt.getPartialTick(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(RenderLivingEvents.After.class, RenderLivingEvent.Post.class, (callback, evt) -> {
            callback.onAfterRenderEntity(evt.getRenderState(), evt.getRenderer(), evt.getPartialTick(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight());
        });
        INSTANCE.register(RenderHandEvents.MainHand.class, RenderHandEvent.class, (RenderHandEvents.MainHand callback, RenderHandEvent evt) -> {
            if (evt.getHand() != InteractionHand.MAIN_HAND) return;
            Minecraft minecraft = Minecraft.getInstance();
            ItemInHandRenderer itemInHandRenderer = minecraft.getEntityRenderDispatcher().getItemInHandRenderer();
            EventResult eventResult = callback.onRenderMainHand(itemInHandRenderer, evt.getHand(), minecraft.player, minecraft.player.getMainArm(), evt.getItemStack(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight(), evt.getPartialTick(), evt.getInterpolatedPitch(), evt.getSwingProgress(), evt.getEquipProgress());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(RenderHandEvents.OffHand.class, RenderHandEvent.class, (RenderHandEvents.OffHand callback, RenderHandEvent evt) -> {
            if (evt.getHand() != InteractionHand.OFF_HAND) return;
            Minecraft minecraft = Minecraft.getInstance();
            ItemInHandRenderer itemInHandRenderer = minecraft.getEntityRenderDispatcher().getItemInHandRenderer();
            EventResult eventResult = callback.onRenderOffHand(itemInHandRenderer, evt.getHand(), minecraft.player, minecraft.player.getMainArm().getOpposite(), evt.getItemStack(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight(), evt.getPartialTick(), evt.getInterpolatedPitch(), evt.getSwingProgress(), evt.getEquipProgress());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(ClientLevelTickEvents.Start.class, LevelTickEvent.Pre.class, (ClientLevelTickEvents.Start callback, LevelTickEvent.Pre evt) -> {
            if (!(evt.getLevel() instanceof ClientLevel clientLevel)) return;
            callback.onStartLevelTick(Minecraft.getInstance(), clientLevel);
        });
        INSTANCE.register(ClientLevelTickEvents.End.class, LevelTickEvent.Post.class, (ClientLevelTickEvents.End callback, LevelTickEvent.Post evt) -> {
            if (!(evt.getLevel() instanceof ClientLevel clientLevel)) return;
            callback.onEndLevelTick(Minecraft.getInstance(), clientLevel);
        });
        INSTANCE.register(ClientChunkEvents.Load.class, ChunkEvent.Load.class, (ClientChunkEvents.Load callback, ChunkEvent.Load evt) -> {
            if (!(evt.getLevel() instanceof ClientLevel clientLevel)) return;
            callback.onChunkLoad(clientLevel, evt.getChunk());
        });
        INSTANCE.register(ClientChunkEvents.Unload.class, ChunkEvent.Unload.class, (ClientChunkEvents.Unload callback, ChunkEvent.Unload evt) -> {
            if (!(evt.getLevel() instanceof ClientLevel clientLevel)) return;
            callback.onChunkUnload(clientLevel, evt.getChunk());
        });
        INSTANCE.register(ClientPlayerNetworkEvents.LoggedIn.class, ClientPlayerNetworkEvent.LoggingIn.class, (ClientPlayerNetworkEvents.LoggedIn callback, ClientPlayerNetworkEvent.LoggingIn evt) -> {
            callback.onLoggedIn(evt.getPlayer(), evt.getMultiPlayerGameMode(), evt.getConnection());
        });
        INSTANCE.register(ClientPlayerNetworkEvents.LoggedOut.class, ClientPlayerNetworkEvent.LoggingOut.class, (ClientPlayerNetworkEvents.LoggedOut callback, ClientPlayerNetworkEvent.LoggingOut evt) -> {
            if (evt.getPlayer() == null || evt.getMultiPlayerGameMode() == null) return;
            Objects.requireNonNull(evt.getConnection(), "connection is null");
            callback.onLoggedOut(evt.getPlayer(), evt.getMultiPlayerGameMode(), evt.getConnection());
        });
        INSTANCE.register(ClientPlayerCopyCallback.class, ClientPlayerNetworkEvent.Clone.class, (ClientPlayerCopyCallback callback, ClientPlayerNetworkEvent.Clone evt) -> {
            callback.onCopy(evt.getOldPlayer(), evt.getNewPlayer(), evt.getMultiPlayerGameMode(), evt.getConnection());
        });
        INSTANCE.register(InteractionInputEvents.Attack.class, InputEvent.InteractionKeyMappingTriggered.class, (InteractionInputEvents.Attack callback, InputEvent.InteractionKeyMappingTriggered evt) -> {
            if (!evt.isAttack()) return;
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.hitResult != null) {
                EventResult eventResult = callback.onAttackInteraction(minecraft, minecraft.player, minecraft.hitResult);
                if (eventResult.isInterrupt()) {
                    // set this to achieve same behavior as Fabric where the methods are cancelled at head without additional processing
                    // just manually send swing hand packet if necessary
                    evt.setSwingHand(false);
                    evt.setCanceled(true);
                }
            }
        });
        INSTANCE.register(InteractionInputEvents.Use.class, InputEvent.InteractionKeyMappingTriggered.class, (InteractionInputEvents.Use callback, InputEvent.InteractionKeyMappingTriggered evt) -> {
            if (!evt.isUseItem()) return;
            Minecraft minecraft = Minecraft.getInstance();
            // add in more checks that also run on Fabric
            if (minecraft.hitResult != null && minecraft.player.getItemInHand(evt.getHand()).isItemEnabled(minecraft.level.enabledFeatures())) {
                if (minecraft.hitResult.getType() != HitResult.Type.ENTITY || minecraft.level.getWorldBorder().isWithinBounds(((EntityHitResult) minecraft.hitResult).getEntity().blockPosition())) {
                    EventResult eventResult = callback.onUseInteraction(minecraft, minecraft.player, evt.getHand(), minecraft.hitResult);
                    if (eventResult.isInterrupt()) {
                        // set this to achieve same behavior as Fabric where the methods are cancelled at head without additional processing
                        // just manually send swing hand packet if necessary
                        evt.setSwingHand(false);
                        evt.setCanceled(true);
                    }
                }
            }
        });
        INSTANCE.register(InteractionInputEvents.Pick.class, InputEvent.InteractionKeyMappingTriggered.class, (InteractionInputEvents.Pick callback, InputEvent.InteractionKeyMappingTriggered evt) -> {
            if (!evt.isPickBlock()) return;
            Minecraft minecraft = Minecraft.getInstance();
            EventResult eventResult = callback.onPickInteraction(minecraft, minecraft.player, minecraft.hitResult);
            if (eventResult.isInterrupt()) {
                evt.setCanceled(true);
            }
        });
        INSTANCE.register(ClientLevelEvents.Load.class, LevelEvent.Load.class, (ClientLevelEvents.Load callback, LevelEvent.Load evt) -> {
            if (!(evt.getLevel() instanceof ClientLevel clientLevel)) return;
            callback.onLevelLoad(Minecraft.getInstance(), clientLevel);
        });
        INSTANCE.register(ClientLevelEvents.Unload.class, LevelEvent.Unload.class, (ClientLevelEvents.Unload callback, LevelEvent.Unload evt) -> {
            if (!(evt.getLevel() instanceof ClientLevel clientLevel)) return;
            callback.onLevelUnload(Minecraft.getInstance(), clientLevel);
        });
        INSTANCE.register(MovementInputUpdateCallback.class, MovementInputUpdateEvent.class, (MovementInputUpdateCallback callback, MovementInputUpdateEvent evt) -> {
            callback.onMovementInputUpdate((LocalPlayer) evt.getEntity(), evt.getInput());
        });
        INSTANCE.register(RenderBlockOverlayCallback.class, RenderBlockScreenEffectEvent.class, (RenderBlockOverlayCallback callback, RenderBlockScreenEffectEvent evt) -> {
            EventResult eventResult = callback.onRenderBlockOverlay((LocalPlayer) evt.getPlayer(), evt.getPoseStack(), Minecraft.getInstance().renderBuffers()
                    .bufferSource(), evt.getBlockState());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(FogEvents.Render.class, ViewportEvent.RenderFog.class, (FogEvents.Render callback, ViewportEvent.RenderFog evt) -> {
            MutableFloat nearPlaneDistance = MutableFloat.fromEvent(nearPlaneDistanceValue -> {
                evt.setNearPlaneDistance(nearPlaneDistanceValue);
                evt.setCanceled(true);
            }, evt::getNearPlaneDistance);
            MutableFloat farPlaneDistance = MutableFloat.fromEvent(farPlaneDistanceValue -> {
                evt.setFarPlaneDistance(farPlaneDistanceValue);
                evt.setCanceled(true);
            }, evt::getFarPlaneDistance);
            MutableValue<FogShape> fogShape = MutableValue.fromEvent(fogShapeValue -> {
                evt.setFogShape(fogShapeValue);
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
        INSTANCE.register(RenderTooltipCallback.class, RenderTooltipEvent.Pre.class, (RenderTooltipCallback callback, RenderTooltipEvent.Pre evt) -> {
            EventResult eventResult = callback.onRenderTooltip(evt.getGraphics(), evt.getFont(), evt.getX(), evt.getY(), evt.getComponents(), evt.getTooltipPositioner());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(RenderHighlightCallback.class, RenderHighlightEvent.Block.class, (RenderHighlightCallback callback, RenderHighlightEvent.Block evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (!(minecraft.getCameraEntity() instanceof Player) || minecraft.options.hideGui) return;
            EventResult eventResult = callback.onRenderHighlight(evt.getLevelRenderer(), evt.getCamera(), minecraft.gameRenderer, evt.getTarget(), evt.getDeltaTracker(), evt.getPoseStack(), evt.getMultiBufferSource(), minecraft.level);
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        }, true);
        INSTANCE.register(RenderHighlightCallback.class, RenderHighlightEvent.Entity.class, (RenderHighlightCallback callback, RenderHighlightEvent.Entity evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (!(minecraft.getCameraEntity() instanceof Player) || minecraft.options.hideGui) return;
            callback.onRenderHighlight(evt.getLevelRenderer(), evt.getCamera(), minecraft.gameRenderer, evt.getTarget(), evt.getDeltaTracker(), evt.getPoseStack(), evt.getMultiBufferSource(), minecraft.level);
        }, true);
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
        INSTANCE.register(GameRenderEvents.Before.class, RenderFrameEvent.Pre.class, (GameRenderEvents.Before callback, RenderFrameEvent.Pre evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            callback.onBeforeGameRender(minecraft, minecraft.gameRenderer, evt.getPartialTick());
        });
        INSTANCE.register(GameRenderEvents.After.class, RenderFrameEvent.Post.class, (GameRenderEvents.After callback, RenderFrameEvent.Post evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            callback.onAfterGameRender(minecraft, minecraft.gameRenderer, evt.getPartialTick());
        });
        INSTANCE.register(AddToastCallback.class, ToastAddEvent.class, (AddToastCallback callback, ToastAddEvent evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            EventResult eventResult = callback.onAddToast(minecraft.getToastManager(), evt.getToast());
            if (eventResult.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(GatherDebugTextEvents.Left.class, CustomizeGuiOverlayEvent.DebugText.class, (GatherDebugTextEvents.Left callback, CustomizeGuiOverlayEvent.DebugText evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.getDebugOverlay().showDebugScreen()) return;
            callback.onGatherLeftDebugText(evt.getWindow(), evt.getGuiGraphics(), evt.getPartialTick(), evt.getLeft());
        });
        INSTANCE.register(GatherDebugTextEvents.Right.class, CustomizeGuiOverlayEvent.DebugText.class, (GatherDebugTextEvents.Right callback, CustomizeGuiOverlayEvent.DebugText evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.getDebugOverlay().showDebugScreen()) return;
            callback.onGatherRightDebugText(evt.getWindow(), evt.getGuiGraphics(), evt.getPartialTick(), evt.getRight());
        });
        INSTANCE.register(ComputeFieldOfViewCallback.class, ViewportEvent.ComputeFov.class, (ComputeFieldOfViewCallback callback, ViewportEvent.ComputeFov evt) -> {
            MutableFloat fieldOfView = MutableFloat.fromEvent(evt::setFOV, evt::getFOV);
            callback.onComputeFieldOfView(evt.getRenderer(), evt.getCamera(), (float) evt.getPartialTick(), fieldOfView);
        });
        INSTANCE.register(
                ChatMessageReceivedCallback.class, ClientChatReceivedEvent.class, (ChatMessageReceivedCallback callback, ClientChatReceivedEvent evt) -> {
                    MutableValue<Component> message = MutableValue.fromEvent(evt::setMessage, evt::getMessage);
                    PlayerChatMessage playerChatMessage = evt instanceof ClientChatReceivedEvent.Player player ? player.getPlayerChatMessage() : null;
                    boolean isOverlay = evt instanceof ClientChatReceivedEvent.System system && system.isOverlay();
                    EventResult eventResult = callback.onChatMessageReceived(message, evt.getBoundChatType(),
                            playerChatMessage,
                            isOverlay
                    );
                    if (eventResult.isInterrupt()) evt.setCanceled(true);
                });
        INSTANCE.register(GatherEffectScreenTooltipCallback.class, GatherEffectScreenTooltipsEvent.class, (GatherEffectScreenTooltipCallback callback, GatherEffectScreenTooltipsEvent evt) -> {
            callback.onGatherEffectScreenTooltip(evt.getScreen(), evt.getEffectInstance(), evt.getTooltip());
        });
    }

    private static <T, E extends ScreenEvent> void registerScreenEvent(Class<T> clazz, Class<E> event, BiConsumer<T, E> converter) {
        INSTANCE.register(clazz, event, (T callback, E evt, @Nullable Object context) -> {
            Objects.requireNonNull(context, "context is null");
            if (!((Class<?>) context).isInstance(evt.getScreen())) return;
            converter.accept(callback, evt);
        });
    }
}
