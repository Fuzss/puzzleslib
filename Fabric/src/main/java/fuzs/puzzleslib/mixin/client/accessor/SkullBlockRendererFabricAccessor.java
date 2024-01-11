package fuzs.puzzleslib.mixin.client.accessor;

import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SkullBlockRenderer.class)
public interface SkullBlockRendererFabricAccessor {

    @Accessor("SKIN_BY_TYPE")
    static Map<SkullBlock.Type, ResourceLocation> puzzleslib$getSkinByType() {
        throw new IllegalStateException();
    }
}
