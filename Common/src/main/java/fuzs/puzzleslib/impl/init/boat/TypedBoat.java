package fuzs.puzzleslib.impl.init.boat;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

/**
 * Copied from Minecraft 26.1.
 */
public class TypedBoat extends Boat {
    private final Supplier<Item> dropItem;

    public TypedBoat(EntityType<? extends Boat> entityType, Level level, Supplier<Item> dropItem) {
        super(entityType, level);
        this.dropItem = dropItem;
    }

    @Override
    public Item getDropItem() {
        return this.dropItem.get();
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        // Boats no longer drop planks and sticks when crashed, which at this point was only ever possible due to a rare bug.
        // The remaining code for boats that caused them to be destroyed like in pre-1.9 has been removed.
        // Prior to this point, falling from very specific heights would destroy the boat and damage the rider.
        this.lastYd = this.getDeltaMovement().y;
        if (!this.isPassenger()) {
            if (onGround) {
                this.resetFallDistance();
            } else if (!this.level().getFluidState(this.blockPosition().below()).is(FluidTags.WATER) && y < 0.0) {
                this.fallDistance -= (float) y;
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.remove("Type");
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        compound.remove("Type");
        super.readAdditionalSaveData(compound);

    }

    @Override
    public void setVariant(Type variant) {
        // NO-OP
    }

    @Override
    public Type getVariant() {
        return Type.OAK;
    }
}
