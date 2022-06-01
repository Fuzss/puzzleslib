package fuzs.puzzleslib.capability.test;

import net.minecraft.nbt.CompoundTag;

public class AirHopsCapabilityImpl implements AirHopsCapability {
    private int airHops = 0;

    @Override
    public int getAirHops() {
        return this.airHops;
    }

    @Override
    public void setAirHops(int amount) {
        this.airHops = amount;
    }

    @Override
    public void write(CompoundTag tag) {
        tag.putByte("AirHops", (byte) this.getAirHops());
    }

    @Override
    public void read(CompoundTag tag) {
        this.setAirHops(tag.getByte("AirHops"));
    }
}
