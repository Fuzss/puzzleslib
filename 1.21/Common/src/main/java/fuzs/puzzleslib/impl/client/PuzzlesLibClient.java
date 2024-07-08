package fuzs.puzzleslib.impl.client;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenMouseEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenOpeningCallback;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import org.jetbrains.annotations.Nullable;

public class PuzzlesLibClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        setupDevelopmentEnvironment();
    }

    private static void setupDevelopmentEnvironment() {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment() || ModLoaderEnvironment.INSTANCE.isDataGeneration()) return;
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isForgeLike()) {
            setupGameOptions();
        }
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ScreenOpeningCallback.EVENT.register((@Nullable Screen oldScreen, DefaultedValue<Screen> newScreen) -> {
            if (newScreen.get() instanceof CreateWorldScreen screen) {
                screen.getUiState().setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE);
                screen.getUiState().setAllowCommands(true);
            }
            return EventResult.PASS;
        });
        // required for EditBox mixin to work properly on all screens like ChatScreen
        ScreenMouseEvents.beforeMouseClick(Screen.class).register((Screen screen, double mouseX, double mouseY, int button) -> {
            for (GuiEventListener guiEventListener : screen.children()) {
                if (guiEventListener instanceof EditBox && guiEventListener.mouseClicked(mouseX, mouseY, button)) {
                    screen.setFocused(guiEventListener);
                    if (button == InputConstants.MOUSE_BUTTON_LEFT) {
                        screen.setDragging(true);
                    }
                    return EventResult.INTERRUPT;
                }
            }
            return EventResult.PASS;
        });
        ScreenMouseEvents.beforeMouseRelease(Screen.class).register((Screen screen, double mouseX, double mouseY, int button) -> {
            screen.setDragging(false);
            return screen.getChildAt(mouseX, mouseY).filter(EditBox.class::isInstance).filter((GuiEventListener guiEventListener) -> {
                return guiEventListener.mouseReleased(mouseX, mouseY, button);
            }).isPresent() ? EventResult.INTERRUPT : EventResult.PASS;
        });
        ScreenMouseEvents.beforeMouseDrag(Screen.class).register((Screen screen, double mouseX, double mouseY, int button, double dragX, double dragY) -> {
            return screen.getFocused() instanceof EditBox && screen.isDragging() && button == InputConstants.MOUSE_BUTTON_LEFT && screen.getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY) ? EventResult.INTERRUPT : EventResult.PASS;
        });
    }

    private static void setupGameOptions() {
        Minecraft minecraft = Minecraft.getInstance();
        // necessary to disable to prevent some options from accessing fields that have not yet been initialized
        boolean running = minecraft.running;
        minecraft.running = false;
        initializeGameOptions(minecraft.options);
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
        options.hideBundleTutorial = true;
        options.operatorItemsTab().set(true);
        options.entityShadows().set(false);
        options.realmsNotifications().set(false);
        options.showSubtitles().set(true);
        options.guiScale().set(5);
        options.onboardAccessibility = false;
        options.skipMultiplayerWarning = true;
    }

    @Override
    public void onClientSetup() {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment() || ModLoaderEnvironment.INSTANCE.isDataGeneration()) return;
        CreativeModeInventoryScreen.selectedTab = BuiltInRegistries.CREATIVE_MODE_TAB.getOrThrow(CreativeModeTabs.SEARCH);
    }
}
