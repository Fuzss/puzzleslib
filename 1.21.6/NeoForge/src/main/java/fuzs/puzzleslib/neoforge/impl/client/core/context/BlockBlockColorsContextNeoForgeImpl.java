package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BlockColorsContext;
import fuzs.puzzleslib.neoforge.mixin.client.accessor.BlockColorsNeoForgeAccessor;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record BlockBlockColorsContextNeoForgeImpl(RegisterColorHandlersEvent.Block evt) implements BlockColorsContext {

    @Override
    public void registerBlockColor(Block block, BlockColor blockColor) {
        Objects.requireNonNull(blockColor, "block color is null");
        Objects.requireNonNull(block, "block is null");
        this.evt.register(blockColor, block);
    }

    @Override
    public @Nullable BlockColor getBlockColor(Block block) {
        return ((BlockColorsNeoForgeAccessor) this.evt.getBlockColors()).puzzleslib$getBlockColors().get(block);
    }
}
