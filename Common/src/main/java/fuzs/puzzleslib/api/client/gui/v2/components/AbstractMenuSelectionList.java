package fuzs.puzzleslib.api.client.gui.v2.components;

import fuzs.puzzleslib.api.client.gui.v2.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/**
 * A selection list implementation that can be used as part of a screen anywhere, without having to cover the whole
 * screen width.
 * <p>
 * Also, the scroll bar is mostly handled separately and is placed outside the bounds of the actual list.
 * <p>
 * There is no need to handle
 * {@link net.minecraft.client.gui.components.AbstractSelectionList#renderSelection(GuiGraphics, int, int, int, int,
 * int)} as that is already bypassed in {@link ContainerObjectSelectionList#isSelectedItem(int)}.
 */
public abstract class AbstractMenuSelectionList<E extends ContainerObjectSelectionList.Entry<E>> extends UpdatedContainerObjectSelectionList<E> {
    public static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace(
            "container/creative_inventory/scroller");
    public static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace(
            "container/creative_inventory/scroller_disabled");

    public AbstractMenuSelectionList(Minecraft minecraft, int x, int y, int width, int height, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
        this.setX(x);
    }

    @Override
    public int addEntry(E entry) {
        return super.addEntry(entry);
    }

    @Override
    public int getRowWidth() {
        return this.getWidth();
    }

    @Override
    protected void extractScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        ResourceLocation sprite = this.scrollable() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
        guiGraphics.blitSprite(sprite,
                this.scrollBarX(),
                this.scrollBarY(),
                this.scrollerWidth(),
                this.scrollerHeight());
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
    protected int contentHeight() {
        return super.contentHeight() - 4;
    }

    @Override
    public int scrollbarWidth() {
        return this.scrollerWidth();
    }

    public int scrollbarHeight() {
        return this.getHeight();
    }

    @Override
    protected int scrollerWidth() {
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.updateScrolling(mouseX, mouseY, button) && this.scrolling) {
            this.setMouseButtonScrollAmount(mouseX, mouseY, button);
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling) {
            this.setMouseButtonScrollAmount(mouseX, mouseY, button);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    protected void setMouseButtonScrollAmount(double mouseX, double mouseY, int button) {
        double scrollOffset =
                (mouseY - this.getY() - this.scrollerHeight() / 2.0) / (this.scrollbarHeight() - this.scrollerHeight());
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
                this.scrollbarHeight(),
                mouseX,
                mouseY);
    }

    @Override
    public int getRowLeft() {
        return this.getX();
    }

    @Override
    public E getEntryAtPosition(double mouseX, double mouseY) {
        // A workaround for getting around vanilla subtracting a height of 4.
        // This avoids having to copy the whole method.
        this.headerHeight -= 4;
        E entry = super.getEntryAtPosition(mouseX, mouseY);
        this.headerHeight += 4;
        return entry;
    }

    @Override
    protected int getRowTop(int index) {
        return super.getRowTop(index) - 4;
    }

    public static class Entry<E extends Entry<E>> extends ContainerObjectSelectionList.Entry<E> {
        private final List<AbstractWidget> children = new ArrayList<>();

        public <T extends AbstractWidget> T addRenderableWidget(T widget) {
            this.children.add(widget);
            return widget;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            for (AbstractWidget widget : this.children) {
                widget.setY(top);
                widget.render(guiGraphics, mouseX, mouseY, partialTick);
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
