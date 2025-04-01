package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.impl.chat.FormattedContentSink;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A helper class for converting various text representations.
 * <p>
 * Useful for text instances returned from {@link net.minecraft.client.StringSplitter} and
 * {@link Component#getVisualOrderText()}.
 */
public class ComponentHelper {

    /**
     * Converts an instance of {@link FormattedText} to a {@link Component}.
     *
     * @param formattedText the text to convert
     * @return the new component
     */
    public static Component toComponent(FormattedText formattedText) {
        return new FormattedContentSink(formattedText).getComponent();
    }

    /**
     * Converts an instance of {@link FormattedCharSequence} to a {@link Component}.
     *
     * @param formattedCharSequence the text to convert
     * @return the new component
     */
    public static Component toComponent(FormattedCharSequence formattedCharSequence) {
        return new FormattedContentSink(formattedCharSequence).getComponent();
    }

    /**
     * Converts an instance of {@link FormattedText} to a string which includes formatting codes supplied via configured
     * {@link net.minecraft.network.chat.Style Styles}.
     * <p>
     * This is mostly useful when working with instances where vanilla still renders raw strings, which inherently
     * support the old chat formatting system, such as in
     * {@link net.minecraft.client.gui.components.DebugScreenOverlay}.
     *
     * @param formattedText the text to convert
     * @return the string
     */
    public static String toString(FormattedText formattedText) {
        return new FormattedContentSink(formattedText).getString();
    }

    /**
     * Converts an instance of {@link FormattedCharSequence} to a string which includes formatting codes supplied via
     * configured {@link net.minecraft.network.chat.Style Styles}.
     * <p>
     * This is mostly useful when working with instances where vanilla still renders raw strings, which inherently
     * support the old chat formatting system, such as in
     * {@link net.minecraft.client.gui.components.DebugScreenOverlay}.
     *
     * @param formattedCharSequence the text to convert
     * @return the string
     */
    public static String toString(FormattedCharSequence formattedCharSequence) {
        return new FormattedContentSink(formattedCharSequence).getString();
    }

    /**
     * Used to check if the control key (command on Mac) is pressed, useful for item tooltips.
     * <p>
     * Always returns <code>false</code> on the server.
     */
    public static boolean hasControlDown() {
        return ProxyImpl.get().hasControlDown();
    }

    /**
     * Used to check if the shift key is pressed, useful for item tooltips.
     * <p>
     * Always returns <code>false</code> on the server.
     *
     * @return is the shift key pressed
     */
    public static boolean hasShiftDown() {
        return ProxyImpl.get().hasShiftDown();
    }

    /**
     * Used to check if the alt key is pressed, useful for item tooltips.
     * <p>
     * Always returns <code>false</code> on the server.
     *
     * @return is the alt key pressed
     */
    public static boolean hasAltDown() {
        return ProxyImpl.get().hasAltDown();
    }

    /**
     * Split a text component into multiple parts depending on a predefined max width.
     * <p>
     * Most useful for constructing item tooltips in
     * {@link net.minecraft.world.item.Item#appendHoverText(ItemStack, Item.TooltipContext, TooltipDisplay, Consumer,
     * TooltipFlag)}.
     * <p>
     * Always returns the unmodified component on the server.
     *
     * @param component the component to split
     * @return the split component
     */
    public static List<Component> splitTooltipLines(Component component) {
        Objects.requireNonNull(component, "component is null");
        return ProxyImpl.get().splitTooltipLines(component);
    }
}
