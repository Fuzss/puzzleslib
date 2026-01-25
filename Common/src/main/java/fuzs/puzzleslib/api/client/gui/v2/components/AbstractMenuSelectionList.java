package fuzs.puzzleslib.api.client.gui.v2.components;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import fuzs.puzzleslib.api.client.gui.v2.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.WidgetSprites;
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
    public static final WidgetSprites SCROLLER_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace(
            "container/creative_inventory/scroller"),
            Identifier.withDefaultNamespace("container/creative_inventory/scroller_disabled"),
            Identifier.withDefaultNamespace("container/creative_inventory/scroller"));
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;

    private final int scrollbarOffset;

    public AbstractMenuSelectionList(Minecraft minecraft, int x, int y, int width, int height, int itemHeight, int scrollbarOffset) {
        super(minecraft, width, height, y, itemHeight);
        this.scrollbarOffset = scrollbarOffset;
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
    protected void renderScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.scrollbarVisible()) {
            int posX = this.scrollBarX();
            double scrollAmount = this.scrollbarUsable() ? this.scrollAmount() / this.maxScrollAmount() : 0;
            int posY = this.getY() + (int) (scrollAmount * (this.getHeight() - this.scrollerHeight()));
            Identifier identifier = SCROLLER_SPRITES.get(this.scrollbarUsable(), false);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    identifier,
                    posX,
                    posY,
                    this.scrollerWidth(),
                    this.scrollerHeight());
            if (this.isOverScrollbar(mouseX, mouseY)) {
                guiGraphics.requestCursor(this.requestedCursorType());
            }
        }
    }

    protected CursorType requestedCursorType() {
        if (this.scrollbarUsable()) {
            return this.scrolling ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND;
        } else {
            return CursorTypes.NOT_ALLOWED;
        }
    }

    @Override
    protected boolean scrollbarVisible() {
        return true;
    }

    protected boolean scrollbarUsable() {
        return this.maxScrollAmount() > 0;
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
        // NO-OP
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
        // NO-OP
    }

    @Override
    protected int getFirstEntryY() {
        return this.getY();
    }

    @Override
    protected int contentHeight() {
        return super.contentHeight() - 4;
    }

    protected int scrollerWidth() {
        return SCROLLER_WIDTH;
    }

    @Override
    protected int scrollerHeight() {
        return SCROLLER_HEIGHT;
    }

    @Override
    protected int scrollBarX() {
        return this.getRowRight() + this.scrollbarOffset;
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
                this.scrollerWidth(),
                this.getHeight(),
                mouseX,
                mouseY);
    }

    @Override
    protected void renderSelection(GuiGraphics guiGraphics, E entry, int innerColor) {
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
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            for (AbstractWidget abstractWidget : this.children) {
                abstractWidget.setY(this.getContentY());
                abstractWidget.render(guiGraphics, mouseX, mouseY, partialTick);
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
