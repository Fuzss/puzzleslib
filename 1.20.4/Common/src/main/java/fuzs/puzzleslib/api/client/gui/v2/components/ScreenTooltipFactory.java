package fuzs.puzzleslib.api.client.gui.v2.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A small helper class for creating new instances of {@link Tooltip} from multiple {@link net.minecraft.network.chat.Component}s instead of just a single one,
 * as is the case in the vanilla implementation.
 */
public final class ScreenTooltipFactory {

    private ScreenTooltipFactory() {

    }

    /**
     * Create a new tooltip instance from multiple lines of text.
     *
     * @param components components to split and build the tooltip from
     * @return the tooltip instance
     */
    public static Tooltip create(FormattedText... components) {
        return create(Arrays.asList(components));
    }

    /**
     * Create a new tooltip instance from multiple lines of text.
     *
     * @param components components to split and build the tooltip from
     * @return the tooltip instance
     */
    public static Tooltip create(List<? extends FormattedText> components) {
        Font font = Minecraft.getInstance().font;
        List<FormattedCharSequence> lines = components.stream()
                .flatMap(t -> {
                    List<FormattedCharSequence> list = font.split(t, 170);
                    // empty components yield an empty list
                    // since empty lines are desired on tooltips make sure they don't go missing
                    if (list.isEmpty()) list = List.of(FormattedCharSequence.EMPTY);
                    return list.stream();
                })
                .collect(Collectors.toList());
        return createTooltip(lines);
    }

    /**
     * Create a new tooltip instance from already split lines of text.
     *
     * @param lines the split lines to build the tooltip from
     * @return the tooltip instance
     */
    public static Tooltip createTooltip(List<FormattedCharSequence> lines) {
        Tooltip tooltip = Tooltip.create(CommonComponents.EMPTY, null);
        tooltip.cachedTooltip = lines;
        return tooltip;
    }
}
