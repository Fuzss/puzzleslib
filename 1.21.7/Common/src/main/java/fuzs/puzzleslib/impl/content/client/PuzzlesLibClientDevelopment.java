package fuzs.puzzleslib.impl.content.client;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.api.client.event.v1.gui.AddToastCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenMouseEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenOpeningCallback;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
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
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
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
        // launch directly into the key binds screen from the control button
        ScreenSkipper.create()
                .setTitleComponent("controls.title")
                .setButtonComponent("controls.keybinds")
                .setLastTitleComponent("options.title")
                .build();
    }

    public static void setupGameOptions(Options options) {
        Minecraft minecraft = Minecraft.getInstance();
        Objects.requireNonNull(minecraft, "minecraft is null");
        // disable to prevent some options from accessing fields that have not yet been initialised
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
        context.replaceGuiLayer(GuiLayersContext.PLAYER_LIST, (GuiLayersContext.Layer layer) -> {
            return (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                Minecraft minecraft = Minecraft.getInstance();
                Scoreboard scoreboard = minecraft.level.getScoreboard();
                Objective objective = scoreboard.getDisplayObjective(DisplaySlot.LIST);
                if (minecraft.options.keyPlayerList.isDown() && minecraft.isLocalServer() &&
                        minecraft.player.connection.getListedOnlinePlayers().size() <= 1 && objective == null) {
                    minecraft.gui.tabList.setVisible(true);
                    minecraft.gui.tabList.render(guiGraphics, guiGraphics.guiWidth(), scoreboard, null);
                } else {
                    layer.render(guiGraphics, deltaTracker);
                }
            };
        });
    }
}
