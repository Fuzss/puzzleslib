package fuzs.puzzleslib.forge.impl.client.event;

import com.google.common.base.Stopwatch;
import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.shaders.FogShape;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.InputEvents;
import fuzs.puzzleslib.api.client.event.v1.ModelEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.ClientEntityLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.*;
import fuzs.puzzleslib.api.client.event.v1.gui.*;
import fuzs.puzzleslib.api.client.event.v1.level.ClientChunkEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelTickEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.*;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.*;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.event.ScreenButtonList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static fuzs.puzzleslib.forge.api.event.v1.core.ForgeEventInvokerRegistry.INSTANCE;

@SuppressWarnings("unchecked")
public final class ForgeClientEventInvokers {
    private static final Supplier<Set<ResourceLocation>> TOP_LEVEL_MODEL_LOCATIONS = Suppliers.memoize(ForgeClientEventInvokers::getTopLevelModelLocations);

    public static void registerLoadingHandlers() {
        INSTANCE.register(ScreenOpeningCallback.class, ScreenEvent.Opening.class, (ScreenOpeningCallback callback, ScreenEvent.Opening evt) -> {
            DefaultedValue<Screen> newScreen = DefaultedValue.fromEvent(evt::setNewScreen, evt::getNewScreen, evt::getScreen);
            EventResult result = callback.onScreenOpening(evt.getCurrentScreen(), newScreen);
            // setting current screen again already prevents Screen#remove from running as implemented by Forge, but Screen#init still runs again,
            // we just manually fully cancel the event to deal in a more 'proper' way with this, the same is implemented on Fabric
            if (result.isInterrupt() || newScreen.getAsOptional().filter(screen -> screen == evt.getCurrentScreen()).isPresent())
                evt.setCanceled(true);
        });
        INSTANCE.register(ModelEvents.ModifyUnbakedModel.class, ModelEvent.ModifyBakingResult.class, (ModelEvents.ModifyUnbakedModel callback, ModelEvent.ModifyBakingResult evt) -> {
            Stopwatch stopwatch = Stopwatch.createStarted();
            Map<ResourceLocation, BakedModel> models = evt.getModels();
            // just like bakedCache in ModelBakery
            Map<ForgeModelBakerImpl.BakedCacheKey, BakedModel> bakedCache = Maps.newHashMap();
            Multimap<ResourceLocation, Material> missingTextures = HashMultimap.create();
            BakedModel missingModel = models.get(ModelBakery.MISSING_MODEL_LOCATION);
            Objects.requireNonNull(missingModel, "missing model is null");
            Map<ResourceLocation, UnbakedModel> additionalModels = Maps.newHashMap();
            Function<ResourceLocation, UnbakedModel> modelGetter = (ResourceLocation resourceLocation) -> {
                if (additionalModels.containsKey(resourceLocation)) {
                    return additionalModels.get(resourceLocation);
                } else {
                    return evt.getModelBakery().getModel(resourceLocation);
                }
            };
            // do not use resource location keys or rely on the baked cache used by the model baker, it will rebake different block states even though the model is the same
            // this also means we cannot use baked models as keys since they are different instances despite having been baked from the same unbaked model
            Map<UnbakedModel, BakedModel> unbakedCache = Maps.newIdentityHashMap();
            // Forge does not grant access to unbaked models, so lookup every unbaked model and replace the baked model if necessary
            // this also means the event is limited to top level models which should be fine though, the same restriction is applied on Fabric
            // do not iterate over the models map provided by the event, when Modern Fix is installed it will be almost empty as models are loaded dynamically
            for (ResourceLocation modelLocation : TOP_LEVEL_MODEL_LOCATIONS.get()) {
                try {
                    EventResultHolder<UnbakedModel> result = callback.onModifyUnbakedModel(modelLocation, () -> {
                        return modelGetter.apply(modelLocation);
                    }, modelGetter, (ResourceLocation resourceLocation, UnbakedModel unbakedModel) -> {
                        // the Fabric callback for adding additional models does not work with model resource locations, so force that restriction here, too
                        if (resourceLocation instanceof ModelResourceLocation) {
                            throw new IllegalArgumentException("model resource location is not supported");
                        } else {
                            additionalModels.put(resourceLocation, unbakedModel);
                        }
                    });
                    if (result.isInterrupt()) {
                        UnbakedModel unbakedModel = result.getInterrupt().get();
                        additionalModels.put(modelLocation, unbakedModel);
                        BakedModel bakedModel = unbakedCache.computeIfAbsent(unbakedModel, $ -> {
                            ForgeModelBakerImpl modelBaker = new ForgeModelBakerImpl(modelLocation, bakedCache, modelGetter, missingTextures::put, missingModel);
                            return modelBaker.bake(unbakedModel, modelLocation);
                        });
                        models.put(modelLocation, bakedModel);
                    }
                } catch (Exception exception) {
                    PuzzlesLib.LOGGER.error("Failed to modify unbaked model", exception);
                }
            }
            missingTextures.asMap().forEach((ResourceLocation resourceLocation, Collection<Material> materials) -> {
                PuzzlesLib.LOGGER.warn("Missing textures in model {}:\n{}", resourceLocation, materials.stream().sorted(Material.COMPARATOR).map((material) -> {
                    return "    " + material.atlasLocation() + ":" + material.texture();
                }).collect(Collectors.joining("\n")));
            });
            PuzzlesLib.LOGGER.info("Modifying unbaked models took {}ms", stopwatch.stop().elapsed().toMillis());
        });
        INSTANCE.register(ModelEvents.ModifyBakedModel.class, ModelEvent.ModifyBakingResult.class, (ModelEvents.ModifyBakedModel callback, ModelEvent.ModifyBakingResult evt) -> {
            Stopwatch stopwatch = Stopwatch.createStarted();
            Map<ResourceLocation, BakedModel> models = evt.getModels();
            // just like bakedCache in ModelBakery
            Map<ForgeModelBakerImpl.BakedCacheKey, BakedModel> bakedCache = Maps.newHashMap();
            Multimap<ResourceLocation, Material> missingTextures = HashMultimap.create();
            BakedModel missingModel = models.get(ModelBakery.MISSING_MODEL_LOCATION);
            Objects.requireNonNull(missingModel, "missing model is null");
            Function<ResourceLocation, ModelBaker> modelBaker = resourceLocation -> {
                return new ForgeModelBakerImpl(resourceLocation, bakedCache, evt.getModelBakery()::getModel, missingTextures::put, missingModel);
            };
            Function<ResourceLocation, BakedModel> modelGetter = (ResourceLocation resourceLocation) -> {
                if (models.containsKey(resourceLocation)) {
                    return models.get(resourceLocation);
                } else {
                    return modelBaker.apply(resourceLocation).bake(resourceLocation, BlockModelRotation.X0_Y0);
                }
            };
            // Forge has no event firing for every baked model like Fabric,
            // instead go through the baked models map and fire the event for every model manually
            // do not iterate over the models map provided by the event, when Modern Fix is installed it will be almost empty as models are loaded dynamically
            for (ResourceLocation modelLocation : TOP_LEVEL_MODEL_LOCATIONS.get()) {
                try {
                    EventResultHolder<BakedModel> result = callback.onModifyBakedModel(modelLocation, () -> {
                        return modelGetter.apply(modelLocation);
                    }, () -> {
                        return modelBaker.apply(modelLocation);
                    }, modelGetter, models::putIfAbsent);
                    result.getInterrupt().ifPresent(bakedModel -> {
                        models.put(modelLocation, bakedModel);
                    });
                } catch (Exception exception) {
                    PuzzlesLib.LOGGER.error("Failed to modify baked model", exception);
                }
            }
            missingTextures.asMap().forEach((ResourceLocation resourceLocation, Collection<Material> materials) -> {
                PuzzlesLib.LOGGER.warn("Missing textures in model {}:\n{}", resourceLocation, materials.stream().sorted(Material.COMPARATOR).map((material) -> {
                    return "    " + material.atlasLocation() + ":" + material.texture();
                }).collect(Collectors.joining("\n")));
            });
            PuzzlesLib.LOGGER.info("Modifying baked models took {}ms", stopwatch.stop().elapsed().toMillis());
        });
        INSTANCE.register(ModelEvents.AdditionalBakedModel.class, ModelEvent.ModifyBakingResult.class, (ModelEvents.AdditionalBakedModel callback, ModelEvent.ModifyBakingResult evt) -> {
            Stopwatch stopwatch = Stopwatch.createStarted();
            Map<ResourceLocation, BakedModel> models = evt.getModels();
            Multimap<ResourceLocation, Material> missingTextures = HashMultimap.create();
            Map<ForgeModelBakerImpl.BakedCacheKey, BakedModel> bakedCache = Maps.newHashMap();
            BakedModel missingModel = models.get(ModelBakery.MISSING_MODEL_LOCATION);
            Objects.requireNonNull(missingModel, "missing model is null");
            try {
                callback.onAdditionalBakedModel(models::putIfAbsent, (ResourceLocation resourceLocation) -> {
                    return models.getOrDefault(resourceLocation, missingModel);
                }, () -> {
                    // just use a dummy model, we cut this out when printing missing textures to the log
                    return new ForgeModelBakerImpl(ModelBakery.MISSING_MODEL_LOCATION, bakedCache, evt.getModelBakery()::getModel, missingTextures::put, missingModel);
                });
            } catch (Exception exception) {
                PuzzlesLib.LOGGER.error("Failed to add additional baked models", exception);
            }
            missingTextures.asMap().forEach((ResourceLocation resourceLocation, Collection<Material> materials) -> {
                PuzzlesLib.LOGGER.warn("Missing textures:\n{}", materials.stream().sorted(Material.COMPARATOR).map((material) -> {
                    return "    " + material.atlasLocation() + ":" + material.texture();
                }).collect(Collectors.joining("\n")));
            });
            PuzzlesLib.LOGGER.info("Adding additional baked models took {}ms", stopwatch.stop().elapsed().toMillis());
        });
        INSTANCE.register(ModelEvents.AfterModelLoading.class, ModelEvent.BakingCompleted.class, (ModelEvents.AfterModelLoading callback, ModelEvent.BakingCompleted evt) -> {
            callback.onAfterModelLoading(evt::getModelManager);
        });
    }

