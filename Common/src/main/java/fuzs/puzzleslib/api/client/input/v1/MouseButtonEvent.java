package fuzs.puzzleslib.api.client.input.v1;

/**
 * Copied from Minecraft 26.2.
 */
public record MouseButtonEvent(double x, double y, MouseButtonInfo buttonInfo) implements InputWithModifiers {
    @Override
    public int input() {
        return this.button();
    }

    public int button() {
        return this.buttonInfo().button();
    }

    @Override
    public int modifiers() {
        return this.buttonInfo().modifiers();
    }
}
