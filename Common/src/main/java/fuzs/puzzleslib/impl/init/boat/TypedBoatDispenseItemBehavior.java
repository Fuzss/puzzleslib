package fuzs.puzzleslib.impl.init.boat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

/**
 * Copied from Minecraft 26.1.
 */
public class TypedBoatDispenseItemBehavior extends DefaultDispenseItemBehavior {
    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
    private final EntityType<? extends Boat> type;

    public TypedBoatDispenseItemBehavior(EntityType<? extends Boat> type) {
        this.type = type;
    }

    @Override
    public ItemStack execute(BlockSource source, ItemStack dispensed) {
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        ServerLevel level = source.level();
        Vec3 center = source.center();
        double justOutsideDispenser = (double) 0.5625F + (double) this.type.getWidth() / (double) 2.0F;
        double spawnX = center.x() + (double) direction.getStepX() * justOutsideDispenser;
        double spawnY = center.y() + (double) ((float) direction.getStepY() * 1.125F);
        double spawnZ = center.z() + (double) direction.getStepZ() * justOutsideDispenser;
        BlockPos frontPos = source.pos().relative(direction);
        double yOffset;
        if (level.getFluidState(frontPos).is(FluidTags.WATER)) {
            yOffset = 1.0F;
        } else {
            if (!level.getBlockState(frontPos).isAir() || !level.getFluidState(frontPos.below()).is(FluidTags.WATER)) {
                return this.defaultDispenseItemBehavior.dispense(source, dispensed);
            }

            yOffset = 0.0F;
        }

        Boat boat = this.type.create(level);
        if (boat != null) {
            boat.absMoveTo(spawnX, spawnY + yOffset, spawnZ);
            EntityType.createDefaultStackConfig(level, dispensed, null).accept(boat);
            boat.setYRot(direction.toYRot());
            level.addFreshEntity(boat);
            dispensed.shrink(1);
        }

        return dispensed;
    }

    @Override
    protected void playSound(BlockSource source) {
        source.level().levelEvent(1000, source.pos(), 0);
    }
}
