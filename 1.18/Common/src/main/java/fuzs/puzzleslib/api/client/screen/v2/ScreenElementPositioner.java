package fuzs.puzzleslib.api.client.screen.v2;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A small helper class for positioning a new {@link AbstractWidget} on a screen next to an already existing widget.
 * <p>The implementation is able to locate the existing widget as indicated by provided translation keys,
 * and then only positions the new widget if it does not overlap with another already existing widget.
 * <p>This class is mainly intended for placing {@link net.minecraft.client.gui.components.ImageButton} instances on {@link net.minecraft.client.gui.screens.TitleScreen}
 * and {@link net.minecraft.client.gui.screens.PauseScreen}, as it's rather likely for other mods to interfere, introducing a necessity to check for possible overlap.
 * <p>Also when using this class during init events it is recommended to run the init events during a phase later than the normal event phase,
 * to possibly run after other mods have added their own widgets, so they can be taken into account when checking for overlap.
 */
public final class ScreenElementPositioner {

    private ScreenElementPositioner() {

    }

    /**
     * Attempts to position <code>element</code> next to other <code>widgets</code>, located via the translation keys provided in <code>translationKeys</code>.
     * <p>When successful the element position has already been changed, all that's left to the caller is actually adding the element to the current screen.
     *
     * @param element         the element to position next to another element already present on the screen
     * @param widgets         a view of all widgets on the current screen
     * @param translationKeys the translation keys to use for locating other widgets to place <code>element</code> next to, the key is retrieved from {@link AbstractWidget#getMessage()}; note that for widgets without a displayed {@link net.minecraft.network.chat.Component} (such as {@link net.minecraft.client.gui.components.ImageButton}) the narration key usually works, too
     * @return was positioning <code>element</code> successful; the element position has already been changed, all that's left to the caller is actually adding the element to the current screen; otherwise if positioning has failed <code>element</code> is unchanged
     */
    public static boolean tryPositionElement(AbstractWidget element, List<? extends GuiEventListener> widgets, String... translationKeys) {
        return tryPositionElement(element, widgets, false, translationKeys);
    }

    /**
     * Attempts to position <code>element</code> next to other <code>widgets</code>, located via the translation keys provided in <code>translationKeys</code>.
     * <p>When successful the element position has already been changed, all that's left to the caller is actually adding the element to the current screen.
     *
     * @param element            the element to position next to another element already present on the screen
     * @param widgets            a view of all widgets on the current screen
     * @param tryPositionRightFirst by default <code>element</code> will be tried to be placed on the left side of a found other element, only if that does not work placing on the right side is attempted; this option inverts that and tries to place to the right side first
     * @param translationKeys    the translation keys to use for locating other widgets to place <code>element</code> next to, the key is retrieved from {@link AbstractWidget#getMessage()}; note that for widgets without a displayed {@link net.minecraft.network.chat.Component} (such as {@link net.minecraft.client.gui.components.ImageButton}) the narration key usually works, too
     * @return was positioning <code>element</code> successful; the element position has already been changed, all that's left to the caller is actually adding the element to the current screen; otherwise if positioning has failed <code>element</code> is unchanged
     */
    public static boolean tryPositionElement(AbstractWidget element, List<? extends GuiEventListener> widgets, boolean tryPositionRightFirst, String... translationKeys) {
        return tryPositionElement(element, widgets, tryPositionRightFirst, 4, translationKeys);
    }

    /**
     * Attempts to position <code>element</code> next to other <code>widgets</code>, located via the translation keys provided in <code>translationKeys</code>.
     * <p>When successful the element position has already been changed, all that's left to the caller is actually adding the element to the current screen.
     *
     * @param element            the element to position next to another element already present on the screen
     * @param widgets            a view of all widgets on the current screen
     * @param tryPositionRightFirst by default <code>element</code> will be tried to be placed on the left side of a found other element, only if that does not work placing on the right side is attempted; this option inverts that and tries to place to the right side first
     * @param horizontalOffset   the horizontal distance between <code>element</code> and the other element it is placed next to
     * @param translationKeys    the translation keys to use for locating other widgets to place <code>element</code> next to, the key is retrieved from {@link AbstractWidget#getMessage()}; note that for widgets without a displayed {@link net.minecraft.network.chat.Component} (such as {@link net.minecraft.client.gui.components.ImageButton}) the narration key usually works, too
     * @return was positioning <code>element</code> successful; the element position has already been changed, all that's left to the caller is actually adding the element to the current screen; otherwise if positioning has failed <code>element</code> is unchanged
     */
    public static boolean tryPositionElement(AbstractWidget element, List<? extends GuiEventListener> widgets, boolean tryPositionRightFirst, int horizontalOffset, String... translationKeys) {
        final int originalX = element.x;
        final int originalY = element.y;
        for (String translationKey : translationKeys) {
            AbstractWidget otherElement = findElement(widgets, translationKey);
            if (otherElement != null) {
                moveElementToOther(element, otherElement, tryPositionRightFirst, horizontalOffset);
                if (noOverlapWithExisting(widgets, element)) {
                    return true;
                } else {
                    moveElementToOther(element, otherElement, !tryPositionRightFirst, horizontalOffset);
                    if (noOverlapWithExisting(widgets, element)) {
                        return true;
                    }
                }
            }
        }
        // reset if we were not successful in finding a position next to another widget
        setPosition(element, originalX, originalY);
        return false;
    }

    private static void moveElementToOther(AbstractWidget element, AbstractWidget otherElement, boolean tryPositionRightFirst, int horizontalOffset) {
        if (tryPositionRightFirst) {
            moveToRight(element, otherElement, horizontalOffset);
        } else {
            moveToLeft(element, otherElement, horizontalOffset);
        }
    }

    private static void moveToLeft(AbstractWidget element, AbstractWidget otherElement, int horizontalOffset) {
        setPosition(element, otherElement.x - element.getWidth() - horizontalOffset, otherElement.y);
    }

    private static void moveToRight(AbstractWidget element, AbstractWidget otherElement, int horizontalOffset) {
        setPosition(element, otherElement.x + otherElement.getWidth() + horizontalOffset, otherElement.y);
    }

    private static void setPosition(AbstractWidget element, int x, int y) {
        element.x = x;
        element.y = y;
    }

    private static boolean noOverlapWithExisting(List<? extends GuiEventListener> widgets, AbstractWidget element) {
        for (GuiEventListener widget : widgets) {
            if (widget instanceof AbstractWidget otherElement) {
                if (intersection(element, otherElement)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean intersection(AbstractWidget element, AbstractWidget otherElement) {
        int i = Math.max(element.x, otherElement.x);
        int j = Math.max(element.y, otherElement.y);
        int k = Math.min(element.x + element.getWidth(), otherElement.x + otherElement.getWidth());
        int l = Math.min(element.y + element.getHeight(), otherElement.y + otherElement.getHeight());
        return i < k && j < l;
    }

    @Nullable
    private static AbstractWidget findElement(List<? extends GuiEventListener> widgets, String translationKey) {
        for (GuiEventListener listener : widgets) {
            if (listener instanceof AbstractWidget widget) {
                if (matchesTranslationKey(widget, translationKey)) {
                    return widget;
                }
            }
        }
        return null;
    }

    private static boolean matchesTranslationKey(AbstractWidget widget, String translationKey) {
        final Component message = widget.getMessage();
        return message instanceof TranslatableComponent contents && contents.getKey().equals(translationKey);
    }
}
