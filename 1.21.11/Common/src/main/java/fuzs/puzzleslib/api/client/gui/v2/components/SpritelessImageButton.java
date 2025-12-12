package fuzs.puzzleslib.api.client.gui.v2.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.util.function.ToIntFunction;

/**
 * A button implementation very similar to the old {@link net.minecraft.client.gui.components.ImageButton} with a couple
 * extra features:
 * <ul>
 * <li>No need for using {@link WidgetSprites}.</li>
 * <li>Completely mutable to allow for manipulating texture coordinates, size and position after the button has been
 * initialised.</li>
 * <li>Control over texture layout, meaning how inactive, active, and hovered sprites are arranged in the texture source
 * file.</li>
 * <li>Optionally draws the vanilla background texture behind the rendered sprite.</li>
 * </ul>
 */
public class SpritelessImageButton extends Button {
    /**
     * The texture layout used in the old {@link net.minecraft.client.gui.components.ImageButton}.
     * <p>
     * The order from top to bottom is:
     * <ul>
     * <li>Active</li>
     * <li>Hovered</li>
     * <li>Inactive</li>
     * </ul>
     */
    public static final ToIntFunction<Button> TEXTURE_LAYOUT = (Button button) -> {
        return !button.isActive() ? 2 : button.isHoveredOrFocused() ? 1 : 0;
    };
    /**
     * The texture layout used in the old <code>textures/gui/widgets.png</code>.
     * <p>
     * The order from top to bottom is:
     * <ul>
     * <li>Inactive</li>
     * <li>Active</li>
     * <li>Hovered</li>
     * </ul>
     */
    public static final ToIntFunction<Button> LEGACY_TEXTURE_LAYOUT = (Button button) -> {
        return !button.isActive() ? 0 : button.isHoveredOrFocused() ? 2 : 1;
    };
    /**
     * A texture layout that always returns the same texture index.
     */
    public static final ToIntFunction<Button> SINGLE_TEXTURE_LAYOUT = (Button button) -> {
        return 0;
    };
    /**
     * The texture layout used in the old {@link net.minecraft.client.gui.components.ImageButton} for buttons that are
     * always active.
     * <p>
     * The order from top to bottom is:
     * <ul>
     * <li>Active / Inactive</li>
     * <li>Hovered</li>
     * </ul>
     */
    public static final ToIntFunction<Button> ALWAYS_ACTIVE_TEXTURE_LAYOUT = (Button button) -> {
        return button.isHoveredOrFocused() ? 1 : 0;
    };

    public Identifier identifier;
    public int xTexStart;
    public int yTexStart;
    public int yDiffTex;
    public int textureWidth;
    public int textureHeight;
    private ToIntFunction<Button> textureLayout = TEXTURE_LAYOUT;
    private boolean drawBackground;

    public SpritelessImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, Identifier identifier, OnPress onPress) {
        this(x, y, width, height, xTexStart, yTexStart, height, identifier, onPress);
    }

    public SpritelessImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, Identifier identifier, OnPress onPress) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, identifier, 256, 256, onPress);
    }

    public SpritelessImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, Identifier identifier, int textureWidth, int textureHeight, OnPress onPress) {
        this(x,
                y,
                width,
                height,
                xTexStart,
                yTexStart,
                yDiffTex,
                identifier,
                textureWidth,
                textureHeight,
                onPress,
                CommonComponents.EMPTY);
    }

    public SpritelessImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, Identifier identifier, int textureWidth, int textureHeight, OnPress onPress, Component message) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
        this.identifier = identifier;
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
     * Controls how inactive, active, and hovered sprites are arranged in the texture source file.
     *
     * @param textureLayout layout property
     * @return builder instance
     */
    public SpritelessImageButton setTextureLayout(ToIntFunction<Button> textureLayout) {
        this.textureLayout = textureLayout;
        return this;
    }

    /**
     * Draws the vanilla button texture when rendering so that sprites provided by this implementation can be drawn on
     * top.
     * <p>
     * This does, however, not include drawing the button message.
     *
     * @return builder instance
     */
    public SpritelessImageButton setDrawBackground() {
        this.drawBackground = true;
        return this;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int i, int j, float f) {
        if (this.drawBackground) {
            this.renderDefaultSprite(guiGraphics);
        }

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                this.identifier,
                this.getX(),
                this.getY(),
                this.xTexStart,
                this.yTexStart + this.yDiffTex * this.getTextureY(),
                this.width,
                this.height,
                this.textureWidth,
                this.textureHeight,
                ARGB.white(this.alpha));
    }

    /**
     * Controls how inactive, active, and hovered sprites are arranged in the texture source file.
     *
     * @return the texture y index (0, 1, or 2)
     */
    protected int getTextureY() {
        return this.textureLayout.applyAsInt(this);
    }
}
