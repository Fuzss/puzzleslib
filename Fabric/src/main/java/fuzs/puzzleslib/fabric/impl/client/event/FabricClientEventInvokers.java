package fuzs.puzzleslib.fabric.impl.client.event;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.client.event.v1.*;
import fuzs.puzzleslib.api.client.event.v1.entity.ClientEntityLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.*;
import fuzs.puzzleslib.api.client.event.v1.gui.*;
import fuzs.puzzleslib.api.client.event.v1.level.ClientChunkEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelTickEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.*;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.fabric.api.client.event.v1.*;
import fuzs.puzzleslib.fabric.api.core.v1.resources.FabricReloadListener;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLifecycleEvents;
import fuzs.puzzleslib.impl.client.event.ModelLoadingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
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
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventInvokerRegistry.INSTANCE;

@SuppressWarnings("unchecked")
public final class FabricClientEventInvokers {
    // a custom item stack used for identity matching to be able to cancel our pick block event
    private static final ItemStack INTERRUPT_PICK_ITEM_STACK = new ItemStack(Items.STONE);

    static {
        ClientPickBlockApplyCallback.EVENT.register((Player player, HitResult result, ItemStack stack) -> {
            // match via reference identity so this can only be our dummy stack
            return stack == INTERRUPT_PICK_ITEM_STACK ? ItemStack.EMPTY : stack;
        });
    }

