package fuzs.puzzleslib.capability.test;

import fuzs.puzzleslib.capability.data.CapabilityComponent;

public interface AirHopsCapability extends CapabilityComponent {
    int getAirHops();

    void setAirHops(int amount);

    default void resetAirHops() {
        this.setAirHops(0);
    }

    default void addAirHop() {
        this.setAirHops(this.getAirHops() + 1);
    }

    default boolean hasUsedAirHops() {
        return this.getAirHops() > 0;
    }
}
