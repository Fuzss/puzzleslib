package fuzs.puzzleslib.api.client.gui.v2.components;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * A small helper class for positioning a new {@link AbstractWidget} on a screen next to an already existing widget.
 * <p>
 * The implementation is able to locate the existing widget as indicated by provided translation keys, and then only
 * positions the new widget if it does not overlap with another already existing widget.
 * <p>
 * This class is mainly intended for placing {@link net.minecraft.client.gui.components.ImageButton} instances on
 * {@link net.minecraft.client.gui.screens.TitleScreen} and {@link net.minecraft.client.gui.screens.PauseScreen}, as
 * it's rather likely for other mods to interfere, introducing a necessity to check for possible overlap.
 * <p>
 * Also, when using this class during init events it is recommended to run the init events during a phase later than the
 * normal event phase, to possibly run after other mods have added their own widgets, so they can be taken into account
 * when checking for overlap.
 */
public final class ScreenElementPositioner {

    private ScreenElementPositioner() {
        // NO-OP
    }

    /**
     * Attempts to position element next to other widgets, located via the provided translation keys.
     * <p>
     * When successful the element position has already been changed, all that's left to the caller is actually adding
     * the element to the current screen.
     *
     * @param element         the element to position next to another element already present on the screen
     * @param widgets         a view of all widgets on the current screen
     * @param translationKeys the translation keys to use for locating other widgets to place <code>element</code> next
     *                        to, the key is retrieved from {@link AbstractWidget#getMessage()}; note that for widgets
     *                        without a displayed {@link net.minecraft.network.chat.Component} (such as
     *                        {@link net.minecraft.client.gui.components.ImageButton}) the narration key usually works,
     *                        too
     * @return was positioning <code>element</code> successful; the element position has already been changed, all
     *         that's left to the caller is actually adding the element to the current screen; otherwise if positioning
     *         has failed <code>element</code> is unchanged
     */
    public static boolean tryPositionElement(LayoutElement element, List<? extends GuiEventListener> widgets, String... translationKeys) {
        return tryPositionElement(element, widgets, false, translationKeys);
    }

    /**
     * Attempts to position element next to other widgets, located via the provided translation keys.
     * <p>
     * When successful the element position has already been changed, all that's left to the caller is actually adding
     * the element to the current screen.
     *
     * @param element               the element to position next to another element already present on the screen
     * @param widgets               a view of all widgets on the current screen
     * @param tryPositionRightFirst by default <code>element</code> will be tried to be placed on the left side of a
     *                              found other element, only if that does not work placing on the right side is
     *                              attempted; this option inverts that and tries to place to the right side first
     * @param translationKeys       the translation keys to use for locating other widgets to place <code>element</code>
     *                              next to, the key is retrieved from {@link AbstractWidget#getMessage()}; note that
     *                              for widgets without a displayed {@link net.minecraft.network.chat.Component} (such
     *                              as {@link net.minecraft.client.gui.components.ImageButton}) the narration key
     *                              usually works, too
     * @return was positioning <code>element</code> successful; the element position has already been changed, all
     *         that's left to the caller is actually adding the element to the current screen; otherwise if positioning
     *         has failed <code>element</code> is unchanged
     */
    public static boolean tryPositionElement(LayoutElement element, List<? extends GuiEventListener> widgets, boolean tryPositionRightFirst, String... translationKeys) {
        return tryPositionElement(element, widgets, tryPositionRightFirst, 4, translationKeys);
    }

    /**
     * Attempts to position element next to other widgets, located via the provided translation keys.
     * <p>
     * When successful the element position has already been changed, all that's left to the caller is actually adding
     * the element to the current screen.
     *
     * @param element               the element to position next to another element already present on the screen
     * @param widgets               a view of all widgets on the current screen
     * @param tryPositionRightFirst by default <code>element</code> will be tried to be placed on the left side of a
     *                              found other element, only if that does not work placing on the right side is
     *                              attempted; this option inverts that and tries to place to the right side first
     * @param horizontalOffset      the horizontal distance between <code>element</code> and the other element it is
     *                              placed next to
     * @param translationKeys       the translation keys to use for locating other widgets to place <code>element</code>
     *                              next to, the key is retrieved from {@link AbstractWidget#getMessage()}; note that
     *                              for widgets without a displayed {@link net.minecraft.network.chat.Component} (such
     *                              as {@link net.minecraft.client.gui.components.ImageButton}) the narration key
     *                              usually works, too
     * @return was positioning <code>element</code> successful; the element position has already been changed, all
     *         that's left to the caller is actually adding the element to the current screen; otherwise if positioning
     *         has failed <code>element</code> is unchanged
     */
    public static boolean tryPositionElement(LayoutElement element, List<? extends GuiEventListener> widgets, boolean tryPositionRightFirst, int horizontalOffset, String... translationKeys) {
        final int originalX = element.getX();
        final int originalY = element.getY();
        for (String translationKey : translationKeys) {
            LayoutElement otherElement = findElement(widgets, translationKey);
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
        element.setPosition(originalX, originalY);
        return false;
    }

    private static void moveElementToOther(LayoutElement element, LayoutElement otherElement, boolean tryPositionRightFirst, int horizontalOffset) {
        if (tryPositionRightFirst) {
            moveToRight(element, otherElement, horizontalOffset);
        } else {
            moveToLeft(element, otherElement, horizontalOffset);
        }
    }

    private static void moveToLeft(LayoutElement element, LayoutElement otherElement, int horizontalOffset) {
        element.setPosition(otherElement.getX() - element.getWidth() - horizontalOffset, otherElement.getY());
    }

    private static void moveToRight(LayoutElement element, LayoutElement otherElement, int horizontalOffset) {
        element.setPosition(otherElement.getX() + otherElement.getWidth() + horizontalOffset, otherElement.getY());
    }

    private static boolean noOverlapWithExisting(List<? extends GuiEventListener> widgets, LayoutElement element) {
        for (GuiEventListener widget : widgets) {
            if (widget instanceof LayoutElement otherElement) {
                if (element.getRectangle().intersection(otherElement.getRectangle()) != null) {
                    return false;
                }
            }
        }

        return true;
    }

    @Nullable
    private static LayoutElement findElement(List<? extends GuiEventListener> widgets, String translationKey) {
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
        final ComponentContents message = widget.getMessage().getContents();
        return message instanceof TranslatableContents contents && contents.getKey().equals(translationKey);
    }
}
