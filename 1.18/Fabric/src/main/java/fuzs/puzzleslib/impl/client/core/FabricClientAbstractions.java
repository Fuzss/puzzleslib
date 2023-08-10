package fuzs.puzzleslib.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public final class FabricClientAbstractions implements ClientAbstractions {

    @Override
    public boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
        return keyMapping.matches(keyCode, scanCode);
    }

    @Override
    public ClientTooltipComponent createImageComponent(TooltipComponent imageComponent) {
        ClientTooltipComponent component = TooltipComponentCallback.EVENT.invoker().getComponent(imageComponent);
        if (component != null) return component;
        return ClientTooltipComponent.create(imageComponent);
    }

    @Override
    public BakedModel getBakedModel(ModelManager modelManager, ResourceLocation identifier) {
        return BakedModelManagerHelper.getModel(modelManager, identifier);
    }
}
