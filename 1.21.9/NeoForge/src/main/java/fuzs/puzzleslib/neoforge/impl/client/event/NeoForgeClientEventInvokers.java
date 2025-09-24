package fuzs.puzzleslib.neoforge.impl.client.event;

import fuzs.puzzleslib.api.client.event.v1.*;
import fuzs.puzzleslib.api.client.event.v1.entity.ClientEntityLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.*;
import fuzs.puzzleslib.api.client.event.v1.gui.*;
import fuzs.puzzleslib.api.client.event.v1.level.ClientChunkEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelTickEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.*;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.impl.client.event.ScreenButtonList;
import fuzs.puzzleslib.impl.event.data.DefaultedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
        INSTANCE.register(ClientSetupCallback.class,
                FMLClientSetupEvent.class,
                (ClientSetupCallback callback, FMLClientSetupEvent event) -> {
                    event.enqueueWork(callback::onClientSetup);
                });
        INSTANCE.register(AddResourcePackReloadListenersCallback.class,
                AddClientReloadListenersEvent.class,
                (AddResourcePackReloadListenersCallback callback, AddClientReloadListenersEvent event) -> {
                    callback.onAddResourcePackReloadListeners(event::addListener);
                });
        INSTANCE.register(ScreenOpeningCallback.class,
                ScreenEvent.Opening.class,
                (ScreenOpeningCallback callback, ScreenEvent.Opening event) -> {
                    EventResultHolder<Screen> eventResult = callback.onScreenOpening(event.getCurrentScreen(),
                            event.getNewScreen());
                    // returning the current screen should ideally cause no change at all,
                    // which is implemented fine on NeoForge via cancelling the event,
                    // on Fabric though the screen will be initialized again, after Screen::remove having been called
                    eventResult.ifInterrupt((Screen screen) -> {
                        if (screen == event.getCurrentScreen()) {
                            event.setCanceled(true);
                        } else {
                            event.setNewScreen(screen);
                        }
                    });
                });
        INSTANCE.register(ExtractRenderStateCallback.class,
                RegisterRenderStateModifiersEvent.class,
                (ExtractRenderStateCallback callback, RegisterRenderStateModifiersEvent event) -> {
                    event.registerEntityModifier((Class<? extends EntityRenderer<? extends Entity, ? extends EntityRenderState>>) (Class<?>) EntityRenderer.class,
                            (Entity entity, EntityRenderState entityRenderState) -> {
                                callback.onExtractRenderState(entity, entityRenderState, entityRenderState.partialTick);
                            });
                });
        INSTANCE.register(ClientLifecycleEvents.Started.class,
                ClientStartedEvent.class,
                (ClientLifecycleEvents.Started callback, ClientStartedEvent event) -> {
                    callback.onClientStarted(Minecraft.getInstance());
                });
        INSTANCE.register(ClientLifecycleEvents.Stopping.class,
                ClientStoppingEvent.class,
                (ClientLifecycleEvents.Stopping callback, ClientStoppingEvent event) -> {
                    callback.onClientStopping(event.getClient());
                });
        INSTANCE.register(DrawItemStackOverlayCallback.class,
                RegisterItemDecorationsEvent.class,
                (DrawItemStackOverlayCallback callback, RegisterItemDecorationsEvent event, @Nullable Object context) -> {
                    Objects.requireNonNull(context, "context is null");
                    Item item = (Item) context;
                    event.register(item,
                            (GuiGraphics guiGraphics, Font font, ItemStack itemStack, int posX, int posY) -> {
                                callback.onDrawItemStackOverlay(guiGraphics, font, itemStack, posX, posY);
                                return false;
                            });
                });
        INSTANCE.register(AddLivingEntityRenderLayersCallback.class,
                EntityRenderersEvent.AddLayers.class,
                (AddLivingEntityRenderLayersCallback callback, EntityRenderersEvent.AddLayers event) -> {
                    for (PlayerModelType playerModelType : PlayerModelType.values()) {
                        AvatarRenderer<?> avatarRenderer = event.getPlayerRenderer(playerModelType);
                        if (avatarRenderer != null) {
                            callback.addLivingEntityRenderLayers(EntityType.PLAYER, avatarRenderer, event.getContext());
                        }
                        avatarRenderer = event.getMannequinRenderer(playerModelType);
                        if (avatarRenderer != null) {
                            callback.addLivingEntityRenderLayers(EntityType.MANNEQUIN,
                                    avatarRenderer,
                                    event.getContext());
                        }
                    }

                    for (EntityType<?> entityType : event.getEntityTypes()) {
                        if (event.getRenderer(entityType) instanceof LivingEntityRenderer<?, ?, ?> entityRenderer) {
                            callback.addLivingEntityRenderLayers(entityType, entityRenderer, event.getContext());
                        }
                    }
                });
    }

    public static void registerEventHandlers() {
        INSTANCE.register(ClientTickEvents.Start.class,
                ClientTickEvent.Pre.class,
                (ClientTickEvents.Start callback, ClientTickEvent.Pre event) -> {
                    callback.onStartClientTick(Minecraft.getInstance());
                });
        INSTANCE.register(ClientTickEvents.End.class,
                ClientTickEvent.Post.class,
                (ClientTickEvents.End callback, ClientTickEvent.Post event) -> {
                    callback.onEndClientTick(Minecraft.getInstance());
                });
        INSTANCE.register(RenderGuiEvents.Before.class,
                RenderGuiEvent.Pre.class,
                (RenderGuiEvents.Before callback, RenderGuiEvent.Pre event) -> {
                    callback.onBeforeRenderGui(event.getGuiGraphics(), event.getPartialTick());
                });
        INSTANCE.register(RenderGuiEvents.After.class,
                RenderGuiEvent.Post.class,
                (RenderGuiEvents.After callback, RenderGuiEvent.Post event) -> {
                    callback.onAfterRenderGui(event.getGuiGraphics(), event.getPartialTick());
                });
        INSTANCE.register(ItemTooltipCallback.class,
                ItemTooltipEvent.class,
                (ItemTooltipCallback callback, ItemTooltipEvent event) -> {
                    callback.onItemTooltip(event.getItemStack(),
                            event.getToolTip(),
                            event.getContext(),
                            event.getEntity(),
                            event.getFlags());
                });
        INSTANCE.register(RenderNameTagCallback.class,
                RenderNameTagEvent.DoRender.class,
                (RenderNameTagCallback callback, RenderNameTagEvent.DoRender event) -> {
                    EventResult eventResult = callback.onRenderNameTag(event.getEntityRenderer(),
                            event.getEntityRenderState(),
                            event.getPoseStack(),
                            event.getSubmitNodeCollector());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        INSTANCE.register(PrepareInventoryMobEffectsCallback.class,
                ScreenEvent.RenderInventoryMobEffects.class,
                (PrepareInventoryMobEffectsCallback callback, ScreenEvent.RenderInventoryMobEffects event) -> {
                    MutableBoolean fullSizeRendering = MutableBoolean.fromEvent(event::setCompact, event::isCompact);
                    MutableInt horizontalOffset = MutableInt.fromEvent(event::setHorizontalOffset,
                            event::getHorizontalOffset);
                    EventResult eventResult = callback.onPrepareInventoryMobEffects(event.getScreen(),
                            event.getAvailableSpace(),
                            fullSizeRendering,
                            horizontalOffset);
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        INSTANCE.register(ComputeFovModifierCallback.class,
                ComputeFovModifierEvent.class,
                (ComputeFovModifierCallback callback, ComputeFovModifierEvent event) -> {
                    float fovEffectScale = Minecraft.getInstance().options.fovEffectScale().get().floatValue();
                    if (fovEffectScale == 0.0F) return;
                    // reverse fovEffectScale calculations applied by vanilla in return statement / by Forge when setting up the event
                    // this approach is chosen so the callback may work with the actual fov modifier, and does not have to deal with the fovEffectScale option,
                    // which is applied automatically regardless
                    Consumer<Float> consumer = value -> event.setNewFovModifier(Mth.lerp(fovEffectScale, 1.0F, value));
                    Supplier<Float> supplier = () -> (event.getNewFovModifier() - 1.0F) / fovEffectScale + 1.0F;
                    callback.onComputeFovModifier(event.getPlayer(),
                            DefaultedFloat.fromEvent(consumer, supplier, event::getFovModifier));
                });
        registerScreenEvent(ScreenEvents.BeforeInit.class, ScreenEvent.Init.Pre.class, (callback, event) -> {
            callback.onBeforeInit(Minecraft.getInstance(),
                    event.getScreen(),
                    event.getScreen().width,
                    event.getScreen().height,
                    new ScreenButtonList(event.getScreen().renderables));
        });
        registerScreenEvent(ScreenEvents.AfterInit.class, ScreenEvent.Init.Post.class, (callback, event) -> {
            callback.onAfterInit(Minecraft.getInstance(),
                    event.getScreen(),
                    event.getScreen().width,
                    event.getScreen().height,
                    new ScreenButtonList(event.getScreen().renderables),
                    (UnaryOperator<AbstractWidget>) (AbstractWidget abstractWidget) -> {
                        event.addListener(abstractWidget);
                        return abstractWidget;
                    },
                    (Consumer<AbstractWidget>) event::removeListener);
        });
        registerScreenEvent(ScreenEvents.Remove.class, ScreenEvent.Closing.class, (callback, event) -> {
            callback.onRemove(event.getScreen());
        });
        registerScreenEvent(ScreenEvents.BeforeRender.class, ScreenEvent.Render.Pre.class, (callback, event) -> {
            callback.onBeforeRender(event.getScreen(),
                    event.getGuiGraphics(),
                    event.getMouseX(),
                    event.getMouseY(),
                    event.getPartialTick());
        });
        registerScreenEvent(ScreenEvents.AfterBackground.class,
                ScreenEvent.Render.Background.class,
                (callback, event) -> {
                    callback.onAfterBackground(event.getScreen(),
                            event.getGuiGraphics(),
                            event.getMouseX(),
                            event.getMouseY(),
                            event.getPartialTick());
                });
        registerScreenEvent(ScreenEvents.AfterRender.class, ScreenEvent.Render.Post.class, (callback, event) -> {
            callback.onAfterRender(event.getScreen(),
                    event.getGuiGraphics(),
                    event.getMouseX(),
                    event.getMouseY(),
                    event.getPartialTick());
        });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseClick.class,
                ScreenEvent.MouseButtonPressed.Pre.class,
                (callback, event) -> {
                    EventResult eventResult = callback.onBeforeMouseClick(event.getScreen(),
                            event.getMouseButtonEvent());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        registerScreenEvent(ScreenMouseEvents.AfterMouseClick.class,
                ScreenEvent.MouseButtonPressed.Post.class,
                (callback, event) -> {
                    callback.onAfterMouseClick(event.getScreen(), event.getMouseButtonEvent());
                });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseRelease.class,
                ScreenEvent.MouseButtonReleased.Pre.class,
                (callback, event) -> {
                    EventResult eventResult = callback.onBeforeMouseRelease(event.getScreen(),
                            event.getMouseButtonEvent());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        registerScreenEvent(ScreenMouseEvents.AfterMouseRelease.class,
                ScreenEvent.MouseButtonReleased.Post.class,
                (callback, event) -> {
                    callback.onAfterMouseRelease(event.getScreen(), event.getMouseButtonEvent());
                });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseScroll.class,
                ScreenEvent.MouseScrolled.Pre.class,
                (callback, event) -> {
                    EventResult eventResult = callback.onBeforeMouseScroll(event.getScreen(),
                            event.getMouseX(),
                            event.getMouseY(),
                            event.getScrollDeltaX(),
                            event.getScrollDeltaY());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        registerScreenEvent(ScreenMouseEvents.AfterMouseScroll.class,
                ScreenEvent.MouseScrolled.Post.class,
                (callback, event) -> {
                    callback.onAfterMouseScroll(event.getScreen(),
                            event.getMouseX(),
                            event.getMouseY(),
                            event.getScrollDeltaX(),
                            event.getScrollDeltaY());
                });
        registerScreenEvent(ScreenMouseEvents.BeforeMouseDrag.class,
                ScreenEvent.MouseDragged.Pre.class,
                (callback, event) -> {
                    EventResult eventResult = callback.onBeforeMouseDrag(event.getScreen(),
                            event.getMouseButtonEvent(),
                            event.getDragX(),
                            event.getDragY());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        registerScreenEvent(ScreenMouseEvents.AfterMouseDrag.class,
                ScreenEvent.MouseDragged.Post.class,
                (callback, event) -> {
                    callback.onAfterMouseDrag(event.getScreen(),
                            event.getMouseButtonEvent(),
                            event.getDragX(),
                            event.getDragY());
                });
        registerScreenEvent(ScreenKeyboardEvents.BeforeKeyPress.class,
                ScreenEvent.KeyPressed.Pre.class,
                (callback, event) -> {
                    EventResult eventResult = callback.onBeforeKeyPress(event.getScreen(), event.getKeyEvent());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        registerScreenEvent(ScreenKeyboardEvents.AfterKeyPress.class,
                ScreenEvent.KeyPressed.Post.class,
                (callback, event) -> {
                    callback.onAfterKeyPress(event.getScreen(), event.getKeyEvent());
                });
        registerScreenEvent(ScreenKeyboardEvents.BeforeKeyRelease.class,
                ScreenEvent.KeyReleased.Pre.class,
                (callback, event) -> {
                    EventResult eventResult = callback.onBeforeKeyRelease(event.getScreen(), event.getKeyEvent());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        registerScreenEvent(ScreenKeyboardEvents.AfterKeyRelease.class,
                ScreenEvent.KeyReleased.Post.class,
                (callback, event) -> {
                    callback.onAfterKeyRelease(event.getScreen(), event.getKeyEvent());
                });
        INSTANCE.register(RenderContainerScreenContentsCallback.class,
                ContainerScreenEvent.Render.Foreground.class,
                (RenderContainerScreenContentsCallback callback, ContainerScreenEvent.Render.Foreground event) -> {
                    callback.onRenderContainerScreenContents(event.getContainerScreen(),
                            event.getGuiGraphics(),
                            event.getMouseX(),
                            event.getMouseY());
                });
        INSTANCE.register(CustomizeChatPanelCallback.class,
                CustomizeGuiOverlayEvent.Chat.class,
                (CustomizeChatPanelCallback callback, CustomizeGuiOverlayEvent.Chat event) -> {
                    MutableInt posX = MutableInt.fromEvent(event::setPosX, event::getPosX);
                    MutableInt posY = MutableInt.fromEvent(event::setPosY, event::getPosY);
                    callback.onRenderChatPanel(event.getGuiGraphics(), event.getPartialTick(), posX, posY);
                });
        INSTANCE.register(ClientEntityLevelEvents.Load.class,
                EntityJoinLevelEvent.class,
                (ClientEntityLevelEvents.Load callback, EntityJoinLevelEvent event) -> {
                    if (!(event.getLevel() instanceof ClientLevel clientLevel)) return;
                    EventResult eventResult = callback.onEntityLoad(event.getEntity(), clientLevel);
                    if (eventResult.isInterrupt()) {
                        if (event.getEntity() instanceof Player) {
                            // we do not support players as it isn't as straight-forward to implement for the server event on Fabric
                            throw new UnsupportedOperationException("Cannot prevent player from spawning in!");
                        } else {
                            event.setCanceled(true);
                        }
                    }
                });
        INSTANCE.register(ClientEntityLevelEvents.Unload.class,
                EntityLeaveLevelEvent.class,
                (ClientEntityLevelEvents.Unload callback, EntityLeaveLevelEvent event) -> {
                    if (!(event.getLevel() instanceof ClientLevel clientLevel)) return;
                    callback.onEntityUnload(event.getEntity(), clientLevel);
                });
        INSTANCE.register(InputEvents.MouseClick.class,
                InputEvent.MouseButton.Pre.class,
                (InputEvents.MouseClick callback, InputEvent.MouseButton.Pre event) -> {
                    EventResult eventResult = callback.onMouseClick(event.getMouseButtonInfo(), event.getAction());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        INSTANCE.register(InputEvents.MouseScroll.class,
                InputEvent.MouseScrollingEvent.class,
                (InputEvents.MouseScroll callback, InputEvent.MouseScrollingEvent event) -> {
                    EventResult eventResult = callback.onMouseScroll(event.isLeftDown(),
                            event.isMiddleDown(),
                            event.isRightDown(),
                            event.getScrollDeltaX(),
                            event.getScrollDeltaY());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        INSTANCE.register(InputEvents.KeyPress.class,
                InputEvent.Key.class,
                (InputEvents.KeyPress callback, InputEvent.Key event) -> {
                    // eventResult is ignored, as before event doesn't exist on NeoForge, so there is nothing to cancel the input
                    callback.onKeyPress(event.getKeyEvent(), event.getAction());
                });
        INSTANCE.register(ComputeCameraAnglesCallback.class,
                ViewportEvent.ComputeCameraAngles.class,
                (ComputeCameraAnglesCallback callback, ViewportEvent.ComputeCameraAngles event) -> {
                    MutableFloat pitch = MutableFloat.fromEvent(event::setPitch, event::getPitch);
                    MutableFloat yaw = MutableFloat.fromEvent(event::setYaw, event::getYaw);
                    MutableFloat roll = MutableFloat.fromEvent(event::setRoll, event::getRoll);
                    callback.onComputeCameraAngles(event.getRenderer(),
                            event.getCamera(),
                            (float) event.getPartialTick(),
                            pitch,
                            yaw,
                            roll);
                });
        INSTANCE.register(RenderLivingEvents.Before.class, RenderLivingEvent.Pre.class, (callback, event) -> {
            EventResult eventResult = callback.onBeforeRenderEntity(event.getRenderState(),
                    event.getRenderer(),
                    event.getPartialTick(),
                    event.getPoseStack(),
                    event.getSubmitNodeCollector());
            if (eventResult.isInterrupt()) event.setCanceled(true);
        });
        INSTANCE.register(RenderLivingEvents.After.class, RenderLivingEvent.Post.class, (callback, event) -> {
            callback.onAfterRenderEntity(event.getRenderState(),
                    event.getRenderer(),
                    event.getPartialTick(),
                    event.getPoseStack(),
                    event.getSubmitNodeCollector());
        });
        INSTANCE.register(RenderHandEvents.MainHand.class,
                RenderHandEvent.class,
                (RenderHandEvents.MainHand callback, RenderHandEvent event) -> {
                    if (event.getHand() != InteractionHand.MAIN_HAND) return;
                    Minecraft minecraft = Minecraft.getInstance();
                    ItemInHandRenderer itemInHandRenderer = minecraft.getEntityRenderDispatcher()
                            .getItemInHandRenderer();
                    EventResult eventResult = callback.onRenderMainHand(itemInHandRenderer,
                            event.getHand(),
                            minecraft.player,
                            minecraft.player.getMainArm(),
                            event.getItemStack(),
                            event.getPoseStack(),
                            event.getSubmitNodeCollector(),
                            event.getPackedLight(),
                            event.getPartialTick(),
                            event.getInterpolatedPitch(),
                            event.getSwingProgress(),
                            event.getEquipProgress());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        INSTANCE.register(RenderHandEvents.OffHand.class,
                RenderHandEvent.class,
                (RenderHandEvents.OffHand callback, RenderHandEvent event) -> {
                    if (event.getHand() != InteractionHand.OFF_HAND) return;
                    Minecraft minecraft = Minecraft.getInstance();
                    ItemInHandRenderer itemInHandRenderer = minecraft.getEntityRenderDispatcher()
                            .getItemInHandRenderer();
                    EventResult eventResult = callback.onRenderOffHand(itemInHandRenderer,
                            event.getHand(),
                            minecraft.player,
                            minecraft.player.getMainArm().getOpposite(),
                            event.getItemStack(),
                            event.getPoseStack(),
                            event.getSubmitNodeCollector(),
                            event.getPackedLight(),
                            event.getPartialTick(),
                            event.getInterpolatedPitch(),
                            event.getSwingProgress(),
                            event.getEquipProgress());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        INSTANCE.register(ClientLevelTickEvents.Start.class,
                LevelTickEvent.Pre.class,
                (ClientLevelTickEvents.Start callback, LevelTickEvent.Pre event) -> {
                    if (!(event.getLevel() instanceof ClientLevel clientLevel)) return;
                    callback.onStartLevelTick(Minecraft.getInstance(), clientLevel);
                });
        INSTANCE.register(ClientLevelTickEvents.End.class,
                LevelTickEvent.Post.class,
                (ClientLevelTickEvents.End callback, LevelTickEvent.Post event) -> {
                    if (!(event.getLevel() instanceof ClientLevel clientLevel)) return;
                    callback.onEndLevelTick(Minecraft.getInstance(), clientLevel);
                });
        INSTANCE.register(ClientChunkEvents.Load.class,
                ChunkEvent.Load.class,
                (ClientChunkEvents.Load callback, ChunkEvent.Load event) -> {
                    if (!(event.getLevel() instanceof ClientLevel clientLevel)) return;
                    callback.onChunkLoad(clientLevel, event.getChunk());
                });
        INSTANCE.register(ClientChunkEvents.Unload.class,
                ChunkEvent.Unload.class,
                (ClientChunkEvents.Unload callback, ChunkEvent.Unload event) -> {
                    if (!(event.getLevel() instanceof ClientLevel clientLevel)) return;
                    callback.onChunkUnload(clientLevel, event.getChunk());
                });
        INSTANCE.register(ClientPlayerNetworkEvents.LoggedIn.class,
                ClientPlayerNetworkEvent.LoggingIn.class,
                (ClientPlayerNetworkEvents.LoggedIn callback, ClientPlayerNetworkEvent.LoggingIn event) -> {
                    callback.onLoggedIn(event.getPlayer(), event.getMultiPlayerGameMode(), event.getConnection());
                });
        INSTANCE.register(ClientPlayerNetworkEvents.LoggedOut.class,
                ClientPlayerNetworkEvent.LoggingOut.class,
                (ClientPlayerNetworkEvents.LoggedOut callback, ClientPlayerNetworkEvent.LoggingOut event) -> {
                    if (event.getPlayer() == null || event.getMultiPlayerGameMode() == null) return;
                    Objects.requireNonNull(event.getConnection(), "connection is null");
                    callback.onLoggedOut(event.getPlayer(), event.getMultiPlayerGameMode(), event.getConnection());
                });
        INSTANCE.register(ClientPlayerCopyCallback.class,
                ClientPlayerNetworkEvent.Clone.class,
                (ClientPlayerCopyCallback callback, ClientPlayerNetworkEvent.Clone event) -> {
                    callback.onCopy(event.getOldPlayer(),
                            event.getNewPlayer(),
                            event.getMultiPlayerGameMode(),
                            event.getConnection());
                });
        INSTANCE.register(InteractionInputEvents.Attack.class,
                InputEvent.InteractionKeyMappingTriggered.class,
                (InteractionInputEvents.Attack callback, InputEvent.InteractionKeyMappingTriggered event) -> {
                    if (!event.isAttack()) return;
                    Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft.hitResult != null) {
                        EventResult eventResult = callback.onAttackInteraction(minecraft,
                                minecraft.player,
                                minecraft.hitResult);
                        if (eventResult.isInterrupt()) {
                            // set this to achieve same behavior as Fabric where the methods are cancelled at head without additional processing
                            // just manually send swing hand packet if necessary
                            event.setSwingHand(false);
                            event.setCanceled(true);
                        }
                    }
                });
        INSTANCE.register(InteractionInputEvents.Use.class,
                InputEvent.InteractionKeyMappingTriggered.class,
                (InteractionInputEvents.Use callback, InputEvent.InteractionKeyMappingTriggered event) -> {
                    if (!event.isUseItem()) return;
                    Minecraft minecraft = Minecraft.getInstance();
                    // add in more checks that also run on Fabric
                    if (minecraft.hitResult != null && minecraft.player.getItemInHand(event.getHand())
                            .isItemEnabled(minecraft.level.enabledFeatures())) {
                        if (minecraft.hitResult.getType() != HitResult.Type.ENTITY || minecraft.level.getWorldBorder()
                                .isWithinBounds(((EntityHitResult) minecraft.hitResult).getEntity().blockPosition())) {
                            EventResult eventResult = callback.onUseInteraction(minecraft,
                                    minecraft.player,
                                    event.getHand(),
                                    minecraft.hitResult);
                            if (eventResult.isInterrupt()) {
                                // set this to achieve same behavior as Fabric where the methods are cancelled at head without additional processing
                                // just manually send swing hand packet if necessary
                                event.setSwingHand(false);
                                event.setCanceled(true);
                            }
                        }
                    }
                });
        INSTANCE.register(InteractionInputEvents.Pick.class,
                InputEvent.InteractionKeyMappingTriggered.class,
                (InteractionInputEvents.Pick callback, InputEvent.InteractionKeyMappingTriggered event) -> {
                    if (!event.isPickBlock()) return;
                    Minecraft minecraft = Minecraft.getInstance();
                    EventResult eventResult = callback.onPickInteraction(minecraft,
                            minecraft.player,
                            minecraft.hitResult);
                    if (eventResult.isInterrupt()) {
                        event.setCanceled(true);
                    }
                });
        INSTANCE.register(ClientLevelEvents.Load.class,
                LevelEvent.Load.class,
                (ClientLevelEvents.Load callback, LevelEvent.Load event) -> {
                    if (!(event.getLevel() instanceof ClientLevel clientLevel)) return;
                    callback.onLevelLoad(Minecraft.getInstance(), clientLevel);
                });
        INSTANCE.register(ClientLevelEvents.Unload.class,
                LevelEvent.Unload.class,
                (ClientLevelEvents.Unload callback, LevelEvent.Unload event) -> {
                    if (!(event.getLevel() instanceof ClientLevel clientLevel)) return;
                    callback.onLevelUnload(Minecraft.getInstance(), clientLevel);
                });
        INSTANCE.register(MovementInputUpdateCallback.class,
                MovementInputUpdateEvent.class,
                (MovementInputUpdateCallback callback, MovementInputUpdateEvent event) -> {
                    callback.onMovementInputUpdate((LocalPlayer) event.getEntity(), event.getInput());
                });
        INSTANCE.register(RenderBlockOverlayCallback.class,
                RenderBlockScreenEffectEvent.class,
                (RenderBlockOverlayCallback callback, RenderBlockScreenEffectEvent event) -> {
                    EventResult eventResult = callback.onRenderBlockOverlay((LocalPlayer) event.getPlayer(),
                            event.getPoseStack(),
                            Minecraft.getInstance().renderBuffers().bufferSource(),
                            event.getBlockState());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        INSTANCE.register(FogEvents.Setup.class,
                ViewportEvent.RenderFog.class,
                (FogEvents.Setup callback, ViewportEvent.RenderFog event) -> {
                    callback.onSetupFog(event.getCamera(),
                            (float) event.getPartialTick(),
                            event.getEnvironment(),
                            event.getType(),
                            event.getFogData());
                });
        INSTANCE.register(FogEvents.Color.class,
                ViewportEvent.ComputeFogColor.class,
                (FogEvents.Color callback, ViewportEvent.ComputeFogColor event) -> {
                    MutableFloat red = MutableFloat.fromEvent(event::setRed, event::getRed);
                    MutableFloat green = MutableFloat.fromEvent(event::setGreen, event::getGreen);
                    MutableFloat blue = MutableFloat.fromEvent(event::setBlue, event::getBlue);
                    callback.onComputeFogColor(event.getCamera(), (float) event.getPartialTick(), red, green, blue);
                });
        INSTANCE.register(RenderTooltipCallback.class,
                RenderTooltipEvent.Pre.class,
                (RenderTooltipCallback callback, RenderTooltipEvent.Pre event) -> {
                    EventResult eventResult = callback.onRenderTooltip(event.getGraphics(),
                            event.getFont(),
                            event.getX(),
                            event.getY(),
                            event.getComponents(),
                            event.getTooltipPositioner());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        INSTANCE.register(GameRenderEvents.Before.class,
                RenderFrameEvent.Pre.class,
                (GameRenderEvents.Before callback, RenderFrameEvent.Pre event) -> {
                    Minecraft minecraft = Minecraft.getInstance();
                    callback.onBeforeGameRender(minecraft, minecraft.gameRenderer, event.getPartialTick());
                });
        INSTANCE.register(GameRenderEvents.After.class,
                RenderFrameEvent.Post.class,
                (GameRenderEvents.After callback, RenderFrameEvent.Post event) -> {
                    Minecraft minecraft = Minecraft.getInstance();
                    callback.onAfterGameRender(minecraft, minecraft.gameRenderer, event.getPartialTick());
                });
        INSTANCE.register(AddToastCallback.class,
                ToastAddEvent.class,
                (AddToastCallback callback, ToastAddEvent event) -> {
                    Minecraft minecraft = Minecraft.getInstance();
                    EventResult eventResult = callback.onAddToast(minecraft.getToastManager(), event.getToast());
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        INSTANCE.register(ComputeFieldOfViewCallback.class,
                ViewportEvent.ComputeFov.class,
                (ComputeFieldOfViewCallback callback, ViewportEvent.ComputeFov event) -> {
                    MutableFloat fieldOfView = MutableFloat.fromEvent(event::setFOV, event::getFOV);
                    callback.onComputeFieldOfView(event.getRenderer(),
                            event.getCamera(),
                            (float) event.getPartialTick(),
                            fieldOfView);
                });
        INSTANCE.register(ChatMessageReceivedCallback.class,
                ClientChatReceivedEvent.class,
                (ChatMessageReceivedCallback callback, ClientChatReceivedEvent event) -> {
                    MutableValue<Component> message = MutableValue.fromEvent(event::setMessage, event::getMessage);
                    PlayerChatMessage playerChatMessage =
                            event instanceof ClientChatReceivedEvent.Player player ? player.getPlayerChatMessage() :
                                    null;
                    boolean isOverlay = event instanceof ClientChatReceivedEvent.System system && system.isOverlay();
                    EventResult eventResult = callback.onChatMessageReceived(message,
                            event.getBoundChatType(),
                            playerChatMessage,
                            isOverlay);
                    if (eventResult.isInterrupt()) event.setCanceled(true);
                });
        INSTANCE.register(GatherEffectScreenTooltipCallback.class,
                GatherEffectScreenTooltipsEvent.class,
                (GatherEffectScreenTooltipCallback callback, GatherEffectScreenTooltipsEvent event) -> {
                    callback.onGatherEffectScreenTooltip(event.getScreen(),
                            event.getEffectInstance(),
                            event.getTooltip());
                });
    }

    private static <T, E extends ScreenEvent> void registerScreenEvent(Class<T> clazz, Class<E> eventClazz, BiConsumer<T, E> converter) {
        INSTANCE.register(clazz, eventClazz, (T callback, E event, @Nullable Object context) -> {
            Objects.requireNonNull(context, "context is null");
            if (!((Class<?>) context).isInstance(event.getScreen())) return;
            converter.accept(callback, event);
        });
    }
}
