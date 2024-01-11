package fuzs.puzzleslib.mixin.client.accessor;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Tooltip.class)
public interface TooltipAccessor {

    @Accessor("cachedTooltip")
    void puzzleslib$setCachedTooltip(@Nullable List<FormattedCharSequence> cachedTooltip);
}
