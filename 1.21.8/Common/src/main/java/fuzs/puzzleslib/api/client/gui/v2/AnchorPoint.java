package fuzs.puzzleslib.api.client.gui.v2;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.joml.Vector2i;

/**
 * A helper class for positioning gui elements inside an area, usually the whole window.
 * <p>
 * Intended to be controlled by a config option.
 */
public enum AnchorPoint {
    TOP_LEFT(-1, -1),
    TOP_CENTER(0, -1),
    TOP_RIGHT(1, -1),
    CENTER_LEFT(-1, 0),
    CENTER(0, 0),
    CENTER_RIGHT(1, 0),
    BOTTOM_LEFT(-1, 1),
    BOTTOM_CENTER(0, 1),
    BOTTOM_RIGHT(1, 1);

    private final int offsetX;
    private final int offsetY;

    AnchorPoint(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * @return is the anchor point oriented to the left
     */
    public boolean isLeft() {
        return this.offsetX == -1;
    }

    /**
     * @return is the anchor point oriented in the center
     */
    public boolean isCenter() {
        return this.offsetX == 0;
    }

    /**
     * @return is the anchor point oriented to the right
     */
    public boolean isRight() {
        return this.offsetX == 1;
    }

    /**
     * @param guiWidth      the width of the total area, likely the whole window
     * @param guiHeight     the height of the total area, likely the whole window
     * @param elementWidth  the width of the element to position
     * @param elementHeight the height of the element to position
     * @return the positioner for placing the element
     */
    public Positioner createPositioner(int guiWidth, int guiHeight, int elementWidth, int elementHeight) {
        return new PositionerImpl(this.offsetX, this.offsetY, guiWidth, guiHeight, elementWidth, elementHeight);
    }

    /**
     * The positioner for placing the element.
     */
    public interface Positioner {

        /**
         * @param posX the horizontal position oriented toward the left
         * @return the final horizontal position
         */
        int getPosX(int posX);

        /**
         * @param posY the vertical position oriented toward the top
         * @return the final vertical position
         */
        int getPosY(int posY);

        /**
         * @param posX the horizontal position oriented toward the left
         * @param posY the vertical position oriented toward the top
         * @return the final position
         */
        default Vector2i getPosition(int posX, int posY) {
            return new Vector2i(this.getPosX(posX), this.getPosY(posY));
        }

        /**
         * @param posX the horizontal position oriented toward the left
         * @param posY the vertical position oriented toward the top
         * @return the rectangle including the element dimensions
         */
        ScreenRectangle getRectangle(int posX, int posY);
    }

    private record PositionerImpl(int offsetX,
                                  int offsetY,
                                  int guiWidth,
                                  int guiHeight,
                                  int elementWidth,
                                  int elementHeight) implements Positioner {

        @Override
        public int getPosX(int posX) {
            return Math.round(this.guiWidth / 2.0F + this.offsetX * (this.guiWidth / 2.0F - posX)
                    - (this.offsetX + 1) * this.elementWidth / 2.0F);
        }

        @Override
        public int getPosY(int posY) {
            return Math.round(this.guiHeight / 2.0F + this.offsetY * (this.guiHeight / 2.0F - posY)
                    - (this.offsetY + 1) * this.elementHeight / 2.0F);
        }

        @Override
        public ScreenRectangle getRectangle(int posX, int posY) {
            return new ScreenRectangle(this.getPosX(posX), this.getPosY(posY), this.elementWidth, this.elementHeight);
        }
    }
}
