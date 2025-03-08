package fuzs.puzzleslib.mixin.client.accessor;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(BlockModelGenerators.class)
public interface BlockModelGeneratorsAccessor {

    @Accessor("nonOrientableTrapdoor")
    @Mutable
    void puzzleslib$setNonOrientableTrapdoor(List<Block> nonOrientableTrapdoor);

    @Accessor("texturedModels")
    @Mutable
    void puzzleslib$setTexturedModels(Map<Block, TexturedModel> texturedModels);

    @Accessor("nonOrientableTrapdoor")
    List<Block> puzzleslib$getNonOrientableTrapdoor();

    @Accessor("texturedModels")
    Map<Block, TexturedModel> puzzleslib$getTexturedModels();
}
