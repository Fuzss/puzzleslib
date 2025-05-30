package fuzs.puzzleslib.fabric.impl.client.event;

import fuzs.puzzleslib.api.client.event.v1.*;
import fuzs.puzzleslib.api.client.event.v1.entity.ClientEntityLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.*;
import fuzs.puzzleslib.api.client.event.v1.gui.*;
import fuzs.puzzleslib.api.client.event.v1.level.ClientChunkEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelTickEvents;
import fuzs.puzzleslib.api.client.event.v1.model.BlockModelLoadingEvents;
import fuzs.puzzleslib.api.client.event.v1.model.ModelBakingCompleteCallback;
import fuzs.puzzleslib.api.client.event.v1.model.ModelLoadingEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.*;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.fabric.api.client.event.v1.*;
import fuzs.puzzleslib.fabric.api.core.v1.resources.FabricReloadListener;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventInvokerRegistry.INSTANCE;

@SuppressWarnings("unchecked")
public final class FabricClientEventInvokers {

    public static void registerLoadingHandlers() {
        INSTANCE.register(AddResourcePackReloadListenersCallback.class, FabricLifecycleEvents.LOAD_COMPLETE, (AddResourcePackReloadListenersCallback callback) -> {
            return () -> {
                callback.onAddResourcePackReloadListeners((ResourceLocation resourceLocation, PreparableReloadListener reloadListener) -> {
                    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricReloadListener(resourceLocation, reloadListener));
                });
            };
        });
        INSTANCE.register(ScreenOpeningCallback.class, FabricGuiEvents.SCREEN_OPENING);
        INSTANCE.register(ModelLoadingEvents.LoadModel.class, (ModelLoadingEvents.LoadModel callback, @Nullable Object o) -> {
            ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                pluginContext.modifyModelOnLoad().register(ModelModifier.OVERRIDE_PHASE, (UnbakedModel model, ModelModifier.OnLoad.Context context) -> {
                    EventResultHolder<UnbakedModel> result = callback.onLoadModel(
                            context.id(),
                            model);
                    return result.getInterrupt().orElse(model);
                });
            });
        });
        INSTANCE.register(ModelLoadingEvents.ModifyUnbakedModel.class, (ModelLoadingEvents.ModifyUnbakedModel callback, @Nullable Object o) -> {
            ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                pluginContext.modifyModelBeforeBake().register(ModelModifier.OVERRIDE_PHASE, (UnbakedModel model, ModelModifier.BeforeBake.Context context) -> {
                    EventResultHolder<UnbakedModel> result = callback.onModifyUnbakedModel(
                            context.id(),
                            model,
                            context.settings(),
                            context.baker());
                    return result.getInterrupt().orElse(model);
                });
            });
        });
        INSTANCE.register(ModelLoadingEvents.ModifyBakedModel.class, (ModelLoadingEvents.ModifyBakedModel callback, @Nullable Object o) -> {
            ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                pluginContext.modifyModelAfterBake().register(ModelModifier.OVERRIDE_PHASE, (BakedModel model, ModelModifier.AfterBake.Context context) -> {
                    EventResultHolder<BakedModel> result = callback.onModifyBakedModel(
                            context.id(),
                            model,
                            context.sourceModel(),
                            context.settings(),
                            context.baker());
                    return result.getInterrupt().orElse(model);
                });
            });
        });
        INSTANCE.register(BlockModelLoadingEvents.LoadModel.class, (BlockModelLoadingEvents.LoadModel callback, @Nullable Object o) -> {
            ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                pluginContext.modifyBlockModelOnLoad().register(ModelModifier.OVERRIDE_PHASE, (UnbakedBlockStateModel model, ModelModifier.OnLoadBlock.Context context) -> {
                    EventResultHolder<UnbakedBlockStateModel> result = callback.onLoadModel(
                            context.id(),
                            model,
                            context.state());
                    return result.getInterrupt().orElse(model);
                });
            });
        });
        INSTANCE.register(BlockModelLoadingEvents.ModifyUnbakedModel.class, (BlockModelLoadingEvents.ModifyUnbakedModel callback, @Nullable Object o) -> {
            ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                pluginContext.modifyBlockModelBeforeBake().register(ModelModifier.OVERRIDE_PHASE, (UnbakedBlockStateModel model, ModelModifier.BeforeBakeBlock.Context context) -> {
                    EventResultHolder<UnbakedBlockStateModel> result = callback.onModifyUnbakedModel(
                            context.id(),
                            model,
                            context.baker());
                    return result.getInterrupt().orElse(model);
                });
            });
        });
        INSTANCE.register(BlockModelLoadingEvents.ModifyBakedModel.class, (BlockModelLoadingEvents.ModifyBakedModel callback, @Nullable Object o) -> {
            ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                pluginContext.modifyBlockModelAfterBake().register(ModelModifier.OVERRIDE_PHASE, (BakedModel model, ModelModifier.AfterBakeBlock.Context context) -> {
                    EventResultHolder<BakedModel> result = callback.onModifyBakedModel(
                            context.id(),
                            model,
                            context.sourceModel(),
                            context.baker());
                    return result.getInterrupt().orElse(model);
                });
            });
        });
        INSTANCE.register(ModelBakingCompleteCallback.class, FabricClientEvents.MODEL_BAKING_COMPLETE);
        INSTANCE.register(ClientStartedCallback.class, ClientLifecycleEvents.CLIENT_STARTED, callback -> {
            return callback::onClientStarted;
        });
        INSTANCE.register(ClientSetupCallback.class, (ClientSetupCallback callback, @Nullable Object context) -> {
            callback.onClientSetup();
        });
    }

    public static void registerEventHandlers() {
        INSTANCE.register(ClientTickEvents.Start.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_CLIENT_TICK, (ClientTickEvents.Start callback) -> {
            return callback::onStartClientTick;
        });
        INSTANCE.register(ClientTickEvents.End.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK, (ClientTickEvents.End callback) -> {
            return callback::onEndClientTick;
        });
        INSTANCE.register(RenderGuiEvents.Before.class, FabricGuiEvents.BEFORE_RENDER_GUI);
        INSTANCE.register(RenderGuiEvents.After.class, HudRenderCallback.EVENT, (RenderGuiEvents.After callback) -> {
            return (GuiGraphics drawContext, DeltaTracker tickCounter) -> {
                callback.onAfterRenderGui(Minecraft.getInstance().gui, drawContext, tickCounter);
            };
        });
        INSTANCE.register(ItemTooltipCallback.class, net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT, (ItemTooltipCallback callback) -> {
            return (ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag context, List<Component> lines) -> {
                callback.onItemTooltip(stack, lines, tooltipContext, Minecraft.getInstance().player, context);
            };
        });
        INSTANCE.register(RenderNameTagCallback.class, FabricRendererEvents.RENDER_NAME_TAG);
        INSTANCE.register(ContainerScreenEvents.Background.class, FabricGuiEvents.CONTAINER_SCREEN_BACKGROUND);
        INSTANCE.register(ContainerScreenEvents.Foreground.class, FabricGuiEvents.CONTAINER_SCREEN_FOREGROUND);
        INSTANCE.register(InventoryMobEffectsCallback.class, FabricGuiEvents.INVENTORY_MOB_EFFECTS);
        INSTANCE.register(ComputeFovModifierCallback.class, FabricClientPlayerEvents.COMPUTE_FOV_MODIFIER);
        INSTANCE.register(ScreenEvents.BeforeInit.class, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.BEFORE_INIT, (callback, context) -> {
            Objects.requireNonNull(context, "context is null");
            return (Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight) -> {
                if (!((Class<?>) context).isInstance(screen)) return;
                callback.onBeforeInit(minecraft, screen, scaledWidth, scaledHeight, Collections.unmodifiableList(Screens.getButtons(screen)));
            };
        });
        INSTANCE.register(ScreenEvents.AfterInit.class, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_INIT, (callback, context) -> {
            Objects.requireNonNull(context, "context is null");
            return (Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight) -> {
                if (!((Class<?>) context).isInstance(screen)) return;
                List<AbstractWidget> widgets = Screens.getButtons(screen);
                callback.onAfterInit(minecraft, screen, scaledWidth, scaledHeight, Collections.unmodifiableList(widgets),
                        (UnaryOperator<AbstractWidget>) (AbstractWidget abstractWidget) -> {
                            widgets.add(abstractWidget);
                            return abstractWidget;
                        },
                        (Consumer<AbstractWidget>) widgets::remove
                );
            };
        });
        registerScreenEvent(ScreenEvents.Remove.class, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.Remove.class, callback -> {
            return callback::onRemove;
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents::remove);
        registerScreenEvent(ScreenEvents.BeforeRender.class, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.BeforeRender.class, callback -> {
            return callback::onBeforeRender;
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents::beforeRender);
        registerScreenEvent(ScreenEvents.AfterRender.class, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterRender.class, callback -> {
            return callback::onAfterRender;
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents::afterRender);
        registerScreenEvent(ScreenMouseEvents.BeforeMouseClick.class, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AllowMouseClick.class, callback -> {
            return (Screen screen, double mouseX, double mouseY, int button) -> {
                return callback.onBeforeMouseClick(screen, mouseX, mouseY, button).isPass();
            };
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::allowMouseClick);
        registerScreenEvent(ScreenMouseEvents.AfterMouseClick.class, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AfterMouseClick.class, callback -> {
            return callback::onAfterMouseClick;
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::afterMouseClick);
        registerScreenEvent(ScreenMouseEvents.BeforeMouseRelease.class, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AllowMouseRelease.class, callback -> {
            return (Screen screen, double mouseX, double mouseY, int button) -> {
                return callback.onBeforeMouseRelease(screen, mouseX, mouseY, button).isPass();
            };
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::allowMouseRelease);
        registerScreenEvent(ScreenMouseEvents.AfterMouseRelease.class, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AfterMouseRelease.class, callback -> {
            return callback::onAfterMouseRelease;
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::afterMouseRelease);
        registerScreenEvent(ScreenMouseEvents.BeforeMouseScroll.class, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AllowMouseScroll.class, callback -> {
            return (Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) -> {
                return callback.onBeforeMouseScroll(screen, mouseX, mouseY, horizontalAmount, verticalAmount).isPass();
            };
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::allowMouseScroll);
        registerScreenEvent(ScreenMouseEvents.AfterMouseScroll.class, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AfterMouseScroll.class, callback -> {
            return callback::onAfterMouseScroll;
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::afterMouseScroll);
        registerScreenEvent(ScreenMouseEvents.BeforeMouseDrag.class, ExtraScreenMouseEvents.AllowMouseDrag.class, callback -> {
            return (Screen screen, double mouseX, double mouseY, int button, double dragX, double dragY) -> {
                return callback.onBeforeMouseDrag(screen, mouseX, mouseY, button, dragX, dragY).isPass();
            };
        }, ExtraScreenMouseEvents::allowMouseDrag);
        registerScreenEvent(ScreenMouseEvents.AfterMouseDrag.class, ExtraScreenMouseEvents.AfterMouseDrag.class, callback -> {
            return callback::onAfterMouseDrag;
        }, ExtraScreenMouseEvents::afterMouseDrag);
        registerScreenEvent(ScreenKeyboardEvents.BeforeKeyPress.class, net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents.AllowKeyPress.class, callback -> {
            return (Screen screen, int key, int scancode, int modifiers) -> {
                return callback.onBeforeKeyPress(screen, key, scancode, modifiers).isPass();
            };
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents::allowKeyPress);
        registerScreenEvent(ScreenKeyboardEvents.AfterKeyPress.class, net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents.AfterKeyPress.class, callback -> {
            return callback::onAfterKeyPress;
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents::afterKeyPress);
        registerScreenEvent(ScreenKeyboardEvents.BeforeKeyRelease.class, net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents.AllowKeyRelease.class, callback -> {
            return (Screen screen, int key, int scancode, int modifiers) -> {
                return callback.onBeforeKeyRelease(screen, key, scancode, modifiers).isPass();
            };
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents::allowKeyRelease);
        registerScreenEvent(ScreenKeyboardEvents.AfterKeyRelease.class, net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents.AfterKeyRelease.class, callback -> {
            return callback::onAfterKeyRelease;
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents::afterKeyRelease);
        INSTANCE.register(RenderGuiLayerEvents.Before.class, (Object context, Consumer<Event<RenderGuiLayerEvents.Before>> applyToInvoker, Consumer<Event<RenderGuiLayerEvents.Before>> removeInvoker) -> {
            Objects.requireNonNull(context, "context is null");
            applyToInvoker.accept(FabricGuiEvents.beforeRenderGuiElement(((ResourceLocation) context)));
        });
        INSTANCE.register(RenderGuiLayerEvents.After.class, (Object context, Consumer<Event<RenderGuiLayerEvents.After>> applyToInvoker, Consumer<Event<RenderGuiLayerEvents.After>> removeInvoker) -> {
            Objects.requireNonNull(context, "context is null");
            applyToInvoker.accept(FabricGuiEvents.afterRenderGuiElement(((ResourceLocation) context)));
        });
        INSTANCE.register(CustomizeChatPanelCallback.class, FabricGuiEvents.CUSTOMIZE_CHAT_PANEL);
        INSTANCE.register(ClientEntityLevelEvents.Load.class, FabricClientEntityEvents.ENTITY_LOAD);
        INSTANCE.register(ClientEntityLevelEvents.Unload.class, ClientEntityEvents.ENTITY_UNLOAD, (ClientEntityLevelEvents.Unload callback) -> {
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
        INSTANCE.register(ClientLevelTickEvents.Start.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_WORLD_TICK, (ClientLevelTickEvents.Start callback) -> {
            return (ClientLevel clientLevel) -> {
                callback.onStartLevelTick(Minecraft.getInstance(), clientLevel);
            };
        });
        INSTANCE.register(ClientLevelTickEvents.End.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_WORLD_TICK, (ClientLevelTickEvents.End callback) -> {
            return (ClientLevel clientLevel) -> {
                callback.onEndLevelTick(Minecraft.getInstance(), clientLevel);
            };
        });
        INSTANCE.register(ClientChunkEvents.Load.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_LOAD, (ClientChunkEvents.Load callback) -> {
            return callback::onChunkLoad;
        });
        INSTANCE.register(ClientChunkEvents.Unload.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_UNLOAD, (ClientChunkEvents.Unload callback) -> {
            return callback::onChunkUnload;
        });
        INSTANCE.register(ClientPlayerNetworkEvents.LoggedIn.class, FabricClientPlayerEvents.PLAYER_LOGGED_IN);
        INSTANCE.register(ClientPlayerNetworkEvents.LoggedOut.class, FabricClientPlayerEvents.PLAYER_LOGGED_OUT);
        INSTANCE.register(ClientPlayerCopyCallback.class, FabricClientPlayerEvents.PLAYER_COPY);
        INSTANCE.register(InteractionInputEvents.Attack.class, ClientPreAttackCallback.EVENT, (InteractionInputEvents.Attack callback) -> {
            return (Minecraft minecraft, LocalPlayer player, int clickCount) -> {
                if (minecraft.missTime <= 0 && minecraft.hitResult != null) {
                    if (clickCount != 0) {
                        if (!player.isHandsBusy()) {
                            ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
                            if (itemInHand.isItemEnabled(minecraft.level.enabledFeatures())) {
                                return callback.onAttackInteraction(minecraft, player, minecraft.hitResult).isInterrupt();
                            }
                        }
                    } else {
                        if (!player.isUsingItem() && minecraft.hitResult.getType() == HitResult.Type.BLOCK) {
                            if (!minecraft.level.isEmptyBlock(((BlockHitResult) minecraft.hitResult).getBlockPos())) {
                                return callback.onAttackInteraction(minecraft, player, minecraft.hitResult).isInterrupt();
                            }
                        }
                    }
                }
                return false;
            };
        });
        INSTANCE.register(InteractionInputEvents.Use.class, UseBlockCallback.EVENT, (InteractionInputEvents.Use callback, @Nullable Object context) -> {
            return (Player player, Level level, InteractionHand hand, BlockHitResult hitResult) -> {
                // this is only fired client-side to mimic InputEvent$InteractionKeyMappingTriggered on Forge
                // proper handling of the Fabric callback with the server-side component is implemented elsewhere
                if (!level.isClientSide) return InteractionResult.PASS;
                Minecraft minecraft = Minecraft.getInstance();
                EventResult result = callback.onUseInteraction(minecraft, (LocalPlayer) player, hand, minecraft.hitResult);
                // when interrupted cancel the interaction without the server being notified
                return result.isInterrupt() ? InteractionResult.FAIL : InteractionResult.PASS;
            };
        }, EventPhase::early, true);
        INSTANCE.register(InteractionInputEvents.Use.class, UseEntityCallback.EVENT, (InteractionInputEvents.Use callback, @Nullable Object context) -> {
            return (Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) -> {
                // this is only fired client-side to mimic InputEvent$InteractionKeyMappingTriggered on Forge
                // proper handling of the Fabric callback with the server-side component is implemented elsewhere
                if (!level.isClientSide) return InteractionResult.PASS;
                Minecraft minecraft = Minecraft.getInstance();
                EventResult result = callback.onUseInteraction(minecraft, (LocalPlayer) player, hand, minecraft.hitResult);
                // when interrupted cancel the interaction without the server being notified
                return result.isInterrupt() ? InteractionResult.FAIL : InteractionResult.PASS;
            };
        }, EventPhase::early, true);
        INSTANCE.register(InteractionInputEvents.Use.class, UseItemCallback.EVENT, (InteractionInputEvents.Use callback, @Nullable Object context) -> {
            return (Player player, Level level, InteractionHand hand) -> {
                // this is only fired client-side to mimic InputEvent$InteractionKeyMappingTriggered on Forge
                // proper handling of the Fabric callback with the server-side component is implemented elsewhere
                if (!level.isClientSide) return InteractionResult.PASS;
                Minecraft minecraft = Minecraft.getInstance();
                EventResult result = callback.onUseInteraction(minecraft, (LocalPlayer) player, hand, minecraft.hitResult);
                // when interrupted cancel the interaction without the server being notified
                return result.isInterrupt() ? InteractionResult.FAIL : InteractionResult.PASS;
            };
        }, EventPhase::early, true);
        INSTANCE.register(InteractionInputEvents.Pick.class, FabricClientPlayerEvents.PICK_INTERACTION_INPUT);
        INSTANCE.register(ClientLevelEvents.Load.class, FabricClientLevelEvents.LOAD_LEVEL);
        INSTANCE.register(ClientLevelEvents.Unload.class, FabricClientLevelEvents.UNLOAD_LEVEL);
        INSTANCE.register(MovementInputUpdateCallback.class, FabricClientPlayerEvents.MOVEMENT_INPUT_UPDATE);
        INSTANCE.register(RenderBlockOverlayCallback.class, FabricRendererEvents.RENDER_BLOCK_OVERLAY);
        INSTANCE.register(FogEvents.Render.class, FabricRendererEvents.RENDER_FOG);
        INSTANCE.register(FogEvents.ComputeColor.class, FabricRendererEvents.COMPUTE_FOG_COLOR);
        INSTANCE.register(RenderTooltipCallback.class, FabricGuiEvents.RENDER_TOOLTIP);
        INSTANCE.register(RenderHighlightCallback.class, WorldRenderEvents.BEFORE_BLOCK_OUTLINE, (RenderHighlightCallback callback) -> {
            return (WorldRenderContext context, @Nullable HitResult hitResult) -> {
                if (hitResult == null || hitResult.getType() == HitResult.Type.MISS || hitResult.getType() == HitResult.Type.BLOCK && !context.blockOutlines()) {
                    return true;
                }
                Minecraft minecraft = Minecraft.getInstance();
                if (!(minecraft.getCameraEntity() instanceof Player) || minecraft.options.hideGui) return true;
                EventResult result = callback.onRenderHighlight(context.worldRenderer(), context.camera(), context.gameRenderer(), hitResult, context.tickCounter(), context.matrixStack(), context.consumers(), context.world());
                return result.isPass();
            };
        });
        INSTANCE.register(RenderLevelEvents.AfterTerrain.class, WorldRenderEvents.BEFORE_ENTITIES, (RenderLevelEvents.AfterTerrain callback) -> {
            return (WorldRenderContext context) -> {
                callback.onRenderLevelAfterTerrain(context.worldRenderer(), context.camera(), context.gameRenderer(), context.tickCounter(), context.matrixStack(), context.projectionMatrix(), context.frustum(), context.world());
            };
        });
        INSTANCE.register(RenderLevelEvents.AfterEntities.class, WorldRenderEvents.AFTER_ENTITIES, (RenderLevelEvents.AfterEntities callback) -> {
            return (WorldRenderContext context) -> {
                callback.onRenderLevelAfterEntities(context.worldRenderer(), context.camera(), context.gameRenderer(), context.tickCounter(), context.matrixStack(), context.projectionMatrix(), context.frustum(), context.world());
            };
        });
        INSTANCE.register(RenderLevelEvents.AfterTranslucent.class, WorldRenderEvents.AFTER_TRANSLUCENT, (RenderLevelEvents.AfterTranslucent callback) -> {
            return (WorldRenderContext context) -> {
                callback.onRenderLevelAfterTranslucent(context.worldRenderer(), context.camera(), context.gameRenderer(), context.tickCounter(), context.matrixStack(), context.projectionMatrix(), context.frustum(), context.world());
            };
        });
        INSTANCE.register(RenderLevelEvents.AfterLevel.class, WorldRenderEvents.END, (RenderLevelEvents.AfterLevel callback) -> {
            return (WorldRenderContext context) -> {
                callback.onRenderLevelAfterLevel(context.worldRenderer(), context.camera(), context.gameRenderer(), context.tickCounter(), context.matrixStack(), context.projectionMatrix(), context.frustum(), context.world());
            };
        });
        INSTANCE.register(GameRenderEvents.Before.class, FabricRendererEvents.BEFORE_GAME_RENDER);
        INSTANCE.register(GameRenderEvents.After.class, FabricRendererEvents.AFTER_GAME_RENDER);
        INSTANCE.register(AddToastCallback.class, FabricGuiEvents.ADD_TOAST);
        INSTANCE.register(GatherDebugTextEvents.Left.class, FabricGuiEvents.GATHER_LEFT_DEBUG_TEXT);
        INSTANCE.register(GatherDebugTextEvents.Right.class, FabricGuiEvents.GATHER_RIGHT_DEBUG_TEXT);
        INSTANCE.register(ComputeFieldOfViewCallback.class, FabricRendererEvents.COMPUTE_FIELD_OF_VIEW);
        INSTANCE.register(ChatMessageReceivedCallback.class, FabricClientEvents.CHAT_MESSAGE_RECEIVED);
        INSTANCE.register(GatherEffectScreenTooltipCallback.class, FabricGuiEvents.GATHER_EFFECT_SCREEN_TOOLTIP);
        INSTANCE.register(ExtractRenderStateCallback.class, FabricRendererEvents.EXTRACT_RENDER_STATE);
    }

    private static <T, E> void registerScreenEvent(Class<T> clazz, Class<E> eventType, Function<T, E> converter, Function<Screen, Event<E>> eventGetter) {
        INSTANCE.register(clazz, eventType, converter, (Object context, Consumer<Event<E>> applyToInvoker, Consumer<Event<E>> removeInvoker) -> {
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
