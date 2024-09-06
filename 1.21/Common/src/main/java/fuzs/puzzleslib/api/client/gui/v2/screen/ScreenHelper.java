package fuzs.puzzleslib.api.client.gui.v2.screen;

import fuzs.puzzleslib.api.client.event.v1.gui.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * A {@link net.minecraft.client.gui.screens.Screen} related helper class.
 */
public final class ScreenHelper {

    private ScreenHelper() {
        // NO-OP
    }

    /**
     * @return current mouse x position
     */
    public static int getMouseX() {
        Minecraft minecraft = Minecraft.getInstance();
        return (int) (minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() /
                minecraft.getWindow().getScreenWidth());
    }

    /**
     * @return current mouse y position
     */
    public static int getMouseY() {
        Minecraft minecraft = Minecraft.getInstance();
        return (int) (minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() /
                minecraft.getWindow().getScreenHeight());
    }

    /**
     * Checks if the mouse cursor is hovering a defined region.
     *
     * @param posX   the x-start of the hover area
     * @param posY   the y-start of the hover area
     * @param width  the width from x-start of the hover area
     * @param height the height from y-start of the hover area
     * @param mouseX mouse x position
     * @param mouseY mouse y position
     * @return is the mouse cursor hovering the defined region
     */
    public static boolean isHovering(int posX, int posY, int width, int height, double mouseX, double mouseY) {
        return mouseX >= posX && mouseX < posX + width && mouseY >= posY && mouseY < posY + height;
    }

    /**
     * Allows for skipping a screen that's just been opened by automatically triggering the press action of a button on
     * that screen.
     *
     * @param screenType      the screen class type
     * @param titleKey        the screen title translation key
     * @param buttonComponent the button to press component
     * @param <T>             screen type
     */
    public static <T extends Screen> void autoPressScreenButton(Class<T> screenType, String titleKey, Component buttonComponent) {
        autoPressScreenButton(screenType, titleKey, ((TranslatableContents) buttonComponent.getContents()).getKey());
    }

    /**
     * Allows for skipping a screen that's just been opened by automatically triggering the press action of a button on
     * that screen.
     *
     * @param screenType the screen class type
     * @param titleKey   the screen title translation key
     * @param buttonKey  the button to press translation key
     * @param <T>        screen type
     */
    public static <T extends Screen> void autoPressScreenButton(Class<T> screenType, String titleKey, String buttonKey) {
        ScreenEvents.afterInit(screenType).register(
                (Minecraft minecraft, T screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets, UnaryOperator<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget) -> {
                    if (screen.getTitle().equals(Component.translatable(titleKey))) {
                        Component component = Component.translatable(buttonKey);
                        for (GuiEventListener guiEventListener : widgets) {
                            if (guiEventListener instanceof Button button) {
                                if (button.getMessage().equals(component)) {
                                    button.onPress();
                                    break;
                                }
                            }
                        }
                    }
                });
    }
}
