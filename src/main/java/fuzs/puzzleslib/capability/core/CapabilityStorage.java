package fuzs.puzzleslib.capability.core;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * serializer for capability, has to be provided when registering
 * @param <T> capability class
 */
public class CapabilityStorage<T> implements Capability.IStorage<T> {

    @Override
    public INBT writeNBT(Capability<T> capability, T instance, Direction side) {

        if (instance instanceof INBTSerializable) {

            return ((INBTSerializable<?>) instance).serializeNBT();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {

        if (instance instanceof INBTSerializable && nbt instanceof CompoundNBT) {

            ((INBTSerializable<CompoundNBT>) instance).deserializeNBT((CompoundNBT) nbt);
        }
    }

}