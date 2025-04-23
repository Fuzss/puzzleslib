package fuzs.puzzleslib.impl.content.client;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.api.client.event.v1.gui.AddToastCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenMouseEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenOpeningCallback;
import fuzs.puzzleslib.api.client.gui.v2.GuiHeightHelper;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class PuzzlesLibClientDevelopment implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ScreenOpeningCallback.EVENT.register((@Nullable Screen oldScreen, @Nullable Screen newScreen) -> {
            if (newScreen instanceof TitleScreen screen) {
                screen.fading = false;
            } else if (newScreen instanceof CreateWorldScreen screen) {
                screen.getUiState().setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE);
                screen.getUiState().setAllowCommands(true);
            }
            return EventResultHolder.pass();
        });
        ScreenEvents.beforeInit(TitleScreen.class)
                .register((Minecraft minecraft, TitleScreen screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets) -> {
                    if (minecraft.getOverlay() instanceof LoadingOverlay loadingOverlay &&
                            loadingOverlay.fadeOutStart != 0L) {
                        loadingOverlay.fadeOutStart = 0L;
                    }
                });
        AddToastCallback.EVENT.register((ToastManager toastManager, Toast toast) -> {
            if (toast instanceof SystemToast systemToast &&
                    systemToast.getToken() == SystemToast.SystemToastId.UNSECURE_SERVER_WARNING) {
                // block the annoying unsecure server warning when joining a multiplayer server in offline mode
                return EventResult.INTERRUPT;
            } else if (toastManager.freeSlotCount() == 0) {
                // prevent toast spam when unlocking all advancements at once
                return EventResult.INTERRUPT;
            } else {
                return EventResult.PASS;
            }
        });
        // required for EditBox mixin to work properly on all screens like ChatScreen
        ScreenMouseEvents.beforeMouseClick(Screen.class)
                .register((Screen screen, double mouseX, double mouseY, int button) -> {
                    for (GuiEventListener guiEventListener : screen.children()) {
                        if (guiEventListener instanceof EditBox &&
                                guiEventListener.mouseClicked(mouseX, mouseY, button)) {
                            screen.setFocused(guiEventListener);
                            if (button == InputConstants.MOUSE_BUTTON_LEFT) {
                                screen.setDragging(true);
                            }
                            return EventResult.INTERRUPT;
                        }
                    }
                    return EventResult.PASS;
                });
        ScreenMouseEvents.beforeMouseRelease(Screen.class)
                .register((Screen screen, double mouseX, double mouseY, int button) -> {
                    screen.setDragging(false);
                    return screen.getChildAt(mouseX, mouseY)
                            .filter(EditBox.class::isInstance)
                            .filter((GuiEventListener guiEventListener) -> {
                                return guiEventListener.mouseReleased(mouseX, mouseY, button);
                            })
                            .isPresent() ? EventResult.INTERRUPT : EventResult.PASS;
                });
        ScreenMouseEvents.beforeMouseDrag(Screen.class)
                .register((Screen screen, double mouseX, double mouseY, int button, double dragX, double dragY) -> {
                    return screen.getFocused() instanceof EditBox && screen.isDragging() &&
                            button == InputConstants.MOUSE_BUTTON_LEFT &&
                            screen.getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY) ?
                            EventResult.INTERRUPT : EventResult.PASS;
                });
    }

    @Override
    public void onClientSetup() {
        CreativeModeInventoryScreen.selectedTab = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.SEARCH);
        initializeScreenSkipper();
    }

    private static void initializeScreenSkipper() {
        // skip experimental settings warning
        ScreenSkipper.create()
                .setTitleComponent("selectWorld.backupQuestion.experimental")
                .setButtonComponent("selectWorld.backupJoinSkipButton")
                .build();
        ScreenSkipper.create()
                .setTitleComponent("selectWorld.warning.experimental.title")
                .setButtonComponent(CommonComponents.GUI_YES)
                .build();
        // launch directly into the key binds screen from the controls button
        ScreenSkipper.create()
                .setTitleComponent("controls.title")
                .setButtonComponent("controls.keybinds")
                .setLastTitleComponent("options.title")
                .build();
    }

    public static void setupGameOptions(Options options) {
        Minecraft minecraft = Minecraft.getInstance();
        Objects.requireNonNull(minecraft, "minecraft is null");
        // disable to prevent some options from accessing fields that have not yet been initialized
        boolean running = minecraft.running;
        minecraft.running = false;
        initializeGameOptions(options);
        minecraft.running = running;
        // no need to save preemptively, we will just apply our settings again if necessary
    }

    public static void initializeGameOptions(Options options) {
        if (options.getFile().exists()) return;
        options.renderDistance().set(16);
        options.framerateLimit().set(60);
        options.narratorHotkey().set(false);
        options.advancedItemTooltips = true;
        options.tutorialStep = TutorialSteps.NONE;
        options.joinedFirstServer = true;
        options.operatorItemsTab().set(true);
        options.entityShadows().set(false);
        options.realmsNotifications().set(false);
        options.showSubtitles().set(true);
        options.guiScale().set(8);
        options.onboardAccessibility = false;
        options.skipMultiplayerWarning = true;
        options.damageTiltStrength().set(0.0);
    }

    @Override
    public void onRegisterGuiLayers(GuiLayersContext context) {
        context.replaceGuiLayer(GuiLayersContext.JUMP_METER, (LayeredDraw.Layer layer) -> {
            return (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                Gui gui = Minecraft.getInstance().gui;
                PlayerRideableJumping playerRideableJumping = gui.minecraft.player.jumpableVehicle();
                if (playerRideableJumping != null) {
                    if (this.isExperienceBarVisible(gui)) {
                        int i = guiGraphics.guiWidth() / 2 - 91;
                        gui.renderExperienceBar(guiGraphics, i);
                        this.renderExperienceLevel(gui, guiGraphics, deltaTracker);
                    } else {
                        layer.render(guiGraphics, deltaTracker);
                    }
                }
            };
        });
        MutableBoolean mutableBoolean = new MutableBoolean();
        context.replaceGuiLayer(GuiLayersContext.AIR_LEVEL, (LayeredDraw.Layer layer) -> {
            return (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                Gui gui = Minecraft.getInstance().gui;
                Player player = gui.getCameraPlayer();
                LivingEntity livingEntity = gui.getPlayerVehicleWithHealth();
                int vehicleMaxHealth = gui.getVehicleMaxHearts(livingEntity);
                int m = guiGraphics.guiWidth() / 2 + 91;
                if (mutableBoolean.isFalse() && vehicleMaxHealth > 0) {
                    mutableBoolean.setTrue();
                    gui.renderFood(guiGraphics,
                            player,
                            guiGraphics.guiHeight() - GuiHeightHelper.getRightHeight(gui),
                            m);
                    GuiHeightHelper.addRightHeight(gui, 10);
                    this.renderVehicleHealth(gui, guiGraphics);
                    gui.renderAirBubbles(guiGraphics,
                            player,
                            10,
                            guiGraphics.guiHeight() - GuiHeightHelper.getRightHeight(gui),
                            m);
                    GuiHeightHelper.addRightHeight(gui, 10);
                    mutableBoolean.setFalse();
                } else {
                    layer.render(guiGraphics, deltaTracker);
                }
            };
        });
        context.replaceGuiLayer(GuiLayersContext.VEHICLE_HEALTH, GuiLayersContext.EMPTY);
    }

    private static final ResourceLocation HEART_VEHICLE_CONTAINER_SPRITE = ResourceLocation.withDefaultNamespace(
            "hud/heart/vehicle_container");
    private static final ResourceLocation HEART_VEHICLE_FULL_SPRITE = ResourceLocation.withDefaultNamespace(
            "hud/heart/vehicle_full");
    private static final ResourceLocation HEART_VEHICLE_HALF_SPRITE = ResourceLocation.withDefaultNamespace(
            "hud/heart/vehicle_half");

    private void renderVehicleHealth(Gui gui, GuiGraphics guiGraphics) {
        LivingEntity livingEntity = gui.getPlayerVehicleWithHealth();
        if (livingEntity != null) {
            int i = gui.getVehicleMaxHearts(livingEntity);
            if (i != 0) {
                int j = (int) Math.ceil(livingEntity.getHealth());
                Profiler.get().popPush("mountHealth");
                int k = guiGraphics.guiHeight() - GuiHeightHelper.getRightHeight(gui);
                int l = guiGraphics.guiWidth() / 2 + 91;
                int m = k;

                for (int n = 0; i > 0; n += 20) {
                    int o = Math.min(i, 10);
                    i -= o;

                    for (int p = 0; p < o; p++) {
                        int q = l - p * 8 - 9;
                        guiGraphics.blitSprite(RenderType::guiTextured, HEART_VEHICLE_CONTAINER_SPRITE, q, m, 9, 9);
                        if (p * 2 + 1 + n < j) {
                            guiGraphics.blitSprite(RenderType::guiTextured, HEART_VEHICLE_FULL_SPRITE, q, m, 9, 9);
                        }

                        if (p * 2 + 1 + n == j) {
                            guiGraphics.blitSprite(RenderType::guiTextured, HEART_VEHICLE_HALF_SPRITE, q, m, 9, 9);
                        }
                    }

                    m -= 10;
                    GuiHeightHelper.addRightHeight(gui, 10);
                }
            }
        }
    }

    private boolean isExperienceBarVisible(Gui gui) {
        return gui.minecraft.gameMode.hasExperience() &&
                (gui.minecraft.player.jumpableVehicle() == null || gui.minecraft.player.getJumpRidingScale() == 0.0F);
    }

    private void renderExperienceLevel(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        int i = gui.minecraft.player.experienceLevel;
        if (i > 0) {
            Profiler.get().push("expLevel");
            String string = i + "";
            int j = (guiGraphics.guiWidth() - gui.getFont().width(string)) / 2;
            int k = guiGraphics.guiHeight() - 31 - 4;
            guiGraphics.drawString(gui.getFont(), string, j + 1, k, 0, false);
            guiGraphics.drawString(gui.getFont(), string, j - 1, k, 0, false);
            guiGraphics.drawString(gui.getFont(), string, j, k + 1, 0, false);
            guiGraphics.drawString(gui.getFont(), string, j, k - 1, 0, false);
            guiGraphics.drawString(gui.getFont(), string, j, k, 8453920, false);
            Profiler.get().pop();
        }
    }
}
