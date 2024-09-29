package fuzs.puzzleslib.fabric.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.core.EventHandlerProvider;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Objects;

public final class FabricClientAbstractions implements ClientAbstractions, EventHandlerProvider {
    /**
     * A key to be used with Fabric's {@link net.fabricmc.loader.api.ObjectShare}.
     * <p>
     * Contains the current render height of hotbar decorations as an {@link Integer}.
     * <p>
     * When rendering additional hotbar decorations on the screen make sure to update the value by adding the height of
     * the decorations.
     * <p>
     * The implementation is very similar to NeoForge's {@code Gui#leftHeight} &amp; {@code Gui#rightHeight}.
     */
    static final String KEY_GUI_LEFT_HEIGHT = PuzzlesLibMod.id("left_height").toString();
    /**
     * A key to be used with Fabric's {@link net.fabricmc.loader.api.ObjectShare}.
     * <p>
     * Contains the current render height of hotbar decorations as an {@link Integer}.
     * <p>
     * When rendering additional hotbar decorations on the screen make sure to update the value by adding the height of
     * the decorations.
     * <p>
     * The implementation is very similar to NeoForge's {@code Gui#leftHeight} &amp; {@code Gui#rightHeight}.
     */
    static final String KEY_GUI_RIGHT_HEIGHT = PuzzlesLibMod.id("right_height").toString();

    @Override
    public boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
        return keyMapping.matches(keyCode, scanCode);
    }

    @Override
    public ClientTooltipComponent createImageComponent(TooltipComponent imageComponent) {
        ClientTooltipComponent component = TooltipComponentCallback.EVENT.invoker().getComponent(imageComponent);
        return Objects.requireNonNullElseGet(component, () -> ClientTooltipComponent.create(imageComponent));
    }

    @Override
    public BakedModel getBakedModel(ModelManager modelManager, ResourceLocation resourceLocation) {
        return modelManager.getModel(resourceLocation);
    }

    @Override
    public RenderType getRenderType(Block block) {
        return ItemBlockRenderTypes.getChunkRenderType(block.defaultBlockState());
    }

    @Override
    public void registerRenderType(Block block, RenderType renderType) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
    }

    @Override
    public void registerRenderType(Fluid fluid, RenderType renderType) {
        BlockRenderLayerMap.INSTANCE.putFluid(fluid, renderType);
    }

    @Override
    public boolean onRenderTooltip(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner positioner) {
        return FabricGuiEvents.RENDER_TOOLTIP.invoker().onRenderTooltip(guiGraphics, font, mouseX, mouseY, components,
                positioner
        ).isInterrupt();
    }

    @Override
    public int getGuiLeftHeight(Gui gui) {
        return FabricLoader.getInstance().getObjectShare().get(KEY_GUI_LEFT_HEIGHT) instanceof Integer i ? i : 0;

    }

    @Override
    public int getGuiRightHeight(Gui gui) {
        return FabricLoader.getInstance().getObjectShare().get(KEY_GUI_RIGHT_HEIGHT) instanceof Integer i ? i : 0;
    }

    @Override
    public void addGuiLeftHeight(Gui gui, int leftHeight) {
        FabricLoader.getInstance().getObjectShare().put(KEY_GUI_LEFT_HEIGHT, this.getGuiLeftHeight(gui) + leftHeight);
    }

    @Override
    public void addGuiRightHeight(Gui gui, int rightHeight) {
        FabricLoader.getInstance().getObjectShare().put(KEY_GUI_RIGHT_HEIGHT,
                this.getGuiRightHeight(gui) + rightHeight
        );
    }

    @Override
    public void registerEventHandlers() {
        RenderGuiLayerEvents.before(RenderGuiLayerEvents.CAMERA_OVERLAYS).register(EventPhase.FIRST,
                (minecraft, guiGraphics, deltaTracker) -> {
                    FabricLoader.getInstance().getObjectShare().put(KEY_GUI_LEFT_HEIGHT, 39);
                    FabricLoader.getInstance().getObjectShare().put(KEY_GUI_RIGHT_HEIGHT, 39);
                    return EventResult.PASS;
                }
        );
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.PLAYER_HEALTH).register(EventPhase.FIRST,
                (minecraft, guiGraphics, deltaTracker) -> {
                    if (minecraft.getCameraEntity() instanceof Player player) {
                        int playerHealth = Mth.ceil(player.getHealth());
                        float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH),
                                (float) Math.max(minecraft.gui.displayHealth, playerHealth)
                        );
                        int absorptionAmount = Mth.ceil(player.getAbsorptionAmount());
                        int healthRows = Mth.ceil((maxHealth + (float) absorptionAmount) / 2.0F / 10.0F);
                        int healthRowShift = Math.max(10 - (healthRows - 2), 3);
                        this.addGuiLeftHeight(minecraft.gui, 10 + (healthRows - 1) * healthRowShift);
                    }
                }
        );
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.ARMOR_LEVEL).register(EventPhase.FIRST,
                (minecraft, guiGraphics, deltaTracker) -> {
                    if (minecraft.getCameraEntity() instanceof Player player && player.getArmorValue() > 0) {
                        this.addGuiLeftHeight(minecraft.gui, 10);
                    }
                }
        );
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.FOOD_LEVEL).register(EventPhase.FIRST,
                (minecraft, guiGraphics, deltaTracker) -> {
                    if (minecraft.getCameraEntity() instanceof Player) {
                        LivingEntity livingEntity = minecraft.gui.getPlayerVehicleWithHealth();
                        if (minecraft.gui.getVehicleMaxHearts(livingEntity) == 0) {
                            this.addGuiRightHeight(minecraft.gui, 10);
                        }
                    }
                }
        );
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.AIR_LEVEL).register(EventPhase.FIRST,
                (minecraft, guiGraphics, deltaTracker) -> {
                    if (minecraft.getCameraEntity() instanceof Player player) {
                        int maxAirSupply = player.getMaxAirSupply();
                        int airSupply = Math.min(player.getAirSupply(), maxAirSupply);
                        if (player.isEyeInFluid(FluidTags.WATER) || airSupply < maxAirSupply) {
                            this.addGuiRightHeight(minecraft.gui, 10);
                        }
                    }
                }
        );
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.VEHICLE_HEALTH).register(EventPhase.FIRST,
                (minecraft, guiGraphics, deltaTracker) -> {
                    if (minecraft.getCameraEntity() instanceof Player) {
                        LivingEntity livingEntity = minecraft.gui.getPlayerVehicleWithHealth();
                        int maxHearts = minecraft.gui.getVehicleMaxHearts(livingEntity);
                        this.addGuiRightHeight(minecraft.gui, 10 * Mth.ceil(maxHearts / 10.0F));
                    }
                }
        );
    }
}
