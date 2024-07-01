package fuzs.puzzleslib.api.shape.v1;

import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.Collections;
import java.util.Map;

/**
 * A simple helper class for {@link VoxelShape}, mainly used for applying rotations.
 */
public class ShapesHelper {

    /**
     * Returns a map with the provided {@link VoxelShape} rotated in all six {@link Direction}s.
     * <p>Note that the provided shape is assumed to be facing {@link Direction#UP}, meaning providing {@link Direction#UP} will return the original shape.
     *
     * @param voxelShape the voxel shape to rotate to
     * @return the rotated shape map
     */
    public static Map<Direction, VoxelShape> rotate(VoxelShape voxelShape) {
        Map<Direction, VoxelShape> shapes = Maps.newEnumMap(Direction.class);
        for (Direction direction : Direction.values()) {
            shapes.put(direction, rotate(direction, voxelShape));
        }
        return Collections.unmodifiableMap(shapes);
    }

    /**
     * Rotates a provided {@link VoxelShape} depending on the provided {@link Direction}.
     * <p>Note that the provided shape is assumed to be facing {@link Direction#UP}, meaning providing {@link Direction#UP} will return the original shape.
     * <p>Also for the algorithm to work the shape must be centered on the origin, this overload assumes this shape uses the default vanilla offset which is 0.5, 0.5, 0.5.
     *
     * @param direction  the direction to rotate to
     * @param voxelShape the voxel shape to rotate to
     * @return the rotated shape
     */
    public static VoxelShape rotate(Direction direction, VoxelShape voxelShape) {
        return rotate(direction, voxelShape, new Vector3d(0.5, 0.5, 0.5));
    }

    /**
     * Rotates a provided {@link VoxelShape} depending on the provided {@link Direction}.
     * <p>Note that the provided shape is assumed to be facing {@link Direction#UP}, meaning providing {@link Direction#UP} will return the original shape.
     * <p>Also for the algorithm to work the shape must be centered on the origin, if it is not already, the current offset must be provided, which is 0.5, 0.5, 0.5 for vanilla shapes.
     *
     * @param direction    the direction to rotate to
     * @param voxelShape   the voxel shape to rotate to
     * @param originOffset offset from the origin at 0, 0, 0, usually 0.5, 0.5, 0.5
     * @return the rotated shape
     */
    public static VoxelShape rotate(Direction direction, VoxelShape voxelShape, Vector3d originOffset) {
        VoxelShape[] value = new VoxelShape[]{Shapes.empty()};
        Quaternionf rotation = direction.getRotation();
        voxelShape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            Vector3d start = rotation.transform(new Vector3d(minX, minY, minZ).sub(originOffset)).add(originOffset);
            Vector3d end = rotation.transform(new Vector3d(maxX, maxY, maxZ).sub(originOffset)).add(originOffset);
            value[0] = Shapes.or(value[0], box(start.x, start.y, start.z, end.x, end.y, end.z));
        });
        return value[0];
    }

    /**
     * Constructs a new {@link VoxelShape}, just like {@link Shapes#box(double, double, double, double, double, double)}, but in contrast properly sorts start and end coordinates automatically.
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
        return Shapes.box(Math.min(startX, endX), Math.min(startY, endY), Math.min(startZ, endZ), Math.max(startX, endX), Math.max(startY, endY), Math.max(startZ, endZ));
    }
}
