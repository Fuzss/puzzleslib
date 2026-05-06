package fuzs.puzzleslib.common.api.client.renderer.v1.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

/**
 * An extension of {@link HumanoidArmorLayer} exposing the
 * {@link net.minecraft.client.resources.model.EquipmentClientInfo.LayerType} choice via
 * {@link #useBabyLayer(HumanoidRenderState)}.
 * <p>
 * By default, the baby layer is never used.
 *
 * @param <S> the render state
 * @param <M> the entity model
 * @param <A> the armor model
 */
public class SimpleHumanoidArmorLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> extends HumanoidArmorLayer<S, M, A> {

    public SimpleHumanoidArmorLayer(RenderLayerParent<S, M> renderer, ArmorModelSet<A> modelSet, EquipmentLayerRenderer equipmentRenderer) {
        super(renderer, modelSet, equipmentRenderer);
    }

    public SimpleHumanoidArmorLayer(RenderLayerParent<S, M> renderer, ArmorModelSet<A> modelSet, ArmorModelSet<A> babyModelSet, EquipmentLayerRenderer equipmentRenderer) {
        super(renderer, modelSet, babyModelSet, equipmentRenderer);
    }

    @Override
    public void renderArmorPiece(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStack itemStack, EquipmentSlot slot, int lightCoords, S state) {
        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable != null && shouldRender(equippable, slot)) {
            A model = this.getArmorModel(state, slot);
            EquipmentClientInfo.LayerType layerType =
                    this.useBabyLayer(state) ? EquipmentClientInfo.LayerType.HUMANOID_BABY :
                            this.usesInnerModel(slot) ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS :
                            EquipmentClientInfo.LayerType.HUMANOID;
            this.equipmentRenderer.renderLayers(layerType,
                    equippable.assetId().orElseThrow(),
                    model,
                    state,
                    itemStack,
                    poseStack,
                    submitNodeCollector,
                    lightCoords,
                    state.outlineColor);
        }
    }

    /**
     * @param state the render state
     * @return should {@link net.minecraft.client.resources.model.EquipmentClientInfo.LayerType#HUMANOID_BABY} be used
     *
     * @see net.minecraft.client.renderer.entity.layers.ItemInHandLayer#useBabyOffset(ArmedEntityRenderState)
     */
    public boolean useBabyLayer(S state) {
        return false;
    }
}
