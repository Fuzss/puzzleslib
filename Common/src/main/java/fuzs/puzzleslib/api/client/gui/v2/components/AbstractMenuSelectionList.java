package fuzs.puzzleslib.api.client.gui.v2.components;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import fuzs.puzzleslib.api.client.gui.v2.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/**
 * A selection list implementation that can be used as part of a screen anywhere, without having to cover the whole
 * screen width.
 * <p>
 * Also, the scroll bar is mostly handled separately and is placed outside the bounds of the actual list.
 */
public class AbstractMenuSelectionList<E extends AbstractMenuSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {
    public static final Identifier DISABLED_SCROLLER_SPRITE = Identifier.withDefaultNamespace(
            "container/creative_inventory/scroller_disabled");
    public static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace(
            "container/creative_inventory/scroller");

    public AbstractMenuSelectionList(Minecraft minecraft, int x, int y, int width, int height, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
        this.setX(x);
    }

    @Override
    public int addEntry(E entry) {
        return super.addEntry(entry);
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }

    @Override
    public int getRowWidth() {
        return this.getWidth();
    }

    @Override
    protected void extractScrollbar(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int scrollbarX = this.scrollBarX();
        int scrollerHeight = this.scrollerHeight();
        int scrollerY = this.scrollBarY();
        if (!this.scrollable()) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    DISABLED_SCROLLER_SPRITE,
                    scrollbarX,
                    this.getY(),
                    this.scrollbarWidth(),
                    scrollerHeight);
            if (this.isOverScrollbar(mouseX, mouseY)) {
                guiGraphics.requestCursor(CursorTypes.NOT_ALLOWED);
            }
        }

        if (this.scrollable()) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    SCROLLER_SPRITE,
                    scrollbarX,
                    scrollerY,
                    this.scrollbarWidth(),
                    scrollerHeight);
            if (this.isOverScrollbar(mouseX, mouseY)) {
                guiGraphics.requestCursor(this.scrolling ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
            }
        }
    }

    @Override
    protected void extractListSeparators(GuiGraphicsExtractor guiGraphics) {
        // NO-OP
    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor guiGraphics) {
        // NO-OP
    }

    @Override
    public int getFirstEntryY() {
        return this.getY();
    }

    @Override
    protected int contentHeight() {
        return super.contentHeight() - 4;
    }

    @Override
    public int scrollbarWidth() {
        return 12;
    }

    @Override
    protected int scrollerHeight() {
        return 15;
    }

    @Override
    public int scrollBarY() {
        if (!this.scrollable() || this.maxScrollAmount() == 0) {
            return this.getY();
        } else {
            double scrollScale = this.scrollAmount() / this.maxScrollAmount();
            return this.getY() + Math.max(0, (int) (scrollScale * (this.getHeight() - this.scrollerHeight())));
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        if (this.updateScrolling(mouseButtonEvent) && this.scrolling) {
            this.setMouseButtonScrollAmount(mouseButtonEvent);
            return true;
        } else {
            return super.mouseClicked(mouseButtonEvent, doubleClick);
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dragX, double dragY) {
        if (this.scrolling) {
            this.setMouseButtonScrollAmount(mouseButtonEvent);
            return true;
        } else {
            return super.mouseDragged(mouseButtonEvent, dragX, dragY);
        }
    }

    protected void setMouseButtonScrollAmount(MouseButtonEvent mouseButtonEvent) {
        double scrollOffset = (mouseButtonEvent.y() - this.getY() - this.scrollerHeight() / 2.0) / (this.getHeight()
                - this.scrollerHeight());
        this.setScrollAmount(Mth.clamp(scrollOffset, 0.0, 1.0) * this.maxScrollAmount());
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || this.isOverScrollbar(mouseX, mouseY);
    }

    @Override
    protected boolean isOverScrollbar(double mouseX, double mouseY) {
        return ScreenHelper.isHovering(this.scrollBarX(),
                this.getY(),
                this.scrollbarWidth(),
                this.getHeight(),
                mouseX,
                mouseY);
    }

    @Override
    protected void extractSelection(GuiGraphicsExtractor guiGraphics, E entry, int innerColor) {
        // NO-OP
    }

    @Override
    public int getRowLeft() {
        return this.getX();
    }

    @Override
    public int getRowTop(int index) {
        return super.getRowTop(index) - 4;
    }

    public static class Entry<E extends Entry<E>> extends ContainerObjectSelectionList.Entry<E> {
        private final List<AbstractWidget> children = new ArrayList<>();

        @Override
        public int getContentX() {
            return this.getX();
        }

        @Override
        public int getContentY() {
            return this.getY();
        }

        @Override
        public int getContentHeight() {
            return this.getHeight();
        }

        @Override
        public int getContentWidth() {
            return this.getWidth();
        }

        public <T extends AbstractWidget> T addRenderableWidget(T widget) {
            this.children.add(widget);
            return widget;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            for (AbstractWidget abstractWidget : this.children) {
                abstractWidget.setY(this.getContentY());
                abstractWidget.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }
}
