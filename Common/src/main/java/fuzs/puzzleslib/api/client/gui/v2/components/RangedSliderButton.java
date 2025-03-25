package fuzs.puzzleslib.api.client.gui.v2.components;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * An extension to {@link AbstractSliderButton} that supports values within any range, therefore requiring minimum and
 * maximum bounds to be specified.
 */
@Deprecated(forRemoval = true)
public abstract class RangedSliderButton extends AbstractSliderButton {
    /**
     * Lower value bound.
     */
    protected final double minValue;
    /**
     * Upper value bound.
     */
    protected final double maxValue;

    /**
     * @param x        x position on the screen
     * @param y        y position on the screen
     * @param width    slider width
     * @param height   slider height
     * @param value    initial slider value
     * @param minValue lower value bound, used for scaling the value
     * @param maxValue upper value bound, used for scaling the value
     */
    public RangedSliderButton(int x, int y, int width, int height, double value, double minValue, double maxValue) {
        super(x, y, width, height, CommonComponents.EMPTY, 0.0);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.setScaledValue(value);
    }

    /**
     * Gets the value adjusted for the full range of the slider.
     *
     * @return current value
     */
    public double getScaledValue() {
        return this.getValue() * (this.maxValue - this.minValue) + this.minValue;
    }

    /**
     * Sets a new unscaled value, which is then scaled down to a percentage internally.
     *
     * @param value new value to set
     */
    public void setScaledValue(double value) {
        this.setValue((value - this.minValue) / (this.maxValue - this.minValue));
    }

    /**
     * Gets the raw value for this slider (as a percentage), always between <code>0.0</code> and <code>1.0</code>.
     *
     * @return current value
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Sets the raw value for this slider (as a percentage), must be between <code>0.0</code> and <code>1.0</code>.
     * <p>
     * Copied from super class.
     *
     * @param value new value to set
     */
    private void setValue(double value) {
        double oldValue = this.value;
        this.value = Mth.clamp(value, 0.0F, 1.0F);
        if (oldValue != this.value) {
            this.applyValue();
        }

        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(this.getMessageFromValue(this.getScaledValue()));
    }

    @Override
    protected void applyValue() {
        this.applyValue(this.getScaledValue());
    }

    /**
     * Update the slider message after the value has changed.
     * <p>
     * Like {@link #updateMessage()}, but directly sets the new message.
     *
     * @param value new value after it has changed, obtained from {@link #getValue()}
     * @return the new component to set to {@link #setMessage(Component)}
     */
    protected abstract Component getMessageFromValue(double value);

    /**
     * Apply the value after it has changed to wherever it is being tracked.
     * <p>
     * Like {@link #applyValue()}, but with additional parameter.
     *
     * @param value new value after it has changed, obtained from {@link #getValue()}
     */
    protected abstract void applyValue(double value);
}
