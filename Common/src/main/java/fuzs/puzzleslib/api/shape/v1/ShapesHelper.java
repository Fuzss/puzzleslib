package fuzs.puzzleslib.api.shape.v1;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.Map;

/**
 * A simple helper class for {@link VoxelShape}, mainly used for applying rotations.
 */
@Deprecated
public final class ShapesHelper {

    private ShapesHelper() {
        // NO-OP
    }

    /**
     * Returns a map with the provided {@link VoxelShape} rotated in all six {@link Direction Directions}.
     * <p>
     * Note that the provided shape is assumed to be facing {@link Direction#UP}, meaning providing {@link Direction#UP}
     * will return the original shape.
     *
     * @param voxelShape the voxel shape to rotate
     * @return the rotated shapes
     */
    public static Map<Direction, VoxelShape> rotate(VoxelShape voxelShape) {
        return fuzs.puzzleslib.api.util.v1.ShapesHelper.rotate(voxelShape);
    }

    /**
     * Returns a map with the provided {@link VoxelShape} rotated in all four horizontal {@link Direction Directions}.
     * <p>
     * Note that the provided shape is assumed to be facing {@link Direction#SOUTH}, meaning providing
     * {@link Direction#SOUTH} will return the original shape.
     *
     * @param voxelShape the voxel shape to rotate
     * @return the rotated shapes
     */
    public static Map<Direction, VoxelShape> rotateHorizontally(VoxelShape voxelShape) {
        return fuzs.puzzleslib.api.util.v1.ShapesHelper.rotateHorizontally(voxelShape);
    }

    /**
     * Computes a quaternion for a horizontal direction.
     * <p>
     * Providing {@link Direction#SOUTH} will return an identity.
     *
     * @param direction the direction
     * @return the quaternion
     */
    public static Quaternionf getHorizontalRotation(Direction direction) {
        return fuzs.puzzleslib.api.util.v1.ShapesHelper.getHorizontalRotation(direction);
    }

    @Deprecated(forRemoval = true)
    public static VoxelShape rotate(Direction direction, VoxelShape voxelShape) {
        return rotate(direction.getRotation(), voxelShape);
    }

    @Deprecated(forRemoval = true)
    public static VoxelShape rotate(Direction direction, VoxelShape voxelShape, Vector3d originOffset) {
        return rotate(direction.getRotation(), voxelShape, originOffset);
    }

    /**
     * Rotates a provided {@link VoxelShape} depending on the provided {@link Quaternionf}.
     * <p>
     * The implementation assumes the provided shape to be offset from the origin by {@code x=0.5}, {@code y=0.5},
     * {@code z=0.5}. This is the case for all block shapes.
     *
     * @param rotation   the rotation to apply
     * @param voxelShape the voxel shape to rotate to
     * @return the rotated shape
     */
    public static VoxelShape rotate(Quaternionf rotation, VoxelShape voxelShape) {
        return fuzs.puzzleslib.api.util.v1.ShapesHelper.rotate(rotation, voxelShape);
    }

    /**
     * Rotates a provided {@link VoxelShape} depending on the provided {@link Quaternionf}.
     * <p>
     * Also for the algorithm to work the shape must be centered on the origin, if it is not already, the current offset
     * must be provided, which is {@code x=0.5}, {@code y=0.5}, {@code z=0.5} for block shapes.
     *
     * @param rotation     the rotation to apply
     * @param voxelShape   the voxel shape to rotate to
     * @param originOffset offset from the origin at {@code x=0.0}, {@code y=0.0}, {@code z=0.0}
     * @return the rotated shape
     */
    public static VoxelShape rotate(Quaternionf rotation, VoxelShape voxelShape, Vector3d originOffset) {
        return fuzs.puzzleslib.api.util.v1.ShapesHelper.rotate(rotation, voxelShape, originOffset);
    }

    /**
     * Constructs a new {@link VoxelShape}, just like
     * {@link Shapes#box(double, double, double, double, double, double)}, but in contrast properly sorts start and end
     * coordinates automatically.
     * <p>
     * Values should ideally range from {@code 0.0} to {@code 1.0}.
     *
     * @param startX start x coordinate
     * @param startY start y coordinate
     * @param startZ start z coordinate
     * @param endX   end x coordinate
     * @param endY   end y coordinate
     * @param endZ   end z coordinate
     * @return the new shape
     */
    public static VoxelShape box(double startX, double startY, double startZ, double endX, double endY, double endZ) {
        return fuzs.puzzleslib.api.util.v1.ShapesHelper.box(startX, startY, startZ, endX, endY, endZ);
    }
}
