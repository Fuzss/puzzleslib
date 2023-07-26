package fuzs.puzzleslib.api.client.screen.v2;

import fuzs.puzzleslib.mixin.client.accessor.TooltipAccessor;
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
     * @param font       the font instance
     * @param components components to split and build the tooltip from
     * @return the tooltip instance
     */
    public static Tooltip create(Font font, FormattedText... components) {
        return create(font, Arrays.asList(components));
    }

    /**
     * Create a new tooltip instance from multiple lines of text.
     *
     * @param font       the font instance
     * @param components components to split and build the tooltip from
     * @return the tooltip instance
     */
    public static Tooltip create(Font font, List<? extends FormattedText> components) {
        List<FormattedCharSequence> lines = components.stream().flatMap(t -> font.split(t, 170).stream()).collect(Collectors.toList());
        return create(lines);
    }

    /**
     * Create a new tooltip instance from already split lines of text.
     *
     * @param lines the split lines to build the tooltip from
     * @return the tooltip instance
     */
    public static Tooltip create(List<FormattedCharSequence> lines) {
        Tooltip tooltip = Tooltip.create(CommonComponents.EMPTY, null);
        ((TooltipAccessor) tooltip).puzzleslib$setCachedTooltip(lines);
        return tooltip;
    }
}
