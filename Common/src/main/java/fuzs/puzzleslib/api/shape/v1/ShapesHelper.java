package fuzs.puzzleslib.api.shape.v1;

import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.Map;

/**
 * A simple helper class for {@link VoxelShape}, mainly used for applying rotations.
 */
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
        Map<Direction, VoxelShape> shapes = Maps.newEnumMap(Direction.class);
        for (Direction direction : Direction.values()) {
            shapes.put(direction, rotate(direction.getRotation(), voxelShape));
        }
        return Maps.immutableEnumMap(shapes);
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
        Map<Direction, VoxelShape> shapes = Maps.newEnumMap(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            Quaternionf rotation = getHorizontalRotation(direction);
            shapes.put(direction, rotate(rotation, voxelShape));
        }
        return Maps.immutableEnumMap(shapes);
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
        return new Quaternionf().rotationY((float) Math.atan2(direction.getStepX(), direction.getStepZ()));
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
        return rotate(rotation, voxelShape, new Vector3d(0.5, 0.5, 0.5));
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
        VoxelShape[] joinedVoxelShape = new VoxelShape[]{Shapes.empty()};
        voxelShape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            Vector3d start = rotation.transform(new Vector3d(minX, minY, minZ).sub(originOffset)).add(originOffset);
            Vector3d end = rotation.transform(new Vector3d(maxX, maxY, maxZ).sub(originOffset)).add(originOffset);
            joinedVoxelShape[0] = Shapes.or(joinedVoxelShape[0], box(start.x, start.y, start.z, end.x, end.y, end.z));
        });
        return joinedVoxelShape[0];
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
        return Shapes.box(Math.min(startX, endX), Math.min(startY, endY), Math.min(startZ, endZ),
                Math.max(startX, endX), Math.max(startY, endY), Math.max(startZ, endZ)
        );
    }
}
