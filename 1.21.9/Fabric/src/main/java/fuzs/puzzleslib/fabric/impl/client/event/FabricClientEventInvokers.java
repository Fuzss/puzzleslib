package fuzs.puzzleslib.fabric.impl.client.event;

import fuzs.puzzleslib.api.client.event.v1.*;
import fuzs.puzzleslib.api.client.event.v1.entity.ClientEntityLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.*;
import fuzs.puzzleslib.api.client.event.v1.gui.*;
import fuzs.puzzleslib.api.client.event.v1.level.ClientChunkEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelTickEvents;
import fuzs.puzzleslib.api.client.event.v1.model.ModelBakingEvents;
import fuzs.puzzleslib.api.client.event.v1.model.ModelLoadingEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.*;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.fabric.api.client.event.v1.*;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLifecycleEvents;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.event.data.DefaultedInt;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventInvokerRegistry.INSTANCE;

@SuppressWarnings("unchecked")
public final class FabricClientEventInvokers {

    public static void registerLoadingHandlers() {
        INSTANCE.register(AddResourcePackReloadListenersCallback.class,
                FabricLifecycleEvents.LOAD_COMPLETE,
                (AddResourcePackReloadListenersCallback callback) -> {
                    return () -> {
                        callback.onAddResourcePackReloadListeners((ResourceLocation resourceLocation, PreparableReloadListener reloadListener) -> {
                            ResourceLoader.get(PackType.CLIENT_RESOURCES)
                                    .registerReloader(resourceLocation, reloadListener);
                        });
                    };
                });
        INSTANCE.register(ScreenOpeningCallback.class, FabricGuiEvents.SCREEN_OPENING);
        INSTANCE.register(ModelLoadingEvents.LoadModel.class,
                (ModelLoadingEvents.LoadModel callback, @Nullable Object o) -> {
                    ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                        pluginContext.modifyModelOnLoad()
                                .register(ModelModifier.OVERRIDE_PHASE,
                                        (UnbakedModel model, ModelModifier.OnLoad.Context context) -> {
                                            EventResultHolder<UnbakedModel> eventResult = callback.onLoadModel(context.id(),
                                                    model);
                                            return eventResult.getInterrupt().orElse(model);
                                        });
                    });
                });
        INSTANCE.register(ModelBakingEvents.BeforeItem.class,
                (ModelBakingEvents.BeforeItem callback, @Nullable Object o) -> {
                    ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                        pluginContext.modifyItemModelBeforeBake()
                                .register(ModelModifier.OVERRIDE_PHASE,
                                        (ItemModel.Unbaked model, ModelModifier.BeforeBakeItem.Context context) -> {
                                            EventResultHolder<ItemModel.Unbaked> eventResult = callback.onBeforeBakeItem(
                                                    context.itemId(),
                                                    model,
                                                    context.bakeContext());
                                            return eventResult.getInterrupt().orElse(model);
                                        });
                    });
                });
        INSTANCE.register(ModelBakingEvents.AfterItem.class,
                (ModelBakingEvents.AfterItem callback, @Nullable Object o) -> {
                    ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                        pluginContext.modifyItemModelAfterBake()
                                .register(ModelModifier.OVERRIDE_PHASE,
                                        (ItemModel model, ModelModifier.AfterBakeItem.Context context) -> {
                                            EventResultHolder<ItemModel> eventResult = callback.onAfterBakeItem(context.itemId(),
                                                    model,
                                                    context.sourceModel(),
                                                    context.bakeContext());
                                            return eventResult.getInterrupt().orElse(model);
                                        });
                    });
                });
        INSTANCE.register(ModelLoadingEvents.LoadBlockModel.class,
                (ModelLoadingEvents.LoadBlockModel callback, @Nullable Object o) -> {
                    ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                        pluginContext.modifyBlockModelOnLoad()
                                .register(ModelModifier.OVERRIDE_PHASE,
                                        (BlockStateModel.UnbakedRoot model, ModelModifier.OnLoadBlock.Context context) -> {
                                            EventResultHolder<BlockStateModel.UnbakedRoot> eventResult = callback.onLoadBlockModel(
                                                    context.state(),
                                                    model);
                                            return eventResult.getInterrupt().orElse(model);
                                        });
                    });
                });
        INSTANCE.register(ModelBakingEvents.BeforeBlock.class,
                (ModelBakingEvents.BeforeBlock callback, @Nullable Object o) -> {
                    ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                        pluginContext.modifyBlockModelBeforeBake()
                                .register(ModelModifier.OVERRIDE_PHASE,
                                        (BlockStateModel.UnbakedRoot model, ModelModifier.BeforeBakeBlock.Context context) -> {
                                            EventResultHolder<BlockStateModel.UnbakedRoot> eventResult = callback.onBeforeBakeBlock(
                                                    context.state(),
                                                    model,
                                                    context.baker());
                                            return eventResult.getInterrupt().orElse(model);
                                        });
                    });
                });
        INSTANCE.register(ModelBakingEvents.AfterBlock.class,
                (ModelBakingEvents.AfterBlock callback, @Nullable Object o) -> {
                    ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                        pluginContext.modifyBlockModelAfterBake()
                                .register(ModelModifier.OVERRIDE_PHASE,
                                        (BlockStateModel model, ModelModifier.AfterBakeBlock.Context context) -> {
                                            EventResultHolder<BlockStateModel> eventResult = callback.onAfterBakeBlock(
                                                    context.state(),
                                                    model,
                                                    context.sourceModel(),
                                                    context.baker());
                                            return eventResult.getInterrupt().orElse(model);
                                        });
                    });
                });
        INSTANCE.register(ClientLifecycleEvents.Started.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STARTED,
                (ClientLifecycleEvents.Started callback) -> {
                    return callback::onClientStarted;
                });
        INSTANCE.register(ClientLifecycleEvents.Stopping.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STOPPING,
                (ClientLifecycleEvents.Stopping callback) -> {
                    return callback::onClientStopping;
                });
        INSTANCE.register(ClientSetupCallback.class, (ClientSetupCallback callback, @Nullable Object context) -> {
            callback.onClientSetup();
        });
        INSTANCE.register(DrawItemStackOverlayCallback.class,
                net.fabricmc.fabric.api.client.rendering.v1.DrawItemStackOverlayCallback.EVENT,
                (DrawItemStackOverlayCallback callback, @Nullable Object context) -> {
                    return (GuiGraphics guiGraphics, Font font, ItemStack itemStack, int posX, int posY) -> {
                        Objects.requireNonNull(context, "context is null");
                        Item item = (Item) context;
                        if (itemStack.is(item)) {
                            callback.onDrawItemStackOverlay(guiGraphics, font, itemStack, posX, posY);
                        }
                    };
                });
        INSTANCE.register(AddLivingEntityRenderLayersCallback.class,
                LivingEntityFeatureRendererRegistrationCallback.EVENT,
                (AddLivingEntityRenderLayersCallback callback) -> {
                    return (EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?, ?> entityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) -> {
                        callback.addLivingEntityRenderLayers(entityType, entityRenderer, context);
                    };
                });
    }

    public static void registerEventHandlers() {
        INSTANCE.register(ClientTickEvents.Start.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_CLIENT_TICK,
                (ClientTickEvents.Start callback) -> {
                    return callback::onStartClientTick;
                });
        INSTANCE.register(ClientTickEvents.End.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK,
                (ClientTickEvents.End callback) -> {
                    return callback::onEndClientTick;
                });
        AtomicInteger atomicInteger = new AtomicInteger();
        INSTANCE.register(RenderGuiEvents.Before.class, (RenderGuiEvents.Before callback, @Nullable Object context) -> {
            // register as late as possible, so we capture as many hud layers as possible
            net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft minecraft) -> {
                HudElementRegistry.addFirst(PuzzlesLibMod.id(String.valueOf(atomicInteger.getAndIncrement())),
                        (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                            if (!minecraft.options.hideGui) {
                                callback.onBeforeRenderGui(guiGraphics, deltaTracker);
                            }
                        });
                // the sleep layer is the only layer that renders when the gui is hidden
                HudElementRegistry.attachElementBefore(VanillaHudElements.SLEEP,
                        PuzzlesLibMod.id(String.valueOf(atomicInteger.getAndIncrement())),
                        (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                            if (minecraft.options.hideGui) {
                                callback.onBeforeRenderGui(guiGraphics, deltaTracker);
                            }
                        });
            });
        });
        INSTANCE.register(RenderGuiEvents.After.class, (RenderGuiEvents.After callback, @Nullable Object context) -> {
            // register as late as possible, so we capture as many hud layers as possible
            net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft minecraft) -> {
                HudElementRegistry.addLast(PuzzlesLibMod.id(String.valueOf(atomicInteger.getAndIncrement())),
                        (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                            if (!minecraft.options.hideGui) {
                                callback.onAfterRenderGui(guiGraphics, deltaTracker);
                            }
                        });
                // the sleep layer is the only layer that renders when the gui is hidden
                HudElementRegistry.attachElementAfter(VanillaHudElements.SLEEP,
                        PuzzlesLibMod.id(String.valueOf(atomicInteger.getAndIncrement())),
                        (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                            if (minecraft.options.hideGui) {
                                callback.onAfterRenderGui(guiGraphics, deltaTracker);
                            }
                        });
            });
        });
        INSTANCE.register(ItemTooltipCallback.class,
                net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT,
                (ItemTooltipCallback callback) -> {
                    return (ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag context, List<Component> lines) -> {
                        callback.onItemTooltip(stack, lines, tooltipContext, Minecraft.getInstance().player, context);
                    };
                });
        INSTANCE.register(RenderNameTagCallback.class, FabricRendererEvents.RENDER_NAME_TAG);
        INSTANCE.register(RenderContainerScreenContentsCallback.class,
                FabricGuiEvents.RENDER_CONTAINER_SCREEN_CONTENTS);
        INSTANCE.register(PrepareInventoryMobEffectsCallback.class, FabricGuiEvents.INVENTORY_MOB_EFFECTS);
        INSTANCE.register(ComputeFovModifierCallback.class, FabricClientPlayerEvents.COMPUTE_FOV_MODIFIER);
        INSTANCE.register(ScreenEvents.BeforeInit.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.BEFORE_INIT,
                (callback, context) -> {
                    Objects.requireNonNull(context, "context is null");
                    return (Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight) -> {
                        if (!((Class<?>) context).isInstance(screen)) return;
                        callback.onBeforeInit(minecraft,
                                screen,
                                scaledWidth,
                                scaledHeight,
                                Collections.unmodifiableList(Screens.getButtons(screen)));
                    };
                });
        INSTANCE.register(ScreenEvents.AfterInit.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_INIT,
                (callback, context) -> {
                    Objects.requireNonNull(context, "context is null");
                    return (Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight) -> {
                        if (!((Class<?>) context).isInstance(screen)) return;
                        List<AbstractWidget> widgets = Screens.getButtons(screen);
                        callback.onAfterInit(minecraft,
                                screen,
                                scaledWidth,
                                scaledHeight,
                                Collections.unmodifiableList(widgets),
                                (UnaryOperator<AbstractWidget>) (AbstractWidget abstractWidget) -> {
                                    widgets.add(abstractWidget);
                                    return abstractWidget;
                                },
                                (Consumer<AbstractWidget>) widgets::remove);
                    };
                });
        registerScreenEvent(ScreenEvents.Remove.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.Remove.class,
                callback -> {
                    return callback::onRemove;
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenEvents::remove);
        registerScreenEvent(ScreenEvents.BeforeRender.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.BeforeRender.class,
                callback -> {
                    return callback::onBeforeRender;
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenEvents::beforeRender);
        registerScreenEvent(ScreenEvents.AfterRender.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterRender.class,
                callback -> {
                    return callback::onAfterRender;
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenEvents::afterRender);
        registerScreenEvent(ScreenEvents.AfterBackground.class, AfterBackgroundCallback.class, callback -> {
            return callback::onAfterBackground;
        }, AfterBackgroundCallback::afterBackground);
        registerScreenEvent(ScreenMouseEvents.BeforeMouseClick.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AllowMouseClick.class,
                callback -> {
                    return (Screen screen, MouseButtonEvent mouseButtonEvent) -> {
                        return callback.onBeforeMouseClick(screen, mouseButtonEvent).isPass();
                    };
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::allowMouseClick);
        registerScreenEvent(ScreenMouseEvents.AfterMouseClick.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AfterMouseClick.class,
                callback -> {
                    return (Screen screen, MouseButtonEvent mouseButtonEvent, boolean consumed) -> {
                        callback.onAfterMouseClick(screen, mouseButtonEvent);
                        return false;
                    };
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::afterMouseClick);
        registerScreenEvent(ScreenMouseEvents.BeforeMouseRelease.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AllowMouseRelease.class,
                callback -> {
                    return (Screen screen, MouseButtonEvent mouseButtonEvent) -> {
                        return callback.onBeforeMouseRelease(screen, mouseButtonEvent).isPass();
                    };
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::allowMouseRelease);
        registerScreenEvent(ScreenMouseEvents.AfterMouseRelease.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AfterMouseRelease.class,
                callback -> {
                    return (Screen screen, MouseButtonEvent mouseButtonEvent, boolean consumed) -> {
                        callback.onAfterMouseRelease(screen, mouseButtonEvent);
                        return false;
                    };
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::afterMouseRelease);
        registerScreenEvent(ScreenMouseEvents.BeforeMouseScroll.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AllowMouseScroll.class,
                callback -> {
                    return (Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) -> {
                        return callback.onBeforeMouseScroll(screen, mouseX, mouseY, horizontalAmount, verticalAmount)
                                .isPass();
                    };
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::allowMouseScroll);
        registerScreenEvent(ScreenMouseEvents.AfterMouseScroll.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AfterMouseScroll.class,
                callback -> {
                    return (Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount, boolean consumed) -> {
                        callback.onAfterMouseScroll(screen, mouseX, mouseY, horizontalAmount, verticalAmount);
                        return false;
                    };
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::afterMouseScroll);
        registerScreenEvent(ScreenMouseEvents.BeforeMouseDrag.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AllowMouseDrag.class,
                callback -> {
                    return (Screen screen, MouseButtonEvent mouseButtonEvent, double horizontalAmount, double verticalAmount) -> {
                        return callback.onBeforeMouseDrag(screen, mouseButtonEvent, horizontalAmount, verticalAmount)
                                .isPass();
                    };
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::allowMouseDrag);
        registerScreenEvent(ScreenMouseEvents.AfterMouseDrag.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AfterMouseDrag.class,
                callback -> {
                    return (Screen screen, MouseButtonEvent mouseButtonEvent, double horizontalAmount, double verticalAmount, boolean consumed) -> {
                        callback.onAfterMouseDrag(screen, mouseButtonEvent, horizontalAmount, verticalAmount);
                        return false;
                    };
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::afterMouseDrag);
        registerScreenEvent(ScreenKeyboardEvents.BeforeKeyPress.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents.AllowKeyPress.class,
                callback -> {
                    return (Screen screen, KeyEvent keyEvent) -> {
                        return callback.onBeforeKeyPress(screen, keyEvent).isPass();
                    };
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents::allowKeyPress);
        registerScreenEvent(ScreenKeyboardEvents.AfterKeyPress.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents.AfterKeyPress.class,
                callback -> {
                    return callback::onAfterKeyPress;
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents::afterKeyPress);
        registerScreenEvent(ScreenKeyboardEvents.BeforeKeyRelease.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents.AllowKeyRelease.class,
                callback -> {
                    return (Screen screen, KeyEvent keyEvent) -> {
                        return callback.onBeforeKeyRelease(screen, keyEvent).isPass();
                    };
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents::allowKeyRelease);
        registerScreenEvent(ScreenKeyboardEvents.AfterKeyRelease.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents.AfterKeyRelease.class,
                callback -> {
                    return callback::onAfterKeyRelease;
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents::afterKeyRelease);
        INSTANCE.register(CustomizeChatPanelCallback.class,
                (CustomizeChatPanelCallback callback, @Nullable Object context) -> {
                    HudElementRegistry.replaceElement(VanillaHudElements.CHAT, (HudElement hudElement) -> {
                        return (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                            guiGraphics.pose().pushMatrix();
                            DefaultedInt posX = DefaultedInt.fromValue(0);
                            DefaultedInt posY = DefaultedInt.fromValue(guiGraphics.guiHeight() - 48);
                            callback.onRenderChatPanel(guiGraphics, deltaTracker, posX, posY);
                            if (posX.getAsOptionalInt().isPresent() || posY.getAsOptionalInt().isPresent()) {
                                guiGraphics.pose()
                                        .translate(posX.getAsInt(), posY.getAsInt() - (guiGraphics.guiHeight() - 48));
                            }
                            hudElement.render(guiGraphics, deltaTracker);
                            guiGraphics.pose().popMatrix();
                        };
                    });
                });
        INSTANCE.register(ClientEntityLevelEvents.Load.class, FabricClientEntityEvents.ENTITY_LOAD);
        INSTANCE.register(ClientEntityLevelEvents.Unload.class,
                ClientEntityEvents.ENTITY_UNLOAD,
                (ClientEntityLevelEvents.Unload callback) -> {
                    return callback::onEntityUnload;
                });
        INSTANCE.register(InputEvents.MouseClick.class, FabricClientEvents.MOUSE_CLICK);
        INSTANCE.register(InputEvents.MouseScroll.class, FabricClientEvents.MOUSE_SCROLL);
        INSTANCE.register(InputEvents.KeyPress.class, FabricClientEvents.KEY_PRESS);
        INSTANCE.register(RenderLivingEvents.Before.class, FabricRendererEvents.BEFORE_RENDER_LIVING);
        INSTANCE.register(RenderLivingEvents.After.class, FabricRendererEvents.AFTER_RENDER_LIVING);
        INSTANCE.register(RenderHandEvents.MainHand.class, FabricRendererEvents.RENDER_MAIN_HAND);
        INSTANCE.register(RenderHandEvents.OffHand.class, FabricRendererEvents.RENDER_OFF_HAND);
        INSTANCE.register(ComputeCameraAnglesCallback.class, FabricRendererEvents.COMPUTE_CAMERA_ANGLES);
        INSTANCE.register(ClientLevelTickEvents.Start.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_WORLD_TICK,
                (ClientLevelTickEvents.Start callback) -> {
                    return (ClientLevel clientLevel) -> {
                        callback.onStartLevelTick(Minecraft.getInstance(), clientLevel);
                    };
                });
        INSTANCE.register(ClientLevelTickEvents.End.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_WORLD_TICK,
                (ClientLevelTickEvents.End callback) -> {
                    return (ClientLevel clientLevel) -> {
                        callback.onEndLevelTick(Minecraft.getInstance(), clientLevel);
                    };
                });
        INSTANCE.register(ClientChunkEvents.Load.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_LOAD,
                (ClientChunkEvents.Load callback) -> {
                    return callback::onChunkLoad;
                });
        INSTANCE.register(ClientChunkEvents.Unload.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_UNLOAD,
                (ClientChunkEvents.Unload callback) -> {
                    return callback::onChunkUnload;
                });
        INSTANCE.register(ClientPlayerNetworkEvents.LoggedIn.class, FabricClientPlayerEvents.PLAYER_LOGGED_IN);
        INSTANCE.register(ClientPlayerNetworkEvents.LoggedOut.class, FabricClientPlayerEvents.PLAYER_LOGGED_OUT);
        INSTANCE.register(ClientPlayerCopyCallback.class, FabricClientPlayerEvents.PLAYER_COPY);
        INSTANCE.register(InteractionInputEvents.Attack.class,
                ClientPreAttackCallback.EVENT,
                (InteractionInputEvents.Attack callback) -> {
                    return (Minecraft minecraft, LocalPlayer player, int clickCount) -> {
                        if (minecraft.missTime <= 0 && minecraft.hitResult != null) {
                            if (clickCount != 0) {
                                if (!player.isHandsBusy()) {
                                    ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
                                    if (itemInHand.isItemEnabled(minecraft.level.enabledFeatures())) {
                                        return callback.onAttackInteraction(minecraft, player, minecraft.hitResult)
                                                .isInterrupt();
                                    }
                                }
                            } else {
                                if (!player.isUsingItem() && minecraft.hitResult.getType() == HitResult.Type.BLOCK) {
                                    if (!minecraft.level.isEmptyBlock(((BlockHitResult) minecraft.hitResult).getBlockPos())) {
                                        return callback.onAttackInteraction(minecraft, player, minecraft.hitResult)
                                                .isInterrupt();
                                    }
                                }
                            }
                        }
                        return false;
                    };
                });
        INSTANCE.register(InteractionInputEvents.Use.class,
                UseBlockCallback.EVENT,
                (InteractionInputEvents.Use callback, @Nullable Object context) -> {
                    return (Player player, Level level, InteractionHand hand, BlockHitResult hitResult) -> {
                        // this is only fired client-side to mimic InputEvent$InteractionKeyMappingTriggered on Forge
                        // proper handling of the Fabric callback with the server-side component is implemented elsewhere
                        if (!level.isClientSide()) return InteractionResult.PASS;
                        Minecraft minecraft = Minecraft.getInstance();
                        EventResult eventResult = callback.onUseInteraction(minecraft,
                                (LocalPlayer) player,
                                hand,
                                minecraft.hitResult);
                        // when interrupted cancel the interaction without the server being notified
                        return eventResult.isInterrupt() ? InteractionResult.FAIL : InteractionResult.PASS;
                    };
                },
                EventPhase::early,
                true);
        INSTANCE.register(InteractionInputEvents.Use.class,
                UseEntityCallback.EVENT,
                (InteractionInputEvents.Use callback, @Nullable Object context) -> {
                    return (Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) -> {
                        // this is only fired client-side to mimic InputEvent$InteractionKeyMappingTriggered on Forge
                        // proper handling of the Fabric callback with the server-side component is implemented elsewhere
                        if (!level.isClientSide()) return InteractionResult.PASS;
                        Minecraft minecraft = Minecraft.getInstance();
                        EventResult eventResult = callback.onUseInteraction(minecraft,
                                (LocalPlayer) player,
                                hand,
                                minecraft.hitResult);
                        // when interrupted cancel the interaction without the server being notified
                        return eventResult.isInterrupt() ? InteractionResult.FAIL : InteractionResult.PASS;
                    };
                },
                EventPhase::early,
                true);
        INSTANCE.register(InteractionInputEvents.Use.class,
                UseItemCallback.EVENT,
                (InteractionInputEvents.Use callback, @Nullable Object context) -> {
                    return (Player player, Level level, InteractionHand hand) -> {
                        // this is only fired client-side to mimic InputEvent$InteractionKeyMappingTriggered on Forge
                        // proper handling of the Fabric callback with the server-side component is implemented elsewhere
                        if (!level.isClientSide()) return InteractionResult.PASS;
                        Minecraft minecraft = Minecraft.getInstance();
                        EventResult eventResult = callback.onUseInteraction(minecraft,
                                (LocalPlayer) player,
                                hand,
                                minecraft.hitResult);
                        // when interrupted cancel the interaction without the server being notified
                        return eventResult.isInterrupt() ? InteractionResult.FAIL : InteractionResult.PASS;
                    };
                },
                EventPhase::early,
                true);
        INSTANCE.register(InteractionInputEvents.Pick.class, FabricClientPlayerEvents.PICK_INTERACTION_INPUT);
        INSTANCE.register(ClientLevelEvents.Load.class, FabricClientLevelEvents.LOAD_LEVEL);
        INSTANCE.register(ClientLevelEvents.Unload.class, FabricClientLevelEvents.UNLOAD_LEVEL);
        INSTANCE.register(MovementInputUpdateCallback.class, FabricClientPlayerEvents.MOVEMENT_INPUT_UPDATE);
        INSTANCE.register(RenderBlockOverlayCallback.class, FabricRendererEvents.RENDER_BLOCK_OVERLAY);
        INSTANCE.register(FogEvents.Setup.class, FabricRendererEvents.SETUP_FOG);
        INSTANCE.register(FogEvents.Color.class, FabricRendererEvents.FOG_COLOR);
        INSTANCE.register(RenderTooltipCallback.class, FabricGuiEvents.RENDER_TOOLTIP);
        INSTANCE.register(GameRenderEvents.Before.class, FabricRendererEvents.BEFORE_GAME_RENDER);
        INSTANCE.register(GameRenderEvents.After.class, FabricRendererEvents.AFTER_GAME_RENDER);
        INSTANCE.register(AddToastCallback.class, FabricGuiEvents.ADD_TOAST);
        INSTANCE.register(ComputeFieldOfViewCallback.class, FabricRendererEvents.COMPUTE_FIELD_OF_VIEW);
        INSTANCE.register(ChatMessageReceivedCallback.class, FabricClientEvents.CHAT_MESSAGE_RECEIVED);
        INSTANCE.register(GatherEffectScreenTooltipCallback.class, FabricGuiEvents.GATHER_EFFECT_SCREEN_TOOLTIP);
        INSTANCE.register(ExtractRenderStateCallback.class, FabricRendererEvents.EXTRACT_RENDER_STATE);
    }

    private static <T, E> void registerScreenEvent(Class<T> clazz, Class<E> eventType, Function<T, E> converter, Function<Screen, Event<E>> eventGetter) {
        INSTANCE.register(clazz,
                eventType,
                converter,
                (Object context, Consumer<Event<E>> applyToInvoker, Consumer<Event<E>> removeInvoker) -> {
                    // we need to keep our own event invokers during the whole pre-init phase to guarantee phase ordering is applied correctly,
                    // since this is managed in the event invokers and there seems to be no way to handle it with just the Fabric event
                    // (since the Fabric event doesn't allow for retrieving already applied event phase orders),
                    // so we register all screen events during pre-init, which allows post-init to already clear our internal map again
                    net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.BEFORE_INIT.register((Minecraft client, Screen screen, int scaledWidth, int scaledHeight) -> {
                        if (((Class<?>) context).isInstance(screen)) applyToInvoker.accept(eventGetter.apply(screen));
                    });
                    net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_INIT.register((Minecraft client, Screen screen, int scaledWidth, int scaledHeight) -> {
                        if (((Class<?>) context).isInstance(screen)) removeInvoker.accept(eventGetter.apply(screen));
                    });
                });
    }
}
