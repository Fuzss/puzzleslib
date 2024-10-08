package fuzs.puzzleslib.impl.client.event;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.Window;
import fuzs.puzzleslib.api.client.event.v1.*;
import fuzs.puzzleslib.api.core.v1.resources.FabricReloadListener;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
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
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.model.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static fuzs.puzzleslib.api.event.v1.core.FabricEventInvokerRegistry.INSTANCE;

@SuppressWarnings("unchecked")
public final class FabricClientEventInvokers {
    // a custom item stack used for identity matching to be able to cancel our pick block event
    private static final ItemStack INTERRUPT_PICK_ITEM_STACK = new ItemStack(Items.STONE);
    private static final MutableInt MODEL_LOADING_LISTENERS = new MutableInt();

    static {
        ClientPickBlockApplyCallback.EVENT.register((Player player, HitResult result, ItemStack stack) -> {
            // match via reference identity so this can only be our dummy stack
            return stack == INTERRUPT_PICK_ITEM_STACK ? ItemStack.EMPTY : stack;
        });
    }

    public static void registerLoadingHandlers() {
        INSTANCE.register(LoadCompleteCallback.class, ClientLifecycleEvents.CLIENT_STARTED, callback -> {
            return (Minecraft minecraft) -> {
                callback.onLoadComplete();
            };
        });
        INSTANCE.register(ModelEvents.ModifyUnbakedModel.class, (ModelEvents.ModifyUnbakedModel callback, @Nullable Object o) -> {
            ModelLoadingPlugin.register(pluginContext -> {
                Map<ResourceLocation, UnbakedModel> additionalModels = Maps.newHashMap();
                pluginContext.modifyModelBeforeBake().register(ModelModifier.OVERRIDE_PHASE, (UnbakedModel model, ModelModifier.BeforeBake.Context context) -> {
                    // no need to include additional models in the model getter like on Forge, this is done automatically on Fabric via the model resolving callback
                    EventResultHolder<UnbakedModel> result = callback.onModifyUnbakedModel(context.id(), () -> model, context.loader()::getModel, (ResourceLocation resourceLocation, UnbakedModel unbakedModel) -> {
                        // the Fabric callback for adding additional models does not work with model resource locations
                        if (resourceLocation instanceof ModelResourceLocation) {
                            throw new IllegalArgumentException("model resource location is not supported");
                        } else {
                            additionalModels.put(resourceLocation, unbakedModel);
                        }
                    });
                    return result.getInterrupt().orElse(model);
                });
                pluginContext.resolveModel().register((ModelResolver.Context context) -> {
                    return additionalModels.get(context.id());
                });
            });
        });
        INSTANCE.register(ModelEvents.ModifyBakedModel.class, (ModelEvents.ModifyBakedModel callback, @Nullable Object o) -> {
            ModelLoadingPlugin.register(pluginContext -> {
                pluginContext.modifyModelAfterBake().register(ModelModifier.OVERRIDE_PHASE, (@Nullable BakedModel model, ModelModifier.AfterBake.Context context) -> {
                    if (model != null) {
                        Map<ResourceLocation, BakedModel> models = context.loader().getBakedTopLevelModels();
                        EventResultHolder<BakedModel> result = callback.onModifyBakedModel(context.id(), () -> model, context::baker, (ResourceLocation resourceLocation) -> {
                            return models.containsKey(resourceLocation) ? models.get(resourceLocation) : context.baker().bake(resourceLocation, BlockModelRotation.X0_Y0);
                        }, models::putIfAbsent);
                        return result.getInterrupt().orElse(model);
                    } else {
                        return null;
                    }
                });
            });
        });
        INSTANCE.register(ModelEvents.AdditionalBakedModel.class, (ModelEvents.AdditionalBakedModel callback, @Nullable Object o) -> {
            ModelLoadingPlugin.register(pluginContext -> {
                pluginContext.modifyModelAfterBake().register((@Nullable BakedModel model, ModelModifier.AfterBake.Context context) -> {
                    // all we want is access to the top level baked models map to be able to insert our own models
                    // since the missing model is guaranteed to be baked at some point hijack the event to get to the map
                    if (context.id().equals(ModelBakery.MISSING_MODEL_LOCATION)) {
                        Map<ResourceLocation, BakedModel> models = context.loader().getBakedTopLevelModels();
                        // using the baker from the context will print the wrong model for missing textures (missing model), but that's how it is
                        callback.onAdditionalBakedModel(models::putIfAbsent, (ResourceLocation resourceLocation) -> {
                            return models.containsKey(resourceLocation) ? models.get(resourceLocation) : context.baker().bake(resourceLocation, BlockModelRotation.X0_Y0);
                        }, context::baker);
                    }
                    return model;
                });
            });
        });
        INSTANCE.register(ModelEvents.AfterModelLoading.class, (ModelEvents.AfterModelLoading callback, @Nullable Object o) -> {
            ResourceLocation resourceLocation = PuzzlesLib.id("after_model_loading/" + MODEL_LOADING_LISTENERS.incrementAndGet());
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricReloadListener(resourceLocation, resourceManager -> {
                Minecraft minecraft = Minecraft.getInstance();
                callback.onAfterModelLoading(minecraft::getModelManager);
            }, ResourceReloadListenerKeys.MODELS));
        });
    }

    public static void registerEventHandlers() {
        INSTANCE.register(ClientTickEvents.Start.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_CLIENT_TICK, callback -> {
            return callback::onStartClientTick;
        });
        INSTANCE.register(ClientTickEvents.End.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK, callback -> {
            return callback::onEndClientTick;
        });
        INSTANCE.register(RenderGuiCallback.class, HudRenderCallback.EVENT, callback -> {
            return (GuiGraphics matrixStack, float tickDelta) -> {
                Minecraft minecraft = Minecraft.getInstance();
                Window window = minecraft.getWindow();
                callback.onRenderGui(minecraft, matrixStack, tickDelta, window.getGuiScaledWidth(), window.getGuiScaledHeight());
            };
        });
        INSTANCE.register(ItemTooltipCallback.class, net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT, callback -> {
            return (ItemStack stack, TooltipFlag context, List<Component> lines) -> {
                callback.onItemTooltip(stack, Minecraft.getInstance().player, lines, context);
            };
        });
        INSTANCE.register(RenderNameTagCallback.class, FabricClientEvents.RENDER_NAME_TAG);
        INSTANCE.register(ContainerScreenEvents.Background.class, FabricScreenEvents.CONTAINER_SCREEN_BACKGROUND);
        INSTANCE.register(ContainerScreenEvents.Foreground.class, FabricScreenEvents.CONTAINER_SCREEN_FOREGROUND);
        INSTANCE.register(InventoryMobEffectsCallback.class, FabricScreenEvents.INVENTORY_MOB_EFFECTS);
        INSTANCE.register(ScreenOpeningCallback.class, FabricScreenEvents.SCREEN_OPENING);
        INSTANCE.register(ComputeFovModifierCallback.class, FabricClientEvents.COMPUTE_FOV_MODIFIER);
        INSTANCE.register(ScreenEvents.BeforeInit.class, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.BEFORE_INIT, callback -> {
            return (Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight) -> {
                callback.onBeforeInit(minecraft, screen, scaledWidth, scaledHeight, Collections.unmodifiableList(Screens.getButtons(screen)));
            };
        });
        INSTANCE.register(ScreenEvents.AfterInit.class, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_INIT, callback -> {
            return (Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight) -> {
                List<AbstractWidget> widgets = Screens.getButtons(screen);
                callback.onAfterInit(minecraft, screen, scaledWidth, scaledHeight, Collections.unmodifiableList(widgets), widgets::add, widgets::remove);
            };
        });
        INSTANCE.register(ScreenEvents.BeforeInitV2.class, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.BEFORE_INIT, (callback, context) -> {
            Objects.requireNonNull(context, "context is null");
            return (Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight) -> {
                if (!((Class<?>) context).isInstance(screen)) return;
                callback.onBeforeInit(minecraft, screen, scaledWidth, scaledHeight, Collections.unmodifiableList(Screens.getButtons(screen)));
            };
        });
        INSTANCE.register(ScreenEvents.AfterInitV2.class, net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_INIT, (callback, context) -> {
            Objects.requireNonNull(context, "context is null");
            return (Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight) -> {
                if (!((Class<?>) context).isInstance(screen)) return;
                List<AbstractWidget> widgets = Screens.getButtons(screen);
                ScreenEvents.ConsumingOperator<AbstractWidget> addWidget = new ScreenEvents.ConsumingOperator<>(widgets::add);
                ScreenEvents.ConsumingOperator<AbstractWidget> removeWidget = new ScreenEvents.ConsumingOperator<>(widgets::remove);
                callback.onAfterInit(minecraft, screen, scaledWidth, scaledHeight, Collections.unmodifiableList(widgets), addWidget, removeWidget);
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
            return (screen, mouseX, mouseY, button) -> {
                return callback.onBeforeMouseClick(screen, mouseX, mouseY, button).isPass();
            };
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::allowMouseClick);
        registerScreenEvent(ScreenMouseEvents.AfterMouseClick.class, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AfterMouseClick.class, callback -> {
            return callback::onAfterMouseClick;
        }, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents::afterMouseClick);
        registerScreenEvent(ScreenMouseEvents.BeforeMouseRelease.class, net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AllowMouseRelease.class, callback -> {
            return (screen, mouseX, mouseY, button) -> {
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
        INSTANCE.register(RenderGuiElementEvents.Before.class, (context, applyToInvoker, removeInvoker) -> {
            Objects.requireNonNull(context, "context is null");
            applyToInvoker.accept(FabricClientEvents.beforeRenderGuiElement(((RenderGuiElementEvents.GuiOverlay) context).id()));
        });
        INSTANCE.register(RenderGuiElementEvents.After.class, (context, applyToInvoker, removeInvoker) -> {
            Objects.requireNonNull(context, "context is null");
            applyToInvoker.accept(FabricClientEvents.afterRenderGuiElement(((RenderGuiElementEvents.GuiOverlay) context).id()));
        });
        INSTANCE.register(CustomizeChatPanelCallback.class, FabricClientEvents.CUSTOMIZE_CHAT_PANEL);
        INSTANCE.register(ClientEntityLevelEvents.Load.class, FabricClientEvents.ENTITY_LOAD);
        INSTANCE.register(ClientEntityLevelEvents.Unload.class, ClientEntityEvents.ENTITY_UNLOAD, callback -> {
            return callback::onEntityUnload;
        });
        INSTANCE.register(InputEvents.BeforeMouseAction.class, FabricClientEvents.BEFORE_MOUSE_ACTION);
        INSTANCE.register(InputEvents.AfterMouseAction.class, FabricClientEvents.AFTER_MOUSE_ACTION);
        INSTANCE.register(InputEvents.BeforeMouseScroll.class, FabricClientEvents.BEFORE_MOUSE_SCROLL);
        INSTANCE.register(InputEvents.AfterMouseScroll.class, FabricClientEvents.AFTER_MOUSE_SCROLL);
        INSTANCE.register(InputEvents.BeforeKeyAction.class, FabricClientEvents.BEFORE_KEY_ACTION);
        INSTANCE.register(InputEvents.AfterKeyAction.class, FabricClientEvents.AFTER_KEY_ACTION);
        INSTANCE.register(RenderLivingEvents.Before.class, FabricClientEvents.BEFORE_RENDER_LIVING);
        INSTANCE.register(RenderLivingEvents.After.class, FabricClientEvents.AFTER_RENDER_LIVING);
        INSTANCE.register(RenderPlayerEvents.Before.class, FabricClientEvents.BEFORE_RENDER_PLAYER);
        INSTANCE.register(RenderPlayerEvents.After.class, FabricClientEvents.AFTER_RENDER_PLAYER);
        INSTANCE.register(RenderHandCallback.class, FabricClientEvents.RENDER_HAND);
        INSTANCE.register(ComputeCameraAnglesCallback.class, FabricClientEvents.COMPUTE_CAMERA_ANGLES);
        INSTANCE.register(ClientLevelTickEvents.Start.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_WORLD_TICK, callback -> {
            return (ClientLevel world) -> {
                callback.onStartLevelTick(Minecraft.getInstance(), world);
            };
        });
        INSTANCE.register(ClientLevelTickEvents.End.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_WORLD_TICK, callback -> {
            return (ClientLevel world) -> {
                callback.onEndLevelTick(Minecraft.getInstance(), world);
            };
        });
        INSTANCE.register(ClientChunkEvents.Load.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_LOAD, callback -> {
            return callback::onChunkLoad;
        });
        INSTANCE.register(ClientChunkEvents.Unload.class, net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_UNLOAD, callback -> {
            return callback::onChunkUnload;
        });
        INSTANCE.register(ClientPlayerEvents.LoggedIn.class, FabricClientEvents.PLAYER_LOGGED_IN);
        INSTANCE.register(ClientPlayerEvents.LoggedOut.class, FabricClientEvents.PLAYER_LOGGED_OUT);
        INSTANCE.register(ClientPlayerEvents.Copy.class, FabricClientEvents.PLAYER_COPY);
        INSTANCE.register(InteractionInputEvents.Attack.class, ClientPreAttackCallback.EVENT, callback -> {
            return (Minecraft minecraft, LocalPlayer player, int clickCount) -> {
                if (minecraft.missTime <= 0 && minecraft.hitResult != null) {
                    if (clickCount != 0) {
                        if (!player.isHandsBusy()) {
                            ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
                            if (itemInHand.isItemEnabled(minecraft.level.enabledFeatures())) {
                                return callback.onAttackInteraction(minecraft, player).isInterrupt();
                            }
                        }
                    } else {
                        if (!player.isUsingItem() && minecraft.hitResult.getType() == HitResult.Type.BLOCK) {
                            if (!minecraft.level.isEmptyBlock(((BlockHitResult) minecraft.hitResult).getBlockPos())) {
                                return callback.onAttackInteraction(minecraft, player).isInterrupt();
                            }
                        }
                    }
                }
                return false;
            };
        });
        INSTANCE.register(InteractionInputEvents.AttackV2.class, ClientPreAttackCallback.EVENT, callback -> {
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
        INSTANCE.register(InteractionInputEvents.Use.class, UseBlockCallback.EVENT, (callback, context) -> {
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
        INSTANCE.register(InteractionInputEvents.Use.class, UseEntityCallback.EVENT, (callback, context) -> {
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
        INSTANCE.register(InteractionInputEvents.Use.class, UseItemCallback.EVENT, (callback, context) -> {
            return (Player player, Level level, InteractionHand hand) -> {
                // this is only fired client-side to mimic InputEvent$InteractionKeyMappingTriggered on Forge
                // proper handling of the Fabric callback with the server-side component is implemented elsewhere
                if (!level.isClientSide) return InteractionResultHolder.pass(ItemStack.EMPTY);
                Minecraft minecraft = Minecraft.getInstance();
                EventResult result = callback.onUseInteraction(minecraft, (LocalPlayer) player, hand, minecraft.hitResult);
                // when interrupted cancel the interaction without the server being notified
                return result.isInterrupt() ? InteractionResultHolder.fail(ItemStack.EMPTY) : InteractionResultHolder.pass(ItemStack.EMPTY);
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
        INSTANCE.register(ClientLevelEvents.Load.class, FabricClientEvents.LOAD_LEVEL);
        INSTANCE.register(ClientLevelEvents.Unload.class, FabricClientEvents.UNLOAD_LEVEL);
        INSTANCE.register(MovementInputUpdateCallback.class, FabricClientEvents.MOVEMENT_INPUT_UPDATE);
        INSTANCE.register(ModelEvents.ModifyBakingResult.class, FabricClientEvents.MODIFY_BAKING_RESULT);
        INSTANCE.register(ModelEvents.BakingCompleted.class, FabricClientEvents.BAKING_COMPLETED);
        INSTANCE.register(ModelEvents.CompleteModelLoading.class, FabricClientEvents.COMPLETE_MODEL_LOADING);
        INSTANCE.register(RenderBlockOverlayCallback.class, FabricClientEvents.RENDER_BLOCK_OVERLAY);
        INSTANCE.register(FogEvents.Render.class, FabricClientEvents.RENDER_FOG);
        INSTANCE.register(FogEvents.ComputeColor.class, FabricClientEvents.COMPUTE_FOG_COLOR);
        INSTANCE.register(ScreenTooltipEvents.Render.class, FabricScreenEvents.RENDER_TOOLTIP);
        INSTANCE.register(RenderHighlightCallback.class, WorldRenderEvents.BEFORE_BLOCK_OUTLINE, callback -> {
            return (WorldRenderContext context, @Nullable HitResult hitResult) -> {
                if (hitResult == null || hitResult.getType() == HitResult.Type.MISS || hitResult.getType() == HitResult.Type.BLOCK && !context.blockOutlines()) return true;
                Minecraft minecraft = Minecraft.getInstance();
                if (!(minecraft.getCameraEntity() instanceof Player) || minecraft.options.hideGui) return true;
                EventResult result = callback.onRenderHighlight(context.worldRenderer(), context.camera(), context.gameRenderer(), hitResult, context.tickDelta(), context.matrixStack(), context.consumers(), context.world());
                return result.isPass();
            };
        });
        INSTANCE.register(RenderLevelEvents.AfterTerrain.class, WorldRenderEvents.BEFORE_ENTITIES, callback -> {
            return (WorldRenderContext context) -> {
                callback.onRenderLevelAfterTerrain(context.worldRenderer(), context.camera(), context.gameRenderer(), context.tickDelta(), context.matrixStack(), context.projectionMatrix(), context.frustum(), context.world());
            };
        });
        INSTANCE.register(RenderLevelEvents.AfterEntities.class, WorldRenderEvents.AFTER_ENTITIES, callback -> {
            return (WorldRenderContext context) -> {
                callback.onRenderLevelAfterEntities(context.worldRenderer(), context.camera(), context.gameRenderer(), context.tickDelta(), context.matrixStack(), context.projectionMatrix(), context.frustum(), context.world());
            };
        });
        INSTANCE.register(RenderLevelEvents.AfterTranslucent.class, WorldRenderEvents.AFTER_TRANSLUCENT, callback -> {
            return (WorldRenderContext context) -> {
                callback.onRenderLevelAfterTranslucent(context.worldRenderer(), context.camera(), context.gameRenderer(), context.tickDelta(), context.matrixStack(), context.projectionMatrix(), context.frustum(), context.world());
            };
        });
        INSTANCE.register(RenderLevelEvents.AfterLevel.class, WorldRenderEvents.END, callback -> {
            return (WorldRenderContext context) -> {
                callback.onRenderLevelAfterLevel(context.worldRenderer(), context.camera(), context.gameRenderer(), context.tickDelta(), context.matrixStack(), context.projectionMatrix(), context.frustum(), context.world());
            };
        });
        INSTANCE.register(GameRenderEvents.Before.class, FabricClientEvents.BEFORE_GAME_RENDER);
        INSTANCE.register(GameRenderEvents.After.class, FabricClientEvents.AFTER_GAME_RENDER);
        INSTANCE.register(AddToastCallback.class, FabricClientEvents.ADD_TOAST);
        INSTANCE.register(GatherDebugTextEvents.Left.class, FabricClientEvents.GATHER_LEFT_DEBUG_TEXT);
        INSTANCE.register(GatherDebugTextEvents.Right.class, FabricClientEvents.GATHER_RIGHT_DEBUG_TEXT);
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
