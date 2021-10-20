package fuzs.puzzleslib.element.v2;

public enum ElementState {

    ENABLED, DISABLED, BROKEN;

    public boolean isBroken() {
        return this == BROKEN;
    }
}
