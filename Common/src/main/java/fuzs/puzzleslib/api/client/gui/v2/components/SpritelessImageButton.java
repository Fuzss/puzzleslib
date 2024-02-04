package fuzs.puzzleslib.api.client.gui.v2.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.ToIntFunction;

/**
 * A button implementation very similar to the old {@link net.minecraft.client.gui.components.ImageButton} with a couple extra features:
 * <li>No need for using {@link WidgetSprites}.</li>
 * <li>Completely mutable to allow for manipulating texture coordinates, size and position after the button has been initialized.</li>
 * <li>Control over texture layout, meaning how inactive, active and hovered sprites are arranged in the texture source file.</li>
 * <li>Optionally draws the vanilla background texture behind the rendered sprite.</li>
 */
public class SpritelessImageButton extends Button {
    /**
     * The texture layout used in the old {@link net.minecraft.client.gui.components.ImageButton}.
     * <p>The order from top to bottom is:</p>
     * <li>Active</li>
     * <li>Hovered</li>
     * <li>Inactive</li>
     */
    public static final ToIntFunction<Button> TEXTURE_LAYOUT = (Button button) -> {
        return !button.isActive() ? 2 : button.isHoveredOrFocused() ? 1 : 0;
    };
    /**
     * The texture layout used in the old <code>textures/gui/widgets.png</code>.
     * <p>The order from top to bottom is:</p>
     * <li>Inactive</li>
     * <li>Active</li>
     * <li>Hovered</li>
     */
    public static final ToIntFunction<Button> LEGACY_TEXTURE_LAYOUT = (Button button) -> {
        return !button.isActive() ? 0 : button.isHoveredOrFocused() ? 2 : 1;
    };

    public ResourceLocation resourceLocation;
    public int xTexStart;
    public int yTexStart;
    public int yDiffTex;
    public int textureWidth;
    public int textureHeight;
    private ToIntFunction<Button> textureLayout = TEXTURE_LAYOUT;
    private boolean drawBackground;

    public SpritelessImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, ResourceLocation resourceLocation, OnPress onPress) {
        this(x, y, width, height, xTexStart, yTexStart, height, resourceLocation, 256, 256, onPress);
    }

    public SpritelessImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, OnPress onPress) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, 256, 256, onPress);
    }

    public SpritelessImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress, CommonComponents.EMPTY);
    }

    public SpritelessImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress, Component message) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
        this.resourceLocation = resourceLocation;
    }

    public SpritelessImageButton setTextureCoordinates(int xTexStart, int yTexStart) {
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        return this;
    }

    public SpritelessImageButton setTextureDimensions(int textureWidth, int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        return this;
    }

    /**
     * Controls how inactive, active and hovered sprites are arranged in the texture source file.
     *
     * @param textureLayout layout property
     *
     * @return builder instance
     */
    public SpritelessImageButton setTextureLayout(ToIntFunction<Button> textureLayout) {
        this.textureLayout = textureLayout;
        return this;
    }

    /**
     * Draws the vanilla button texture when rendering, so that sprites provided by this implementation can be drawn on top.
     * <p>This does however not include drawing the button message.
     *
     * @return builder instance
     */
    public SpritelessImageButton setDrawBackground() {
        this.drawBackground = true;
        return this;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.drawBackground) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        } else {
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
        }
        guiGraphics.blit(this.resourceLocation, this.getX(), this.getY(), this.xTexStart, this.yTexStart + this.yDiffTex * this.getTextureY(), this.width, this.height, this.textureWidth, this.textureHeight);
    }

    @Override
    public void renderString(GuiGraphics guiGraphics, Font font, int color) {
        // NO-OP
    }

    /**
     * Controls how inactive, active and hovered sprites are arranged in the texture source file.
     *
     * @return the texture y index (0, 1, or 2)
     */
    protected int getTextureY() {
        return this.textureLayout.applyAsInt(this);
    }
}
