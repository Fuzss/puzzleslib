package fuzs.puzzleslib.api.client.gui.v2.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.ToIntFunction;

/**
 * A button implementation very similar to the old {@link net.minecraft.client.gui.components.ImageButton},
 * without the need for using {@link WidgetSprites}.
 */
public class SpritelessImageButton extends Button {
    public static final ToIntFunction<Button> TEXTURE_LAYOUT = (Button button) -> {
        return !button.isActive() ? 2 : button.isHoveredOrFocused() ? 1 : 0;
    };
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

    public SpritelessImageButton setTextureLayout(ToIntFunction<Button> textureLayout) {
        this.textureLayout = textureLayout;
        return this;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableDepthTest();
        guiGraphics.blit(this.resourceLocation, this.getX(), this.getY(), this.xTexStart, this.yTexStart + this.yDiffTex * this.getTextureY(), this.width, this.height, this.textureWidth, this.textureHeight);
    }

    protected int getTextureY() {
        return this.textureLayout.applyAsInt(this);
    }
}