    public static void registerEventHandlers() {
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
        registerScreenEvent(ScreenEvents.BeforeInit.class, ScreenEvent.Init.Pre.class, (callback, evt) -> {
            callback.onBeforeInit(Minecraft.getInstance(), evt.getScreen(), evt.getScreen().width, evt.getScreen().height, new ScreenButtonList(evt.getScreen().renderables));
        });
        registerScreenEvent(ScreenEvents.AfterInit.class, ScreenEvent.Init.Post.class, (callback, evt) -> {
            ScreenEvents.ConsumingOperator<GuiEventListener> addWidget = new ScreenEvents.ConsumingOperator<>(evt::addListener);
            ScreenEvents.ConsumingOperator<GuiEventListener> removeWidget = new ScreenEvents.ConsumingOperator<>(evt::removeListener);
            callback.onAfterInit(Minecraft.getInstance(), evt.getScreen(), evt.getScreen().width, evt.getScreen().height, new ScreenButtonList(evt.getScreen().renderables), addWidget, removeWidget);
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
            EventResult result = callback.onBeforeMouseScroll(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getDeltaX(), evt.getDeltaY());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        registerScreenEvent(ScreenMouseEvents.AfterMouseScroll.class, ScreenEvent.MouseScrolled.Post.class, (callback, evt) -> {
            callback.onAfterMouseScroll(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getDeltaX(), evt.getDeltaY());
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
        INSTANCE.register(RenderGuiElementEvents.Before.class, RenderGuiOverlayEvent.Pre.class, (RenderGuiElementEvents.Before callback, RenderGuiOverlayEvent.Pre evt, @Nullable Object context) -> {
            Objects.requireNonNull(context, "context is null");
            RenderGuiElementEvents.GuiOverlay overlay = (RenderGuiElementEvents.GuiOverlay) context;
            Minecraft minecraft = Minecraft.getInstance();
            if (!evt.getOverlay().id().equals(overlay.id()) || !overlay.filter().test(minecraft)) return;
            EventResult result = callback.onBeforeRenderGuiElement(minecraft, evt.getGuiGraphics(), evt.getPartialTick(), evt.getWindow().getGuiScaledWidth(), evt.getWindow().getGuiScaledHeight());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(RenderGuiElementEvents.After.class, RenderGuiOverlayEvent.Post.class, (RenderGuiElementEvents.After callback, RenderGuiOverlayEvent.Post evt, @Nullable Object context) -> {
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
            EventResult result = callback.onEntityLoad(evt.getEntity(), (ClientLevel) evt.getLevel());
            if (result.isInterrupt()) {
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
            EventResult result = callback.onBeforeMouseScroll(evt.isLeftDown(), evt.isMiddleDown(), evt.isRightDown(), evt.getDeltaX(), evt.getDeltaY());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(InputEvents.AfterMouseScroll.class, InputEvent.MouseScrollingEvent.class, (InputEvents.AfterMouseScroll callback, InputEvent.MouseScrollingEvent evt) -> {
            // Forge doesn't have this, but shouldn't be important really
            callback.onAfterMouseScroll(evt.isLeftDown(), evt.isMiddleDown(), evt.isRightDown(), evt.getDeltaX(), evt.getDeltaY());
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
        INSTANCE.register(RenderLivingEvents.Before.class, RenderLivingEvent.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeRenderEntity(evt.getEntity(), evt.getRenderer(), evt.getPartialTick(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(RenderLivingEvents.After.class, RenderLivingEvent.Post.class, (callback, evt) -> {
            callback.onAfterRenderEntity(evt.getEntity(), evt.getRenderer(), evt.getPartialTick(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight());
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
        INSTANCE.register(RenderHandEvents.MainHand.class, RenderHandEvent.class, (RenderHandEvents.MainHand callback, RenderHandEvent evt) -> {
            if (evt.getHand() != InteractionHand.MAIN_HAND) return;
            Minecraft minecraft = Minecraft.getInstance();
            ItemInHandRenderer itemInHandRenderer = minecraft.getEntityRenderDispatcher().getItemInHandRenderer();
            EventResult result = callback.onRenderMainHand(itemInHandRenderer, minecraft.player, minecraft.player.getMainArm(), evt.getItemStack(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight(), evt.getPartialTick(), evt.getInterpolatedPitch(), evt.getSwingProgress(), evt.getEquipProgress());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(RenderHandEvents.OffHand.class, RenderHandEvent.class, (RenderHandEvents.OffHand callback, RenderHandEvent evt) -> {
            if (evt.getHand() != InteractionHand.OFF_HAND) return;
            Minecraft minecraft = Minecraft.getInstance();
            ItemInHandRenderer itemInHandRenderer = minecraft.getEntityRenderDispatcher().getItemInHandRenderer();
            EventResult result = callback.onRenderOffHand(itemInHandRenderer, minecraft.player, minecraft.player.getMainArm().getOpposite(), evt.getItemStack(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight(), evt.getPartialTick(), evt.getInterpolatedPitch(), evt.getSwingProgress(), evt.getEquipProgress());
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
            callback.onChunkLoad(level, (LevelChunk) evt.getChunk());
        });
        INSTANCE.register(ClientChunkEvents.Unload.class, ChunkEvent.Unload.class, (ClientChunkEvents.Unload callback, ChunkEvent.Unload evt) -> {
            if (!(evt.getLevel() instanceof ClientLevel level)) return;
            callback.onChunkUnload(level, (LevelChunk) evt.getChunk());
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
                EventResult result = callback.onAttackInteraction(minecraft, minecraft.player, minecraft.hitResult);
                if (result.isInterrupt()) {
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
                    EventResult result = callback.onUseInteraction(minecraft, minecraft.player, evt.getHand(), minecraft.hitResult);
                    if (result.isInterrupt()) {
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
            EventResult result = callback.onPickInteraction(minecraft, minecraft.player, minecraft.hitResult);
            if (result.isInterrupt()) {
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
        INSTANCE.register(RenderTooltipCallback.class, RenderTooltipEvent.Pre.class, (RenderTooltipCallback callback, RenderTooltipEvent.Pre evt) -> {
            EventResult result = callback.onRenderTooltip(evt.getGraphics(), evt.getFont(), evt.getX(), evt.getY(), evt.getComponents(), evt.getTooltipPositioner());
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
        INSTANCE.register(GameRenderEvents.Before.class, TickEvent.RenderTickEvent.class, (GameRenderEvents.Before callback, TickEvent.RenderTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.START) return;
            Minecraft minecraft = Minecraft.getInstance();
            callback.onBeforeGameRender(minecraft, minecraft.gameRenderer, evt.renderTickTime);
        });
        INSTANCE.register(GameRenderEvents.After.class, TickEvent.RenderTickEvent.class, (GameRenderEvents.After callback, TickEvent.RenderTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.END) return;
            Minecraft minecraft = Minecraft.getInstance();
            callback.onAfterGameRender(minecraft, minecraft.gameRenderer, evt.renderTickTime);
        });
        INSTANCE.register(AddToastCallback.class, ToastAddEvent.class, (AddToastCallback callback, ToastAddEvent evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            EventResult result = callback.onAddToast(minecraft.getToasts(), evt.getToast());
            if (result.isInterrupt()) evt.setCanceled(true);
        });
        INSTANCE.register(GatherDebugTextEvents.Left.class, CustomizeGuiOverlayEvent.DebugText.class, (GatherDebugTextEvents.Left callback, CustomizeGuiOverlayEvent.DebugText evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.getDebugOverlay().showDebugScreen()) return;
            callback.onGatherLeftDebugText(evt.getLeft());
        });
        INSTANCE.register(GatherDebugTextEvents.Right.class, CustomizeGuiOverlayEvent.DebugText.class, (GatherDebugTextEvents.Right callback, CustomizeGuiOverlayEvent.DebugText evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.getDebugOverlay().showDebugScreen()) return;
            callback.onGatherRightDebugText(evt.getRight());
        });
    }

    private static <T, E extends ScreenEvent> void registerScreenEvent(Class<T> clazz, Class<E> event, BiConsumer<T, E> converter) {
        INSTANCE.register(clazz, event, (T callback, E evt, @Nullable Object context) -> {
            Objects.requireNonNull(context, "context is null");
            if (!((Class<?>) context).isInstance(evt.getScreen())) return;
            converter.accept(callback, evt);
        });
    }

    private static Set<ResourceLocation> getTopLevelModelLocations() {
        Set<ResourceLocation> modelLocations = Sets.newHashSet(ModelBakery.MISSING_MODEL_LOCATION);
        for (Block block : BuiltInRegistries.BLOCK) {
            block.getStateDefinition().getPossibleStates().forEach(blockState -> {
                modelLocations.add(BlockModelShaper.stateToModelLocation(blockState));
            });
        }
        for (ResourceLocation resourcelocation : BuiltInRegistries.ITEM.keySet()) {
            modelLocations.add(new ModelResourceLocation(resourcelocation, "inventory"));
        }
        modelLocations.add(ItemRenderer.TRIDENT_IN_HAND_MODEL);
        modelLocations.add(ItemRenderer.SPYGLASS_IN_HAND_MODEL);
        // skip the Forge additional models call, we probably don't need those and better to avoid accessing internals
        return Collections.unmodifiableSet(modelLocations);
    }
}
