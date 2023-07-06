package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.mixin.client.accessor.SkullBlockRendererFabricAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;

import java.util.Map;

public class FabricClientAbstractions implements ClientAbstractions {

    @Override
    public boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
        return keyMapping.matches(keyCode, scanCode);
    }

    @Override
    public Map<SkullBlock.Type, ResourceLocation> getSkullTypeSkins() {
        return SkullBlockRendererFabricAccessor.puzzleslib$getSkinByType();
    }
}
