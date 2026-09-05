package fuzs.puzzleslib.api.client.gui.v2.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

/**
 * Backported from Minecraft 26.2.
 */
public abstract class UpdatedContainerObjectSelectionList<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {

    public UpdatedContainerObjectSelectionList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    @Override
    protected final void renderDecorations(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.extractScrollbar(guiGraphics, mouseX, mouseY);
    }

    protected abstract void extractScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY);

    @Override
    protected final boolean scrollbarVisible() {
        return false;
    }

    @Override
    protected final void updateScrollingState(double mouseX, double mouseY, int button) {
        this.updateScrolling(mouseX, mouseY, button);
    }

    public boolean updateScrolling(double mouseX, double mouseY, int button) {
        this.scrolling = this.scrollable() && this.isValidClickButton(button) && this.isOverScrollbar(mouseX, mouseY);
        return this.scrolling;
    }

    protected boolean isOverScrollbar(double mouseX, double mouseY) {
        return mouseX >= this.getScrollbarPosition() && mouseX < (this.getScrollbarPosition() + 6);
    }

    @Override
    public final int getMaxScroll() {
        return this.maxScrollAmount();
    }

    public int maxScrollAmount() {
        return Math.max(0, this.contentHeight() - this.height);
    }

    protected boolean scrollable() {
        return this.maxScrollAmount() > 0;
    }

    @Override
    protected final int getMaxPosition() {
        return this.contentHeight();
    }

    protected int contentHeight() {
        return super.getMaxPosition() + 4;
    }

    @Override
    protected final int getScrollbarPosition() {
        return this.scrollBarX();
    }

    protected int scrollBarX() {
        return super.getScrollbarPosition();
    }

    /**
     * @see net.minecraft.client.gui.components.AbstractSelectionList#renderWidget(GuiGraphics, int, int, float)
     */
    public int scrollBarY() {
        return this.maxScrollAmount() == 0 ? this.getY() : Math.max(this.getY(),
                (int) this.scrollAmount() * (this.height - this.scrollerHeight()) / this.maxScrollAmount()
                        + this.getY());
    }

    public abstract int scrollbarWidth();

    protected abstract int scrollerWidth();

    protected abstract int scrollerHeight();

    @Override
    public final double getScrollAmount() {
        return this.scrollAmount();
    }

    public double scrollAmount() {
        return super.getScrollAmount();
    }
}