    public static void registerLoadingHandlers() {
        INSTANCE.register(AddResourcePackReloadListenersCallback.class,
                FabricLifecycleEvents.LOAD_COMPLETE,
                callback -> {
                    return () -> {
                        callback.onAddResourcePackReloadListeners((ResourceLocation resourceLocation, PreparableReloadListener reloadListener) -> {
                            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                                    .registerReloadListener(new FabricReloadListener(resourceLocation, reloadListener));
                        });
                    };
                });
        INSTANCE.register(ScreenOpeningCallback.class, FabricGuiEvents.SCREEN_OPENING);
        INSTANCE.register(ModelEvents.ModifyUnbakedModel.class,
                (ModelEvents.ModifyUnbakedModel callback, @Nullable Object o) -> {
                    ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                        Map<ResourceLocation, UnbakedModel> additionalModels = Maps.newHashMap();
                        pluginContext.modifyModelBeforeBake()
                                .register(ModelModifier.OVERRIDE_PHASE,
                                        (UnbakedModel model, ModelModifier.BeforeBake.Context context) -> {
                                            // no need to include additional models in the model getter like on Forge, this is done automatically on Fabric via the model resolving callback
                                            if (context.topLevelId() != null) {
                                                EventResultHolder<UnbakedModel> result = callback.onModifyUnbakedModel(
                                                        context.topLevelId(),
                                                        () -> model,
                                                        ModelLoadingHelper.getUnbakedTopLevelModel(context.loader()),
                                                        additionalModels::put);
                                                return result.getInterrupt().orElse(model);
                                            } else {
                                                return model;
                                            }
                                        });
                        pluginContext.resolveModel().register((ModelResolver.Context context) -> {
                            return additionalModels.get(context.id());
                        });
                    });
                });
        INSTANCE.register(ModelEvents.ModifyBakedModel.class,
                (ModelEvents.ModifyBakedModel callback, @Nullable Object o) -> {
                    ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                        pluginContext.modifyModelAfterBake()
                                .register(ModelModifier.OVERRIDE_PHASE,
                                        (@Nullable BakedModel model, ModelModifier.AfterBake.Context context) -> {
                                            if (model != null) {
                                                Map<ModelResourceLocation, BakedModel> models = context.loader()
                                                        .getBakedTopLevelModels();
                                                EventResultHolder<BakedModel> result = callback.onModifyBakedModel(
                                                        context.topLevelId(),
                                                        () -> model,
                                                        context::baker,
                                                        (ModelResourceLocation resourceLocation) -> {
                                                            return models.containsKey(resourceLocation) ?
                                                                    models.get(resourceLocation) : context.baker()
                                                                    .bake(resourceLocation.id(),
                                                                            BlockModelRotation.X0_Y0);
                                                        },
                                                        models::putIfAbsent);
                                                return result.getInterrupt().orElse(model);
                                            } else {
                                                return null;
                                            }
                                        });
                    });
                });
        INSTANCE.register(ModelEvents.AddAdditionalBakedModel.class,
                (ModelEvents.AddAdditionalBakedModel callback, @Nullable Object o) -> {
                    ModelLoadingPlugin.register((ModelLoadingPlugin.Context pluginContext) -> {
                        pluginContext.modifyModelAfterBake()
                                .register((@Nullable BakedModel model, ModelModifier.AfterBake.Context context) -> {
                                    // all we want is access to the top level baked models map to be able to insert our own models
                                    // since the missing model is guaranteed to be baked at some point hijack the event to get to the map
                                    if (context.topLevelId().equals(ModelBakery.MISSING_MODEL_VARIANT)) {
                                        Map<ModelResourceLocation, BakedModel> models = context.loader()
                                                .getBakedTopLevelModels();
                                        // using the baker from the context will print the wrong model for missing textures (missing model), but that's how it is
                                        callback.onAddAdditionalBakedModel(models::putIfAbsent,
                                                (ModelResourceLocation resourceLocation) -> {
                                                    return models.containsKey(resourceLocation) ?
                                                            models.get(resourceLocation) : context.baker()
                                                            .bake(resourceLocation.id(), BlockModelRotation.X0_Y0);
                                                },
                                                context::baker);
                                    }

                                    return model;
                                });
                    });
                });
        INSTANCE.register(ModelEvents.CompleteModelLoading.class, FabricClientEvents.COMPLETE_MODEL_LOADING);
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
        INSTANCE.register(AddLivingEntityRenderLayersCallback.class,
                LivingEntityFeatureRendererRegistrationCallback.EVENT,
                (AddLivingEntityRenderLayersCallback callback) -> {
                    return (EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?> entityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) -> {
                        callback.addLivingEntityRenderLayers(entityType, entityRenderer, context);
                    };
                });
    }

    public static void registerEventHandlers() {
        INSTANCE.register(ClientTickEvents.Start.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_CLIENT_TICK,
                callback -> {
                    return callback::onStartClientTick;
                });
        INSTANCE.register(ClientTickEvents.End.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK,
                callback -> {
                    return callback::onEndClientTick;
                });
        INSTANCE.register(RenderGuiCallback.class, HudRenderCallback.EVENT, callback -> {
            return (GuiGraphics drawContext, DeltaTracker tickCounter) -> {
                callback.onRenderGui(Minecraft.getInstance(), drawContext, tickCounter);
            };
        });
        INSTANCE.register(RenderGuiEvents.Before.class, FabricGuiEvents.BEFORE_RENDER_GUI);
        INSTANCE.register(RenderGuiEvents.After.class, HudRenderCallback.EVENT, callback -> {
            return (GuiGraphics drawContext, DeltaTracker tickCounter) -> {
                callback.onAfterRenderGui(Minecraft.getInstance().gui, drawContext, tickCounter);
            };
        });
        INSTANCE.register(ItemTooltipCallback.class,
                net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT,
                callback -> {
                    return (ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag context, List<Component> lines) -> {
                        callback.onItemTooltip(stack, lines, tooltipContext, Minecraft.getInstance().player, context);
                    };
                });
        INSTANCE.register(RenderNameTagCallback.class, FabricRendererEvents.RENDER_NAME_TAG);
        INSTANCE.register(ContainerScreenEvents.Background.class, FabricGuiEvents.CONTAINER_SCREEN_BACKGROUND);
        INSTANCE.register(ContainerScreenEvents.Foreground.class, FabricGuiEvents.CONTAINER_SCREEN_FOREGROUND);
        INSTANCE.register(InventoryMobEffectsCallback.class, FabricGuiEvents.INVENTORY_MOB_EFFECTS);
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
        registerScreenEvent(ScreenMouseEvents.BeforeMouseClick.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AllowMouseClick.class,
                callback -> {
                    return (screen, mouseX, mouseY, button) -> {
                        return callback.onBeforeMouseClick(screen, mouseX, mouseY, button).isPass();
                    };
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::allowMouseClick);
        registerScreenEvent(ScreenMouseEvents.AfterMouseClick.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AfterMouseClick.class,
                callback -> {
                    return callback::onAfterMouseClick;
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::afterMouseClick);
        registerScreenEvent(ScreenMouseEvents.BeforeMouseRelease.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AllowMouseRelease.class,
                callback -> {
                    return (screen, mouseX, mouseY, button) -> {
                        return callback.onBeforeMouseRelease(screen, mouseX, mouseY, button).isPass();
                    };
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::allowMouseRelease);
        registerScreenEvent(ScreenMouseEvents.AfterMouseRelease.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AfterMouseRelease.class,
                callback -> {
                    return callback::onAfterMouseRelease;
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
                    return callback::onAfterMouseScroll;
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::afterMouseScroll);
        registerScreenEvent(ScreenMouseEvents.BeforeMouseDrag.class,
                ExtraScreenMouseEvents.AllowMouseDrag.class,
                callback -> {
                    return (Screen screen, double mouseX, double mouseY, int button, double dragX, double dragY) -> {
                        return callback.onBeforeMouseDrag(screen, mouseX, mouseY, button, dragX, dragY).isPass();
                    };
                },
                ExtraScreenMouseEvents::allowMouseDrag);
        registerScreenEvent(ScreenMouseEvents.AfterMouseDrag.class,
                ExtraScreenMouseEvents.AfterMouseDrag.class,
                callback -> {
                    return callback::onAfterMouseDrag;
                },
                ExtraScreenMouseEvents::afterMouseDrag);
        registerScreenEvent(ScreenKeyboardEvents.BeforeKeyPress.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents.AllowKeyPress.class,
                callback -> {
                    return (Screen screen, int key, int scancode, int modifiers) -> {
                        return callback.onBeforeKeyPress(screen, key, scancode, modifiers).isPass();
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
                    return (Screen screen, int key, int scancode, int modifiers) -> {
                        return callback.onBeforeKeyRelease(screen, key, scancode, modifiers).isPass();
                    };
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents::allowKeyRelease);
        registerScreenEvent(ScreenKeyboardEvents.AfterKeyRelease.class,
                net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents.AfterKeyRelease.class,
                callback -> {
                    return callback::onAfterKeyRelease;
                },
                net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents::afterKeyRelease);
        INSTANCE.register(RenderGuiLayerEvents.Before.class, (context, applyToInvoker, removeInvoker) -> {
            Objects.requireNonNull(context, "context is null");
            applyToInvoker.accept(FabricGuiEvents.beforeRenderGuiElement(((ResourceLocation) context)));
        });
        INSTANCE.register(RenderGuiLayerEvents.After.class, (context, applyToInvoker, removeInvoker) -> {
            Objects.requireNonNull(context, "context is null");
            applyToInvoker.accept(FabricGuiEvents.afterRenderGuiElement(((ResourceLocation) context)));
        });
        INSTANCE.register(CustomizeChatPanelCallback.class, FabricGuiEvents.CUSTOMIZE_CHAT_PANEL);
        INSTANCE.register(ClientEntityLevelEvents.Load.class, FabricClientEntityEvents.ENTITY_LOAD);
        INSTANCE.register(ClientEntityLevelEvents.Unload.class, ClientEntityEvents.ENTITY_UNLOAD, callback -> {
            return callback::onEntityUnload;
        });
        INSTANCE.register(InputEvents.MouseClick.class, FabricClientEvents.MOUSE_CLICK);
        INSTANCE.register(InputEvents.MouseScroll.class, FabricClientEvents.MOUSE_SCROLL);
        INSTANCE.register(InputEvents.KeyPress.class, FabricClientEvents.KEY_PRESS);
        INSTANCE.register(RenderLivingEvents.Before.class, FabricRendererEvents.BEFORE_RENDER_LIVING);
        INSTANCE.register(RenderLivingEvents.After.class, FabricRendererEvents.AFTER_RENDER_LIVING);
        INSTANCE.register(RenderPlayerEvents.Before.class, FabricRendererEvents.BEFORE_RENDER_PLAYER);
        INSTANCE.register(RenderPlayerEvents.After.class, FabricRendererEvents.AFTER_RENDER_PLAYER);
        INSTANCE.register(RenderHandEvents.MainHand.class, FabricRendererEvents.RENDER_MAIN_HAND);
        INSTANCE.register(RenderHandEvents.OffHand.class, FabricRendererEvents.RENDER_OFF_HAND);
        INSTANCE.register(ComputeCameraAnglesCallback.class, FabricRendererEvents.COMPUTE_CAMERA_ANGLES);
        INSTANCE.register(ClientLevelTickEvents.Start.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_WORLD_TICK,
                callback -> {
                    return (ClientLevel world) -> {
                        callback.onStartLevelTick(Minecraft.getInstance(), world);
                    };
                });
        INSTANCE.register(ClientLevelTickEvents.End.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_WORLD_TICK,
                callback -> {
                    return (ClientLevel world) -> {
                        callback.onEndLevelTick(Minecraft.getInstance(), world);
                    };
                });
        INSTANCE.register(ClientChunkEvents.Load.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_LOAD,
                callback -> {
                    return callback::onChunkLoad;
                });
        INSTANCE.register(ClientChunkEvents.Unload.class,
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_UNLOAD,
                callback -> {
                    return callback::onChunkUnload;
                });
        INSTANCE.register(ClientPlayerNetworkEvents.LoggedIn.class, FabricClientPlayerEvents.PLAYER_LOGGED_IN);
        INSTANCE.register(ClientPlayerNetworkEvents.LoggedOut.class, FabricClientPlayerEvents.PLAYER_LOGGED_OUT);
        INSTANCE.register(ClientPlayerCopyCallback.class, FabricClientPlayerEvents.PLAYER_COPY);
        INSTANCE.register(InteractionInputEvents.Attack.class, ClientPreAttackCallback.EVENT, callback -> {
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
        INSTANCE.register(InteractionInputEvents.Use.class, UseBlockCallback.EVENT, (callback, context) -> {
            return (Player player, Level level, InteractionHand hand, BlockHitResult hitResult) -> {
                // this is only fired client-side to mimic InputEvent$InteractionKeyMappingTriggered on Forge
                // proper handling of the Fabric callback with the server-side component is implemented elsewhere
                if (!level.isClientSide) return InteractionResult.PASS;
                Minecraft minecraft = Minecraft.getInstance();
                EventResult result = callback.onUseInteraction(minecraft,
                        (LocalPlayer) player,
                        hand,
                        minecraft.hitResult);
                // when interrupted cancel the interaction without the server being notified
                return result.isInterrupt() ? InteractionResult.FAIL : InteractionResult.PASS;
            };
        }, EventPhase::early, true);
        INSTANCE.register(InteractionInputEvents.Use.class, UseEntityCallback.EVENT, (callback, context) -> {
            return (Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) -> {
                // this is only fired client-side to mimic InputEvent$InteractionKeyMappingTriggered on Forge
                // proper handling of the Fabric callback with the server-side component is implemented elsewhere
                if (!level.isClientSide) return InteractionResult.PASS;
                Minecraft minecraft = Minecraft.getInstance();
                EventResult result = callback.onUseInteraction(minecraft,
                        (LocalPlayer) player,
                        hand,
                        minecraft.hitResult);
                // when interrupted cancel the interaction without the server being notified
                return result.isInterrupt() ? InteractionResult.FAIL : InteractionResult.PASS;
            };
        }, EventPhase::early, true);
        INSTANCE.register(InteractionInputEvents.Use.class, UseItemCallback.EVENT, (callback, context) -> {
            return (Player player, Level level, InteractionHand hand) -> {
                // this is only fired client-side to mimic InputEvent$InteractionKeyMappingTriggered on Forge
                // proper handling of the Fabric callback with the server-side component is implemented elsewhere
                if (!level.isClientSide) return InteractionResultHolder.pass(ItemStack.EMPTY);
                Minecraft minecraft = Minecraft.getInstance();
                EventResult result = callback.onUseInteraction(minecraft,
                        (LocalPlayer) player,
                        hand,
                        minecraft.hitResult);
                // when interrupted cancel the interaction without the server being notified
                return result.isInterrupt() ? InteractionResultHolder.fail(ItemStack.EMPTY) :
                        InteractionResultHolder.pass(ItemStack.EMPTY);
            };
        }, EventPhase::early, true);
        INSTANCE.register(InteractionInputEvents.Pick.class, ClientPickBlockGatherCallback.EVENT, callback -> {
            return (Player player, HitResult hitResult) -> {
                // add in more checks that also run on Forge
                if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
                    Minecraft minecraft = Minecraft.getInstance();
                    EventResult result = callback.onPickInteraction(minecraft, (LocalPlayer) player, hitResult);
                    // this uses a second event to filter out this custom item stack again to be able to cancel the interaction
                    // otherwise just returning empty will do nothing and let the behavior continue
                    return result.isInterrupt() ? INTERRUPT_PICK_ITEM_STACK : ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            };
        });
        INSTANCE.register(ClientLevelEvents.Load.class, FabricClientLevelEvents.LOAD_LEVEL);
        INSTANCE.register(ClientLevelEvents.Unload.class, FabricClientLevelEvents.UNLOAD_LEVEL);
        INSTANCE.register(MovementInputUpdateCallback.class, FabricClientPlayerEvents.MOVEMENT_INPUT_UPDATE);
        INSTANCE.register(RenderBlockOverlayCallback.class, FabricRendererEvents.RENDER_BLOCK_OVERLAY);
        INSTANCE.register(FogEvents.Render.class, FabricRendererEvents.RENDER_FOG);
        INSTANCE.register(FogEvents.ComputeColor.class, FabricRendererEvents.COMPUTE_FOG_COLOR);
        INSTANCE.register(RenderTooltipCallback.class, FabricGuiEvents.RENDER_TOOLTIP);
        INSTANCE.register(RenderHighlightCallback.class, WorldRenderEvents.BEFORE_BLOCK_OUTLINE, callback -> {
            return (WorldRenderContext context, @Nullable HitResult hitResult) -> {
                if (hitResult == null || hitResult.getType() == HitResult.Type.MISS
                        || hitResult.getType() == HitResult.Type.BLOCK && !context.blockOutlines()) {
                    return true;
                }
                Minecraft minecraft = Minecraft.getInstance();
                if (!(minecraft.getCameraEntity() instanceof Player) || minecraft.options.hideGui) return true;
                EventResult result = callback.onRenderHighlight(context.worldRenderer(),
                        context.camera(),
                        context.gameRenderer(),
                        hitResult,
                        context.tickCounter(),
                        context.matrixStack(),
                        context.consumers(),
                        context.world());
                return result.isPass();
            };
        });
        INSTANCE.register(RenderLevelEvents.AfterTerrain.class, WorldRenderEvents.BEFORE_ENTITIES, callback -> {
            return (WorldRenderContext context) -> {
                callback.onRenderLevelAfterTerrain(context.worldRenderer(),
                        context.camera(),
                        context.gameRenderer(),
                        context.tickCounter(),
                        context.matrixStack(),
                        context.projectionMatrix(),
                        context.frustum(),
                        context.world());
            };
        });
        INSTANCE.register(RenderLevelEvents.AfterEntities.class, WorldRenderEvents.AFTER_ENTITIES, callback -> {
            return (WorldRenderContext context) -> {
                callback.onRenderLevelAfterEntities(context.worldRenderer(),
                        context.camera(),
                        context.gameRenderer(),
                        context.tickCounter(),
                        context.matrixStack(),
                        context.projectionMatrix(),
                        context.frustum(),
                        context.world());
            };
        });
        INSTANCE.register(RenderLevelEvents.AfterTranslucent.class, WorldRenderEvents.AFTER_TRANSLUCENT, callback -> {
            return (WorldRenderContext context) -> {
                callback.onRenderLevelAfterTranslucent(context.worldRenderer(),
                        context.camera(),
                        context.gameRenderer(),
                        context.tickCounter(),
                        context.matrixStack(),
                        context.projectionMatrix(),
                        context.frustum(),
                        context.world());
            };
        });
        INSTANCE.register(RenderLevelEvents.AfterLevel.class, WorldRenderEvents.END, callback -> {
            return (WorldRenderContext context) -> {
                callback.onRenderLevelAfterLevel(context.worldRenderer(),
                        context.camera(),
                        context.gameRenderer(),
                        context.tickCounter(),
                        context.matrixStack(),
                        context.projectionMatrix(),
                        context.frustum(),
                        context.world());
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
