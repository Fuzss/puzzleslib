package fuzs.puzzleslib.impl.client.core;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public final class ForgeClientAbstractions implements ClientAbstractions {

    @Override
    public boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
        return keyMapping.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode));
    }

    @Override
    public ClientTooltipComponent createImageComponent(TooltipComponent imageComponent) {
        return ClientTooltipComponent.create(imageComponent);
    }

    @Override
    public BakedModel getBakedModel(ResourceLocation identifier) {
        return Minecraft.getInstance().getModelManager().getModel(identifier);
    }

    @Override
    public float getPartialTick() {
        return Minecraft.getInstance().getPartialTick();
    }

    @Override
    public SearchRegistry getSearchRegistry() {
        return Minecraft.getInstance().getSearchTreeManager();
    }
}
